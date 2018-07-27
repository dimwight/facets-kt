package fkt.facets.core
interface Targeter : Notifying,Notifiable,Retargetable{
fun target():Targety
override val elements: List<Targeter>
fun attachFacet(f:Facet)
fun retargetFacets()
}

