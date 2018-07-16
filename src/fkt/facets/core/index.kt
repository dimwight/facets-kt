package fkt.facets.core

interface Target{
}
typealias SimpleState=Any
typealias FacetUpdater=(state:SimpleState)->Unit
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
  open val newFrameTargets: (() -> (Array<out Target>))? = null
  open val newIndexedTreeTitle: ((Any) -> String)? = null
  open val newIndexedTree: ((Any, String) -> Target)? = null
}
interface Times{
var doTime:Boolean
fun setResetWait(millis:Int)
fun elapsed():Int
fun traceElapsed(msg:String)
}
interface Facets{
val activeContentTitle:String
val times:Times
var doTrace:Boolean
fun newTextualTarget(title:String,coupler:TextualCoupler):Target
fun newTogglingTarget(title:String,coupler:TogglingCoupler):Target
fun newNumericTarget(title:String,coupler:NumericCoupler):Target
fun newTriggerTarget(title:String,coupler:TargetCoupler):Target
fun newTargetGroup(title:String,members:Array<Target> ):Target
fun newIndexingTarget(title:String,coupler:IndexingCoupler):Target
fun getIndexingState(title:String):IndexingState
fun newIndexingFrame(p:IndexingFramePolicy):Target
fun addContentTree(tree:Target)
fun activateContentTree(title:String)
fun attachFacet(title:String,updater:FacetUpdater)
fun updateTargetState(title:String,update:SimpleState)
fun getTargetState(title:String):SimpleState
fun notifyTargetUpdated(title:String)
fun updateTargetWithNotify(title:String,update:SimpleState)
fun setTargetLive(title:String,live:Boolean)
fun isTargetLive(title:String):Boolean
fun buildApp(app:FacetsApp)
val supplement:()->Unit
}
interface FacetsApp{
fun getContentTrees():Array<Target> 
fun onRetargeted(activeTitle:String)
fun buildLayout()
}

