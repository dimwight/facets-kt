package fkt.facets

/**
 Marker type of Superficial targets (and trees) created in [Facets].

 A [TTarget]

 - exposes client state or logic in the UI
 - can be created in a range of flavours as listed below
 - is created with an identifying [String] *title* that thereafter identifies it
 - has a *live* state which can be used to enable its avatar in the UI

 ## Target flavours
 ### Stateful
 - *Textual*: Exposes a [String]
 - *Toggling*: Exposes a [Boolean]
 - *Numeric*: Exposes a [Number]
 - *Trigger*: Exposes an action
 - *Indexing*: Exposes an index into a [List] of values

 ### Group
 Container for [List] of [TTarget]s, enabling construction of [TTarget] trees.

 ### Framing
 - Exposes a generic object.
 - An *IndexingFrame* exposes an *Indexing* and a [TTarget] tree exposing the currently
 indexed item.
 */
interface TTarget
/**
 Defines UI response to regargeting of Superficial facet.

 @param [state] of the facet's current target
 */
typealias FacetUpdater = (state: Any) -> Unit

/**
 Connects a [TTarget] with client code.
 */
abstract class TargetCoupler {
  /**
   Invoked (if non-`null`) when target state is updated from the UI or by client logic.

   @param [state] as updated

   @param [title] identifies the target updated
   */
  open val targetStateUpdated: ((state:Any, title:String) -> Unit)? = null
  /**
   Enables setting of initial Superficial live state.

   Default is `true`.
   */
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
  /**
   Return (non-empty) list of objects to be indexed
   */
  abstract fun getIndexables(): List<Any>

  /**
   Return text value representing the [indexable].

   If `null`, the indexing will create a dummy value.
   */
  open val newUiSelectable: ((indexable:Any) -> String)? = null
  /**
   Define the initial state of the indexing.

   If `null`, state will be `0`.
   */
  open val passIndex: Int? = null
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

/**
 Supplies policy for an _IndexingFrame_ [TTarget].

 __Note__ For values left as `null`, appropriate defaults are created.
 */
abstract class IndexingFramePolicy {
  /**
   Title for the _IndexingFrame_ itself.

   Particularly useful where the frame is the root of a content tree
   */
  open val frameTitle: String? = null
  /**
   Title for the _Indexing_ framed.

   Enables retrieval of the current [IndexingState] for the framed.
   */
  open val indexingTitle: String? = null
  abstract fun getIndexables(): List<Any>
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
  fun updateTarget(title: String, state: Any?=null)
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

