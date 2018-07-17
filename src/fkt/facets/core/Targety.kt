package fkt.facets.core

import fkt.java.TTarget

interface Targety : Notifying, TTarget {
override fun title():String
override fun elements():Array<Targety> 
open fun updateState(update:Any):Unit
open fun state():Any
open fun isLive():Boolean
open fun setLive(live:Boolean):Unit
}

