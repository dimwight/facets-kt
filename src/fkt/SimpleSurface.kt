package fkt
import fkt.IndexingCoupler
import fkt.NumericCoupler
import fkt.TargetCoupler
import fkt.TextualCoupler
import fkt.TogglingCoupler
import fkt.core.STarget
import fkt.SimpleTitles as Simples
open class SimpleSurface(test:TargetTest,trace:Boolean):SurfaceCore(newFacets(trace),test){
	override fun getContentTrees():Any {
    trace(" > Generating targets")
    val treeTitle = test.toString() + " Test"
    val members = if (test === TargetTest.Textual)
    arrayOf(newTextual(
      Simples.MasterTextual), newTextual(Simples.SlaveTextual))
    else if (test === TargetTest.TogglingLive)
    arrayOf(newToggling(Simples.Toggling, Simples.StartToggled), newTextual(Simples.Toggled))
    else if (test === TargetTest.Indexing)
    arrayOf(newIndexing(Simples.Indexing, arrayOf(Simples.MasterTextual, Simples.SlaveTextual),
                                 Simples.StartIndex), newTextual(Simples.Index), newTextual(Simples.Indexed))
    else if (test === TargetTest.Numeric)
    arrayOf(newNumeric(
      Simples.NumericField), newTextual(Simples.NumericValue))
    else
    arrayOf(newTrigger(Simples.Trigger), newTextual(Simples.Triggerings))
    return facets.newTargetGroup(treeTitle, members)
  }
  override protected fun doTraceMsg(msg:String) {
    if (true && facets.doTrace) super.doTraceMsg(msg)
  }
  protected fun newTrigger(title:String):STarget {
    return facets.newTriggerTarget(title, object:TargetCoupler(){
      override val targetStateUpdated= { _:Any, title:String->
          trace(" > Trigger fired: title=" + title)
          val got:String? = facets.getTargetState(Simples.Triggerings) as String
          if (got != null){
            val valueOf = (Integer.valueOf(got) + 1).toString()
            facets.updateTargetState(Simples.Triggerings, valueOf)
          }
			 }
     })
  }
  protected fun newTextual(title:String):STarget {
    val coupler = newTextualCouplerCore(title)
    val passText = coupler.passText
    trace(" > Generating textual target state=",
      if (passText != null) passText else coupler.getText?.invoke(title))
    return facets.newTextualTarget(title, coupler)
  }
  protected fun newNumeric(title:String):STarget {
    val coupler = object:NumericCoupler() {
        override val passValue = Simples.StartNumber
        override val min = 5.0
        override val max = 25.0
    }
    trace(" > Generating numeric target state=", coupler.passValue)
    return facets.newNumericTarget(title, coupler)
  }
  protected fun newToggling(title:String, state:Boolean):STarget {
    trace(" > Generating toggling target state=", state)
    val coupler = object:TogglingCoupler() {
      override val passSet = state
      override val targetStateUpdated = {
				state:Any, title:String->
        trace(" > Toggling state updated: title=" + title + " state=", state)
        facets.setTargetLive(Simples.Toggled, state as Boolean)
			}
    }
    return facets.newTogglingTarget(title, coupler)
  }
  protected fun newIndexing(title:String, indexables:Array<out String>, indexStart:Int):STarget {
    trace(" > Generating indexing target state=", indexStart)
    val coupler = object:IndexingCoupler() {
    		override val targetStateUpdated=null
        override val getIndexables = { _:String-> indexables }
        override val newUiSelectable = { indexable:Any-> indexable as String }
        override val passIndex = indexStart
    }
    return facets.newIndexingTarget(title, coupler)
  }
  protected fun newTextualCouplerCore(title:String):TextualCoupler {
    val textTextual = title + " text in " + this.title()
    return when (title){
			Simples.NumericValue->object:TextualCoupler() {
      override val getText = {_:String->
       val state = facets.getTargetState(Simples.NumericField)
       ("Number is " + (if (state != null) Math.rint(state as Double) else " not yet set")).replace(("\\.\\d+").toRegex(), "")
			}
    }
			Simples.Toggled->object:TextualCoupler() {
      override val getText = { _:String->
				"Set to " + facets.getTargetState(Simples.Toggling)
			 }
    }
			Simples.Indexed->object:TextualCoupler() {
        override val getText = { _:String->
					if (facets.getTargetState(Simples.Indexing) == null)
           ("No data yet for " + Simples.Indexing)
           else
           facets.getIndexingState(Simples.Indexing).indexed as String
				}
    }
      Simples.Index->object:TextualCoupler() {
        override val getText = { _:String->
           val state = facets.getTargetState(Simples.Indexing)
           if (state == null) ("No data yet for " + Simples.Indexing) else (state).toString()
			}
    }
      Simples.MasterTextual->object:TextualCoupler() {
        override val getText = { _:String-> textTextual }
        override val targetStateUpdated = { state:Any, title:String->
          trace(" > Textual state updated: title=" + title + " state=", state)
          facets.updateTargetState(Simples.SlaveTextual,
                                   Simples.MasterTextual + " has changed to: " + state)
				}
    }
      Simples.Triggerings->object:TextualCoupler() {
        override val passText = "0"
        override val targetStateUpdated = { state:Any, _:String->
					 if (Integer.valueOf(state as String) > 4)
						 facets.setTargetLive(Simples.Trigger, false)
				 }
    }
       else->object:TextualCoupler() {
        override val passText = textTextual
       }
		}
  }
  override fun onRetargeted(activeTitle:String) {}
  override fun buildLayout() {
    if (test === TargetTest.Textual)
    generateFacets(Simples.MasterTextual)
    else if (test === TargetTest.TogglingLive)
    generateFacets(Simples.Toggling, Simples.Toggled)
    else if (test === TargetTest.Numeric)
    generateFacets(Simples.NumericField, Simples.NumericValue)
    else if (test === TargetTest.Trigger)
    generateFacets(Simples.Trigger, Simples.Triggerings)
    else
    generateFacets(Simples.Indexing, Simples.Index, Simples.Indexed)
  }
  override fun buildSurface() {
    super.buildSurface()
    if (false) return
    val update:Any = when (test){
			TargetTest.TogglingLive->!Simples.StartToggled
			TargetTest.Indexing->(Simples.StartIndex + 1) % 2
			TargetTest.Numeric->Simples.StartNumber * 2
			else ->"Some updated text"
		}
    trace(" > Simulating input: update=", update)
    val title = if (test === TargetTest.Indexing)
    Simples.Indexing
    else if (test === TargetTest.TogglingLive)
    Simples.Toggling
    else if (test === TargetTest.Numeric)
    Simples.NumericField
    else if (test === TargetTest.Trigger)
    Simples.Trigger
    else
    Simples.MasterTextual
    facets.updateTargetState(title, update)
  }
}