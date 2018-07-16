package fkt.facets.core
open class Indexing(title:String,coupler:IndexingCoupler) : TargetCore(title,coupler){
lateinit var indexings:Array<Any> 
open fun index():Int{
return this.state_ as Int
}
open fun setIndex(index:Int){
val first=this.state_===NoState
this.state_=index
if(!first)this.coupler().targetStateUpdated(this.state_,this.title())
}
open fun indexables():Array<Any> {
val indexables:Array<Any> =this.coupler().getIndexables(this.title())
if(indexables.isEmpty())throw Error("Empty indexables in"+this)
else return indexables
}
open fun uiSelectables():Array<String> {
var selectables=0
val coupler=this.coupler()
return this.indexables().map({i->
 ((coupler.newUiSelectable(i))?:this.title())as String+selectables++}).toTypedArray()
}
private fun coupler():IndexingCoupler{
return this.extra as IndexingCoupler
}
open fun indexed():Any{
if(this.state_===NoState)throw Error("No index in"+this.title())
else return this.indexables()[this.state_ as Int]
}
open fun setIndexed(indexable:Any){
this.indexables().forEachIndexed({at,i->
if(i===indexable)this.setIndex(at)
})
}
override fun updateState(update:SimpleState){
this.setIndex(update as Int)
}
}

