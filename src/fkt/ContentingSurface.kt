package fkt
import fkt.facets.IndexingFramePolicy
import fkt.facets.TTarget
import fkt.facets.TargetCoupler
import fkt.facets.TextualCoupler
import fkt.SelectingTitles as Titles
import fkt.SimpleTitles as Simples
open class ContentingSurface(trace:Boolean)
		:SelectingSurface(TargetTest.Contenting,trace) {
  private lateinit var active:TextContent
  private lateinit var edit:TextContent
	override fun getContentTrees(): Any {
    return arrayOf(
    		newContentTree(list[0]),
    		newContentTree(list[2]),
    		facets.newIndexingFrame(object: IndexingFramePolicy() {
    			override val frameTitle = Titles.Chooser
					override val indexingTitle = Titles.Select
					override val getIndexables = { list.toTypedArray() }
    			override val newUiSelectable = { indexable:Any-> 
    				(indexable as TextContent).text 
    			}
    			override val newFrameTargets = {
  					arrayOf(facets.newTriggerTarget(Titles.OpenEdit, 
    							object: TextualCoupler() {
						override val targetStateUpdated = {_:Any, _:String->
              active = facets.getIndexingState(Titles.Select).indexed as TextContent
          		edit = active.clone()
              facets.addContentTree(newContentTree(edit))
           }
					}
				))
			}
    }))
  }
  private fun newContentTree(content:TextContent): TTarget {
    val type = SelectableType.getContentType(content)
    val tail = type.titleTail()
    val members = ArrayList<TTarget>()
    members.add(newEditTarget(content, tail))
    if (type === SelectableType.ShowChars) members.add(newCharsTarget(tail))
    members.add(facets.newTriggerTarget(Titles.Save + tail, object: TargetCoupler() {
      override val targetStateUpdated = { _:Any, _:String->
	      active.copyClone(edit)
	      activateChooser()
      }
    }))
    members.add(facets.newTriggerTarget(Titles.Cancel + tail, object: TargetCoupler() {
    	override val targetStateUpdated = {_:Any, _:String-> 
    		activateChooser()
    	}
    }))
    return facets.newTargetGroup(type.title(), members.toTypedArray())
  }
  private fun activateChooser() =
    facets.activateContentTree(Titles.Chooser)

	override fun onRetargeted(activeTitle:String) {}
  override fun buildLayout() {
    generateFacets(Titles.Select, Titles.EditText, Titles.EditText + Titles.CharsTail,
                   Titles.CharsCount + Titles.CharsTail)
  }
}