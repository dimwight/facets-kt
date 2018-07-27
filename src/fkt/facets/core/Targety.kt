package fkt.facets.core

import fkt.facets.TTarget

interface Targety : Notifying, TTarget {
override val elements: List<Targety>
fun updateState(update:Any)
fun state():Any
fun isLive():Boolean
fun setLive(live:Boolean)
}

