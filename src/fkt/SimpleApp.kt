package fkt

import fkt.facets.IndexingCoupler
import fkt.facets.NumericCoupler
import fkt.facets.TTarget
import fkt.facets.TargetCoupler
import fkt.facets.TextualCoupler
import fkt.facets.TogglingCoupler
import fkt.SimpleTitles as Simples

open class SimpleApp(test: TargetTest, trace: Boolean) : AppCore(trace, test) {
  override fun newContentTrees(): Set<TTarget> {
    trace(" > Generating targets")
    return setOf(facets.newTargetGroup(title ="${test.toString()} Test", members = when (test) {
      TargetTest.Textual -> listOf(
        newTextual(Simples.MasterTextual),
        newTextual(Simples.SlaveTextual)
      )
      TargetTest.TogglingLive -> listOf(
        newToggling(Simples.Toggling, Simples.StartToggled),
        newTextual(Simples.Toggled)
      )
      TargetTest.Indexing -> listOf(
        newIndexing(Simples.Indexing, listOf(
          Simples.MasterTextual,
          Simples.SlaveTextual
        ),
          Simples.StartIndex),
        newTextual(Simples.Index),
        newTextual(Simples.Indexed)
      )
      TargetTest.Numeric -> listOf(
        newNumeric(Simples.NumericField),
        newTextual(Simples.NumericValue)
      )
      else -> listOf(
        newTrigger(Simples.Trigger),
        newTextual(Simples.Triggerings)
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
        val got: String? = facets.getTargetState(Simples.Triggerings) as String
        if (got != null) {
          val valueOf = (Integer.valueOf(got) + 1).toString()
          facets.updateTargetState(Simples.Triggerings, valueOf)
        }
      }
    })
  }

  private fun newTextual(title: String): TTarget {
    val coupler = newTextualCouplerCore(title)
    val passText = coupler.passText
    trace(" > Generating textual target state=",
      passText ?: coupler.getText?.invoke(title) ?: Error("No textual state"))
    return facets.newTextualTarget(title, coupler)
  }

  private fun newNumeric(title: String): TTarget {
    val coupler = object : NumericCoupler() {
      override val passValue = Simples.StartNumber
      override val min = 5.0
      override val max = 25.0
    }
    trace(" > Generating numeric target state=", coupler.passValue)
    return facets.newNumericTarget(title, coupler)
  }

  private fun newToggling(title: String, state: Boolean): TTarget {
    trace(" > Generating toggling target state=", state)
    val coupler = object : TogglingCoupler() {
      override val passSet = state
      override val targetStateUpdated = { state: Any, title: String ->
        trace(" > Toggling state updated: title=$title state=", state)
        facets.setTargetLive(Simples.Toggled, state as Boolean)
      }
    }
    return facets.newTogglingTarget(title, coupler)
  }

  private fun newIndexing(title: String, indexables: List<String>, indexStart: Int): TTarget {
    trace(" > Generating indexing target state=", indexStart)
    val coupler = object : IndexingCoupler() {
      override val getIndexables = { _: String -> indexables }
      override val newUiSelectable = { indexable: Any -> indexable as String }
      override val passIndex = indexStart
    }
    return facets.newIndexingTarget(title, coupler)
  }

  private fun newTextualCouplerCore(title: String): TextualCoupler {
    val textTextual = title + " text in " + this.title
    return when (title) {
      Simples.NumericValue -> object : TextualCoupler() {
        override val getText = { _: String ->
          val state = facets.getTargetState(Simples.NumericField)
          ("Number is " + (if (state != null) Math.rint(state as Double) else " not yet set")).replace(("\\.\\d+").toRegex(), "")
        }
      }
      Simples.Toggled -> object : TextualCoupler() {
        override val getText = { _: String ->
          "Set to " + facets.getTargetState(Simples.Toggling)
        }
      }
      Simples.Indexed -> object : TextualCoupler() {
        override val getText = { _: String ->
          if (facets.getTargetState(Simples.Indexing) == null)
            ("No data yet for " + Simples.Indexing)
          else
            facets.getIndexingState(Simples.Indexing).indexed as String
        }
      }
      Simples.Index -> object : TextualCoupler() {
        override val getText = { _: String ->
          val state = facets.getTargetState(Simples.Indexing)
          state?.toString() ?: "No data yet for "+Simples.Indexing
        }
      }
      Simples.MasterTextual -> object : TextualCoupler() {
        override val getText = { _: String -> textTextual }
        override val targetStateUpdated = { state: Any, title: String ->
          trace(" > Textual state updated: title=$title state=", state)
          facets.updateTargetState(Simples.SlaveTextual,
            Simples.MasterTextual + " has changed to: " + state)
        }
      }
      Simples.Triggerings -> object : TextualCoupler() {
        override val passText = "0"
        override val targetStateUpdated = { state: Any, _: String ->
          if (Integer.valueOf(state as String) > 4)
            facets.setTargetLive(Simples.Trigger, false)
        }
      }
      else -> object : TextualCoupler() {
        override val passText = textTextual
      }
    }
  }

  override fun buildLayout() {
    when (test) {
      TargetTest.Textual -> generateFacets(Simples.MasterTextual)
      TargetTest.TogglingLive -> generateFacets(Simples.Toggling, Simples.Toggled)
      TargetTest.Numeric -> generateFacets(Simples.NumericField, Simples.NumericValue)
      TargetTest.Trigger -> generateFacets(Simples.Trigger, Simples.Triggerings)
      else -> generateFacets(Simples.Indexing, Simples.Index, Simples.Indexed)
    }
  }

  override fun buildSurface() {
    super.buildSurface()
    if (false) return
    val update: Any = when (test) {
      TargetTest.TogglingLive -> !Simples.StartToggled
      TargetTest.Indexing -> (Simples.StartIndex + 1) % 2
      TargetTest.Numeric -> Simples.StartNumber * 2
      else -> "Some updated text"
    }
    trace(" > Simulating input: update=", update)
    val title = when (test) {
      TargetTest.Indexing -> Simples.Indexing
      TargetTest.TogglingLive -> Simples.Toggling
      TargetTest.Numeric -> Simples.NumericField
      TargetTest.Trigger -> Simples.Trigger
      else -> Simples.MasterTextual
    }
    facets.updateTargetState(title, update)
  }
}