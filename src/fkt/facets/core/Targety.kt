package fkt.facets.core

import fkt.facets.TTarget

interface Targety : Notifying, TTarget {
override fun title():String
override fun elements(): List<out Notifying>
  fun updateState(update:Any)
fun state():Any
fun isLive():Boolean
fun setLive(live:Boolean)
}

