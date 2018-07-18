package fkt.facets.core

interface Targety : Notifying, TTarget {
override fun title():String
override fun elements():Array<Targety> 
fun updateState(update:Any):Unit
fun state():Any
fun isLive():Boolean
fun setLive(live:Boolean):Unit
}

