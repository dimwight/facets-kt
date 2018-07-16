package fkt.facets
import fkt.facets.core.Targety
import fkt.facets.core.TargetCore
import fkt.facets.core.Targeter
import fkt.facets.core.Notifiable
import fkt.facets.core.Indexing
import fkt.facets.core.Toggling
import fkt.facets.core.Textual
import fkt.facets.core.IndexingFrame
import fkt.facets.core.Facet
import fkt.facets.core.SimpleState
import fkt.facets.core.FacetUpdater
import fkt.facets.core.TextualCoupler
import fkt.facets.core.FacetsApp
import fkt.facets.core.Target
import fkt.facets.core.IndexingCoupler
import fkt.facets.core.IndexingState
import fkt.facets.core.TogglingCoupler
import fkt.facets.core.TargetCoupler
import fkt.facets.core.IndexingFramePolicy
import fkt.facets.core.TargeterCore
import fkt.facets.core.*

fun newInstance(trace:Boolean):Facets{
return Facets(trace)
}
open class Facets(private val doTrace:Boolean){
open fun trace(msg:String){
if(this.doTrace)print(">"+msg)
}
val times=object:Any(){
val doTime=false
}
var activeContentTitle="[Active Content Tree]"
var notifiable:Notifiable=object:Notifiable{
override fun notify(notice:Any){
trace("Notified with"+rootTargeter.title())
rootTargeter.retarget(rootTargeter.target())
callOnRetargeted()
rootTargeter.retargetFacets()
}
}
lateinit var onRetargeted:(title:String)->Any
var titleTargeters=HashMap<String, Targeter>()
var titleTrees=HashMap<String, Targety>()
lateinit var root:IndexingFrame
lateinit var rootTargeter:Targeter
open fun buildApp(app:FacetsApp){
this.onRetargeted={title->
app.onRetargeted(title)
}
val trees=app.getContentTrees()
if(trees is Array<*>)(trees as Array<Targety>).forEach({t->
this.addContentTree(t)})
else this.addContentTree((trees as Targety))
this.trace("Building targeter tree for root="+this.root.title())
if(this.rootTargeter==null)this.rootTargeter=(this.root as TargetCore).newTargeter()
this.rootTargeter.setNotifiable(this.notifiable)
this.rootTargeter.retarget(this.root)
this.addTitleTargeters(this.rootTargeter)
this.callOnRetargeted()
app.buildLayout()
}
private fun callOnRetargeted(){
val title=this.root.title()
this.trace("Calling disableAll with active="+title)
this.onRetargeted(title)
}
open fun addContentTree(tree:Targety){
this.titleTrees.set(tree.title(),tree)
this.root.indexing().setIndexed(tree)
}
open fun activateContentTree(title:String){
val tree=this.titleTrees.get(title)
if(tree==null)throw Error("No tree for"+title)
this.root.indexing().setIndexed(tree)
this.notifiable.notify(title)
}
open fun newTextualTarget(title:String,coupler:TextualCoupler):Target{
val textual=Textual(title,coupler)
this.trace("Created textual title="+title)
return textual
}
open fun newTogglingTarget(title:String,coupler:TogglingCoupler):Target{
val toggling=Toggling(title,coupler)
this.trace("Created toggling title="+title)
return toggling
}
open fun newTriggerTarget(title:String,coupler:TargetCoupler):Target{
val trigger=TargetCore(title,coupler)
this.trace("Created trigger title="+title)
return trigger
}
open fun newTargetGroup(title:String,members:Array<Target> ):Target{
return TargetCore(title,members as Array<Targety> )
}
open fun addTitleTargeters(t:Targeter){
val title=t.title()
val elements:Array<Targeter> =(t as TargeterCore).titleElements()
this.titleTargeters.set(title,t)
this.trace("Added targeter: title="+title+": elements="+elements.size)
elements.forEach({e->
this.addTitleTargeters(e)})
}
open fun attachFacet(title:String,updater:FacetUpdater):Unit{
val t:Targeter=this.titleTargeters.get(title) as Targeter
if(t==null)throw Error("No targeter for"+title)
this.trace("Attaching facet: title="+title)
val facet: Facet =object:Facet{
override fun retarget(ta:Targety){
trace("Facet retargeted title="+ta.title())
// +' state='+ta.state()
updater(ta.state())
}

}
t.attachFacet(facet)
}
open fun updateTargetState(title:String,update:SimpleState):Unit{
this.titleTarget(title).updateState(update)
this.notifiable.notify(title)
}
open fun getTargetState(title:String):SimpleState{
return this.titleTarget(title).state()
}
open fun isTargetLive(title:String):Boolean{
return this.titleTarget(title).isLive()
}
open fun setTargetLive(title:String,live:Boolean){
this.titleTarget(title).setLive(live)
}
open fun notifyTargetUpdated(title:String){
val target=this.titleTarget(title)
target.notifyParent()
}
open fun titleTarget(title:String):Targety{
val got=this.titleTargeters.get(title)
if(got==null)throw Error("No targeter for"+title)
return got.target()
}
open fun newIndexingTarget(title:String,coupler:IndexingCoupler):Targety{
val indexing=Indexing(title,coupler)
this.trace("Created indexing title="+title)
return indexing
}
open fun getIndexingState(title:String):IndexingState{
val i:Indexing=this.titleTarget(title) as Indexing
if(i==null)throw Error("No target for title="+title)
else return object:IndexingState{
override var uiSelectables=i.uiSelectables()
override var indexed=i.indexed()
}
}
var indexingFrames=0
open fun newIndexingFrame(p:IndexingFramePolicy):Targety{
val frameTitle=if(p.frameTitle!=null)p.frameTitle else "IndexingFrame"+this.indexingFrames
// ++
val indexingTitle=if(p.indexingTitle!=null)p.indexingTitle else frameTitle+".Indexing"
val indexing=Indexing(indexingTitle!!,object:IndexingCoupler{
override fun targetStateUpdated(state:SimpleState,title:String){
}
override var passIndex=0
override fun getIndexables(title:String)=
p.getIndexables()
// newUiSelectable:indexable=>!p.newUiSelectable?null:p.newUiSelectable(indexable)
override fun newUiSelectable(indexable:Any)=
p.newUiSelectable(indexable)
})
this.trace("Created indexing"+indexingTitle)
val frame=object  : IndexingFrame(frameTitle!!,indexing){
override fun lazyElements():Array<Targety> {
return if(p.newFrameTargets()!=null)p.newFrameTargets() as Array<Targety>  else  arrayOf()
}
override fun newIndexedTargets(indexed:Any):Targety{
val titler=p.newIndexedTreeTitle(indexed)
val title=if(titler!=null)titler else this.title()
// +'|indexed'
val newTree=p.newIndexedTree(indexed,title)
return if(newTree!=null)newTree as Targety else TargetCore(title)
}
}

// (frameTitle, indexing)
this.trace("Created indexing frame"+frameTitle)
return frame
}
}

