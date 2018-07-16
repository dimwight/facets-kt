package fkt.facets.core
import fkt.facets.util.traceThing
open class IndexingFrame(title:String,val indexing_:Indexing) : TargetCore(title){
open fun indexedTarget():Targety{
var indexed=this.indexing_.indexed() as TargetCore
val type:String?=indexed.type
traceThing("^indexedTarget",indexed.type)
return if(type!=null&&type===TargetCoreType)indexed else this.newIndexedTargets(indexed)
}
open fun newIndexedTargets(indexed:Any):Targety{
throw Error("Not implemented in"+this.title())
}
open fun indexing():Indexing{
return this.indexing_
}
override fun newTargeter():Targeter{
return IndexingFrameTargeter()
}
override fun notifiesTargeter():Boolean{
return true
}
}

