package fkt.facets.core
interface Notifying : Notifiable{
open fun title():String
open fun setNotifiable(n:Notifiable):Unit
open fun notifiable():Notifiable
open fun elements():Array<out Notifying> 
open fun notifyParent():Unit
}

