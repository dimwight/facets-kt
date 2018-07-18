package fkt.facets.core
import fkt.facets.util.Titled
interface Notifying : Notifiable,Titled{
open fun setNotifiable(n:Notifiable):Unit
open fun notifiable():Notifiable
open fun elements():Array<out Notifying> 
open fun notifyParent():Unit
}

