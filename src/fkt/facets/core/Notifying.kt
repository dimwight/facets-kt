package fkt.facets.core
import fkt.facets.util.Titled
interface Notifying : Notifiable,Titled{
fun setNotifiable(n:Notifiable):Unit
fun notifiable():Notifiable
val elements: List<Notifying>
fun notifyParent():Unit
}

