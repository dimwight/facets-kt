package fkt.facets

/**
 Marker type of Superficial targets (and trees) created in [Facets]
 */
interface TTarget
typealias FacetUpdater = (state: Any) -> Unit

abstract class TargetCoupler {
  open val targetStateUpdated: ((Any, String) -> Unit)? = null
  open val passLive=true
}

abstract class TextualCoupler : TargetCoupler() {
  open val passText: String? = null
  open val getText: ((String) -> String)? = null
}

abstract class TogglingCoupler : TargetCoupler() {
  abstract val passSet: Boolean
}

abstract class NumericCoupler : TargetCoupler() {
  abstract val passValue: Double
  open val min: Double? = null
  open val max: Double? = null
}

/**
 [TargetCoupler] for an indexing [TTarget]
 */
abstract class IndexingCoupler : TargetCoupler() {
  abstract val getIndexables: () -> List<Any>
  open val passIndex: Int? = null
  open val newUiSelectable: ((indexable:Any) -> String)? = null
}

/**
 Supplements the basic (integer) state of an indexing target,
 based on values returned by the [IndexingCoupler] passed when
 that target was created.
 */
abstract class IndexingState {
  /**
   Text values rendering the current indexables,
   as returned by [IndexingCoupler.newUiSelectable]
   */
  abstract val uiSelectables: List<String>
  /**
   As indexed by the current state of the target into
   the current return of [IndexingCoupler.getIndexables]
   */
  abstract val indexed: Any
}

abstract class IndexingFramePolicy {
  open val indexingTitle: String? = null
  abstract val getIndexables: () -> List<Any>
  open val frameTitle: String? = null
  open val newUiSelectable: ((indexable:Any) -> String)? = null
  open val newFrameTargets: (() -> (List<TTarget>))? = null
  open val newIndexedTreeTitle: ((indexed:Any) -> String)? = null
  open val newIndexedTree: ((indexed:Any, title:String) -> TTarget)? = null
}

interface Times {
  var doTime: Boolean
  fun setResetWait(millis: Int)
  fun elapsed(): Int
  fun traceElapsed(msg: String?)
}

fun newFacets(trace: Boolean, app: FacetsApp): Facets = FacetsWorks(false || trace, app)
interface Facets {
  /**
   Enables access to internal textual [TTarget]
   whose state is always the title of the active content tree.
   */
  val activeContentTitle: String
  /**
   Constructs a Superficial targeter tree from [TTarget] trees defined by [app];
   initially retargets it; and prompts the [app] to add UI facets.
   */
  fun buildApp(app: FacetsApp)
  /**
  Replaces any tree with the same title and calls [activateContentTree]
   */
  fun attachContentTree(tree: TTarget)
  /**
   Sets the internal indexing frame to the content tree identified by [title];
   complains if not found.
   */
  fun activateContentTree(title: String)
  fun newTextualTarget(title: String, c: TextualCoupler): TTarget
  fun newTogglingTarget(title: String, c: TogglingCoupler): TTarget
  fun newTriggerTarget(title: String, c: TargetCoupler): TTarget
  fun newNumericTarget(title: String, c: NumericCoupler): TTarget
  fun newTargetGroup(title: String, members: List<TTarget>): TTarget
  fun newIndexingTarget(title: String, c: IndexingCoupler): TTarget
  fun getIndexingState(title: String): IndexingState
  fun newIndexingFrame(p: IndexingFramePolicy): TTarget
  fun attachFacet(title: String, updater: FacetUpdater)
  fun updateTargetState(title: String, update: Any)
  fun notifyTargetUpdated(title: String)
  fun updateTargetWithNotify(title: String, update: Any)
  fun getTargetState(title: String): Any?
  fun setTargetLive(title: String, live: Boolean)
  fun isTargetLive(title: String): Boolean
  val supplement: () -> Unit
  val times: Times
  val doTrace: Boolean
}

/**
Defines methods to be called by a [Facets] instance on its containing app.
 */
interface FacetsApp {
  /**
  Create [TTarget] trees exposing app content.

  Called once from [Facets.buildApp]; must contain one of
  each possible tree structure to enable UI construction in [buildLayout].
   */
  fun newContentTrees(): List<TTarget>

  /**
  Called on each retargeting of the content tree root, before facets are updated.

  Typically used to update target live states.
   */
  fun onRetargeted(activeTitle: String)

  /**
  Called once from [Facets.buildApp], immediately following [onRetargeted].

  Should create a Superficial layout using [Facets.attachFacet] etc.
   */
  fun buildLayout()
}

