package fkt.facets.core
import fkt.facets.util.traceThing
open class TargeterCore() : NotifyingCore("Targeter","Untargeted"),Targeter{
lateinit var elements_:Array<Targeter> 
lateinit var target_:Targety
var facets_:MutableList<Facet> = mutableListOf()
override fun retarget(target:Targety){
// if(target==null)throw new Error('Missing target');
this.target_=target
val targets:Array<Targety> =target.elements()
traceThing("^retarget",targets)
// if(this.elements_==null)
this.elements_=targets.map({targety->
var element=(targety as TargetCore).newTargeter()
element.setNotifiable(this)
element
}).toTypedArray()
if(targets.size==this.elements_.size)this.elements_.forEachIndexed({at,e->
e.retarget(targets[at])})
if((target as TargetCore).notifiesTargeter())target.setNotifiable(this)
}
override fun title():String{
return if(this.target_!=null)this.target_.title() else this.title_
}
override fun target():Targety{
if(this.target_==null)throw Error(this.title_)
else return this.target_
}
override fun elements():Array<Targeter> {
return this.elements_
}
open fun titleElements():Array<Targeter> {
return this.elements()
}
override fun attachFacet(f:Facet){
if(!this.facets_.contains(f))this.facets_.add(f)
f.retarget(this.target_)
}
override fun retargetFacets(){
this.elements_.forEach({e->
e.retargetFacets()})
this.facets_.forEach({f->
f.retarget(this.target_)})
}
}

