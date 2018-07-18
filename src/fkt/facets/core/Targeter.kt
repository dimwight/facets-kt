package fkt.facets.core
interface Targeter : Notifying,Notifiable,Retargetable{
fun target():Targety
override fun elements():Array<Targeter> 
fun attachFacet(f:Facet):Unit
fun retargetFacets():Unit
}

