package fkt.app

import fkt.facets.IndexingCoupler
import fkt.facets.NumericCoupler
import fkt.facets.TTarget
import fkt.facets.TargetCoupler
import fkt.facets.TextualCoupler
import fkt.facets.TogglingCoupler
import fkt.app.SimpleTitles as Titles

object SimpleTitles {
  const val MasterTextual = "Master"
  const val SlaveTextual = "Slave"
  const val Indexing = "Pick One"
  const val Index = "Pick Value"
  const val Indexed = "Picked"
  const val Toggling = "Click to toggle live"
  const val Toggled = "Toggle state"
  const val NumericField = "Number"
  const val NumericValue = "Value"
  const val Trigger = "Click Me!"
  const val Triggerings = "Click Count"
  const val StartIndex:Int = 0
  const val StartToggled = false
  const val StartNumber = 123.0
}

open class SimpleApp(test: TargetTest, trace: Boolean) : AppCore(trace, test) {
  override fun newContentTrees(): List<TTarget> {
    trace(" > Generating targets")
    return listOf(facets.newTargetGroup(title ="${test.toString()} Test", members = when (test) {
      TargetTest.Textual -> listOf(
        newTextual(Titles.MasterTextual),
        newTextual(Titles.SlaveTextual)
      )
      TargetTest.TogglingLive -> listOf(
        newToggling(Titles.Toggling, Titles.StartToggled),
        newTextual(Titles.Toggled)
      )
      TargetTest.Indexing -> listOf(
        newIndexing(Titles.Indexing, listOf(
          Titles.MasterTextual,
          Titles.SlaveTextual
        ),
          Titles.StartIndex),
        newTextual(Titles.Index),
        newTextual(Titles.Indexed)
      )
      TargetTest.Numeric -> listOf(
        newNumeric(Titles.NumericField),
        newTextual(Titles.NumericValue)
      )
      else -> listOf(
        newTrigger(Titles.Trigger),
        newTextual(Titles.Triggerings)
      )
    }))
  }

  override fun doTraceMsg(msg: String) {
    if (true && facets.doTrace) super.doTraceMsg(msg)
  }

  private fun newTrigger(title: String): TTarget {
    return facets.newTriggerTarget(title, object : TargetCoupler() {
      override val targetStateUpdated = { _: Any, title: String ->
        trace(" > Trigger fired: title=$title")
        val got: String? = facets.getTargetState(Titles.Triggerings) as String
        if (got != null) {
          val valueOf = (Integer.valueOf(got) + 1).toString()
          facets.updateTargetState(Titles.Triggerings, valueOf)
        }
      }
    })
  }

  private fun newTextual(title: String): TTarget {
    val coupler = newTextualCouplerCore(title)
    val passText = coupler.passText
    trace(" > Generating textual target _state=",
      passText ?: coupler.getText?.invoke(title) ?: Error("No textual _state"))
    return facets.newTextualTarget(title, coupler)
  }

  private fun newNumeric(title: String): TTarget {
    val coupler = object : NumericCoupler() {
      override val passValue = Titles.StartNumber
      override val min = 5.0
      override val max = 25.0
    }
    trace(" > Generating numeric target _state=", coupler.passValue)
    return facets.newNumericTarget(title, coupler)
  }

  private fun newToggling(title: String, state: Boolean): TTarget {
    trace(" > Generating toggling target _state=", state)
    val coupler = object : TogglingCoupler() {
      override val passSet = state
      override val targetStateUpdated = { state: Any, title: String ->
        trace(" > Toggling _state updated: title=$title _state=", state)
        facets.setTargetLive(Titles.Toggled, state as Boolean)
      }
    }
    return facets.newTogglingTarget(title, coupler)
  }

  private fun newIndexing(title: String, indexables: List<String>, indexStart: Int): TTarget {
    trace(" > Generating indexing target _state=", indexStart)
    val coupler = object : IndexingCoupler() {
      override val getIndexables = { indexables }
      override val newUiSelectable = { indexable: Any -> indexable as String }
      override val passIndex = indexStart
    }
    return facets.newIndexingTarget(title, coupler)
  }

  private fun newTextualCouplerCore(title: String): TextualCoupler {
    val textTextual = title + " text in " + this.title
    return when (title) {
      Titles.NumericValue -> object : TextualCoupler() {
        override val getText = { _: String ->
          val state = facets.getTargetState(Titles.NumericField)
          ("Number is " + (if (state != null) Math.rint(state as Double) else " not yet set")).replace(("\\.\\d+").toRegex(), "")
        }
      }
      Titles.Toggled -> object : TextualCoupler() {
        override val getText = { _: String ->
          "Set to " + facets.getTargetState(Titles.Toggling)
        }
      }
      Titles.Indexed -> object : TextualCoupler() {
        override val getText = { _: String ->
          if (facets.getTargetState(Titles.Indexing) == null)
            ("No data yet for " + Titles.Indexing)
          else
            facets.getIndexingState(Titles.Indexing).indexed as String
        }
      }
      Titles.Index -> object : TextualCoupler() {
        override val getText = { _: String ->
          val state = facets.getTargetState(Titles.Indexing)
          state?.toString() ?: "No data yet for "+Titles.Indexing
        }
      }
      Titles.MasterTextual -> object : TextualCoupler() {
        override val getText = { _: String -> textTextual }
        override val targetStateUpdated = { state: Any, title: String ->
          trace(" > Textual _state updated: title=$title _state=", state)
          facets.updateTargetState(Titles.SlaveTextual,
            Titles.MasterTextual + " has changed to: " + state)
        }
      }
      Titles.Triggerings -> object : TextualCoupler() {
        override val passText = "0"
        override val targetStateUpdated = { state: Any, _: String ->
          if (Integer.valueOf(state as String) > 4)
            facets.setTargetLive(Titles.Trigger, false)
        }
      }
      else -> object : TextualCoupler() {
        override val passText = textTextual
      }
    }
  }

  override fun buildLayout() {
    when (test) {
      TargetTest.Textual -> generateFacets(Titles.MasterTextual)
      TargetTest.TogglingLive -> generateFacets(Titles.Toggling, Titles.Toggled)
      TargetTest.Numeric -> generateFacets(Titles.NumericField, Titles.NumericValue)
      TargetTest.Trigger -> generateFacets(Titles.Trigger, Titles.Triggerings)
      else -> generateFacets(Titles.Indexing, Titles.Index, Titles.Indexed)
    }
  }

  override fun buildSurface() {
    super.buildSurface()
    if (false) return
    val update: Any = when (test) {
      TargetTest.TogglingLive -> !Titles.StartToggled
      TargetTest.Indexing -> (Titles.StartIndex + 1) % 2
      TargetTest.Numeric -> Titles.StartNumber * 2
      else -> "Some updated text"
    }
    trace(" > Simulating input: update=", update)
    val title = when (test) {
      TargetTest.Indexing -> Titles.Indexing
      TargetTest.TogglingLive -> Titles.Toggling
      TargetTest.Numeric -> Titles.NumericField
      TargetTest.Trigger -> Titles.Trigger
      else -> Titles.MasterTextual
    }
    facets.updateTargetState(title, update)
  }
}