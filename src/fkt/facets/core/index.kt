package fkt.facets.core
interface Target{
}
typealias SimpleState=Any
typealias FacetUpdater=(state:SimpleState)->Unit
interface TargetCoupler{
fun targetStateUpdated(state:SimpleState,title:String)
}
interface TextualCoupler : TargetCoupler{
var passText:String?
fun getText(title:String):String?
fun isValidText(text:String,title:String):Boolean?
}
interface TogglingCoupler : TargetCoupler{
var passSet:Boolean
}
interface NumericCoupler : TargetCoupler{
var passValue:Int
var min:Int
var max:Int
}
interface IndexingCoupler : TargetCoupler{
val passIndex:Int
fun getIndexables(title:String):Array<Any> 
fun newUiSelectable(indexable:Any):Any?
}
interface IndexingState{
var uiSelectables:Array<String> 
var indexed:Any
}
interface IndexingFramePolicy{
var frameTitle:String?
var indexingTitle:String?
fun getIndexables():Array<Any> 
fun newUiSelectable(indexable:Any):Any?
fun newFrameTargets():Array<Target> ?
fun newIndexedTreeTitle(indexed:Any):String?
fun newIndexedTree(indexed:Any,title:String):Target?
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
fun newIndexingFrame(policy:IndexingFramePolicy):Target
fun addContentTree(add:Target)
fun activateContentTree(title:String)
fun attachFacet(title:String,updater:FacetUpdater)
fun updateTargetState(title:String,update:SimpleState)
fun getTargetState(title:String):SimpleState
fun notifyTargetUpdated(title:String)
fun updateTargetWithNotify(title:String,update:SimpleState)
fun setTargetLive(title:String,live:Boolean)
fun isTargetLive(title:String):Boolean
var supplement:Any
fun buildApp(app:FacetsApp)
fun newInstance(trace:Boolean):Facets
}
interface FacetsApp{
fun getContentTrees():Array<Target> 
fun onRetargeted(activeTitle:String)
fun buildLayout()
}

