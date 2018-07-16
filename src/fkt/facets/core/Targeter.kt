package fkt.facets.core
interface Targeter : Notifying,Notifiable,Retargetable{
open fun target():Targety
override fun elements():Array<Targeter> 
open fun attachFacet(f:Facet):Unit
open fun retargetFacets():Unit
}

