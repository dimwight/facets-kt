package fkt.facets.core
open class IndexingFrameTargeter : TargeterCore(){
var titleTargeters=HashMap<String,Targeter>()
var indexing:Targeter?=null
var indexed:Targeter?=null
lateinit var indexingTarget:Indexing
lateinit var indexedTarget:Targety
lateinit var indexedTitle:String
override fun retarget(target:Targety){
super.retarget(target)
this.updateToTarget()
if(this.indexing==null){
this.indexing=this.indexingTarget.newTargeter()
this.indexing!!.setNotifiable(this)
}
if(this.titleTargeters.size==0){
var atThen=this.indexingTarget.index()
for(at in this.indexingTarget.indexables().indices){
this.indexingTarget.setIndex(at)
this.updateToTarget()
this.indexed=(this.indexedTarget as TargetCore).newTargeter()
val indexed=this.indexed!!
indexed.setNotifiable(this)
indexed.retarget(this.indexedTarget)
this.titleTargeters.put(this.indexedTitle,indexed)
}
this.indexingTarget.setIndex(atThen)
this.updateToTarget()
}
this.indexing!!.retarget(this.indexingTarget)
this.indexed=this.titleTargeters.get(this.indexedTitle) as Targeter
if(this.indexed==null)throw Error("No indexed for"+this.indexedTitle)
this.indexed!!.retarget(this.indexedTarget)
}
override fun retargetFacets(){
super.retargetFacets()
this.indexing!!.retargetFacets()
this.titleTargeters.values.forEach({t->
t.retargetFacets()})
}
override fun titleElements():Array<Targeter> {
var list=this.elements().toMutableList()
list.add(this.indexing!!)
this.titleTargeters.values.forEach({it->
list.add(it)})
return list.toTypedArray()
}
private fun updateToTarget(){
var frame=this.target() as IndexingFrame
this.indexingTarget=frame.indexing()
this.indexedTarget=frame.indexedTarget()
this.indexedTitle=this.indexedTarget.title()
}
}

