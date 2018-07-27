package fkt.facets

import fkt.facets.core.FacetsWorks

interface TTarget
typealias FacetUpdater=(state:Any)->Unit
abstract class TargetCoupler {
  open val targetStateUpdated: ((Any, String) -> Unit)? = null
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
abstract class IndexingCoupler : TargetCoupler() {
  abstract val getIndexables: (String) -> List<*>
  open val passIndex: Int? = null
  open val newUiSelectable: ((Any) -> String)? = null
}
abstract class IndexingState {
  abstract val uiSelectables: Array<String>
  abstract val indexed: Any
}
abstract class IndexingFramePolicy {
  open val indexingTitle: String? = null
  abstract val getIndexables: () -> (List<Any>)
  open val frameTitle: String? = null
  open val newUiSelectable: ((Any) -> String)? = null
  open val newFrameTargets: (() -> (List<TTarget>))? = null
  open val newIndexedTreeTitle: ((Any) -> String)? = null
  open val newIndexedTree: ((Any, String) -> TTarget)? = null
}
interface Times{
var doTime:Boolean
fun setResetWait(millis:Int)
fun elapsed():Int
fun traceElapsed(msg:String?)
}
fun newFacets(trace: Boolean,app:FacetsApp): Facets = FacetsWorks(trace, app)
interface Facets{
val activeContentTitle:String
val times: Times
var doTrace:Boolean
fun buildApp(app: FacetsApp)
fun addContentTree(tree: TTarget)
fun activateContentTree(title:String)
fun newTextualTarget(title:String,c: TextualCoupler): TTarget
fun newTogglingTarget(title:String,c: TogglingCoupler): TTarget
fun newTriggerTarget(title:String,c: TargetCoupler): TTarget
fun newNumericTarget(title:String,c: NumericCoupler): TTarget
fun newTargetGroup(title:String, members: List<TTarget>): TTarget
fun newIndexingTarget(title:String,c: IndexingCoupler): TTarget
fun getIndexingState(title:String): IndexingState
fun newIndexingFrame(p: IndexingFramePolicy): TTarget
fun attachFacet(title:String,updater: FacetUpdater)
fun updateTargetState(title:String,update:Any)
fun notifyTargetUpdated(title:String)
fun updateTargetWithNotify(title:String,update:Any)
fun getTargetState(title:String):Any?
fun setTargetLive(title:String,live:Boolean)
fun isTargetLive(title:String):Boolean
val supplement:()->Unit
}

/**
Defines methods to be called from [Facets] instance.
 */
interface FacetsApp{
  /**
   Create a set of [TTarget] trees exposing app content.

   To be called once from [Facets.buildApp]; must contain at least one of
   each possible tree structure.
   */
  fun newContentTrees(): Set<TTarget>

  /**
   To be called on each retargeting of the content tree root, before facets are updated.

   Typically used to update target live states.
   */
  fun onRetargeted(activeTitle:String)
  /**
   To be called once from [Facets.buildApp], immediately following [onRetargeted].

   The app should create a Superficial layout using [Facets.attachFacet] etc.
   */
  fun buildLayout()
}

