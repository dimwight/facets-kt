package fkt.facets.core
import fkt.java.TTarget
typealias FacetUpdater=(state:Any)->Unit
abstract class TargetCoupler {
  open val targetStateUpdated: ((Any, String) -> Unit)? = null
}
abstract class TextualCoupler : TargetCoupler() {
  open val passText: String? = null
  open val getText: ((String) -> String)? = null
  open val isValidText: ((String, String) -> Boolean)? = null
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
  abstract val getIndexables: (String) -> Array<out Any>
  open val passIndex: Int? = null
  open val newUiSelectable: ((Any) -> String)? = null
}
abstract class IndexingState {
  abstract val uiSelectables: Array<String>
  abstract val indexed: Any
}
abstract class IndexingFramePolicy {
  open val indexingTitle: String? = null
  abstract val getIndexables: () -> (Array<out Any>)
  open val frameTitle: String? = null
  open val newUiSelectable: ((Any) -> String)? = null
  open val newFrameTargets: (() -> (Array<out TTarget>))? = null
  open val newIndexedTreeTitle: ((Any) -> String)? = null
  open val newIndexedTree: ((Any, String) -> TTarget)? = null
}
interface Times{
var doTime:Boolean
fun setResetWait(millis:Int)
fun elapsed():Int
fun traceElapsed(msg:String?)
}
interface Facets{
val activeContentTitle:String
val times:Times
var doTrace:Boolean
fun buildApp(app:FacetsApp)
fun addContentTree(tree: TTarget)
fun activateContentTree(title:String)
fun newTextualTarget(title:String,coupler:TextualCoupler): TTarget
fun newTogglingTarget(title:String,coupler:TogglingCoupler): TTarget
fun newNumericTarget(title:String,coupler:NumericCoupler): TTarget
fun newTriggerTarget(title:String,coupler:TargetCoupler): TTarget
fun newTargetGroup(title:String,members:Array<TTarget> ): TTarget
fun newIndexingTarget(title:String,coupler:IndexingCoupler): TTarget
fun getIndexingState(title:String):IndexingState
fun newIndexingFrame(p:IndexingFramePolicy): TTarget
fun attachFacet(title:String,updater:FacetUpdater)
fun updateTargetState(title:String,update:Any)
fun notifyTargetUpdated(title:String)
fun updateTargetWithNotify(title:String,update:Any)
fun getTargetState(title:String):Any?
fun setTargetLive(title:String,live:Boolean)
fun isTargetLive(title:String):Boolean
val supplement:()->Unit
}
interface FacetsApp{
fun getContentTrees():Any
fun onRetargeted(activeTitle:String)
fun buildLayout()
}

