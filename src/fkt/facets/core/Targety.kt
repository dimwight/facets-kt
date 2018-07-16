package fkt.facets.core
import fkt.facets.core.SimpleState
import fkt.facets.core.Target
interface Targety : Notifying,Target{
override fun title():String
override fun elements():Array<Targety> 
open fun updateState(update:SimpleState):Unit
open fun state():SimpleState
open fun isLive():Boolean
open fun setLive(live:Boolean):Unit
}

