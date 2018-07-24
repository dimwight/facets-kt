package fkt.facets.core
interface Targeter : Notifying,Notifiable,Retargetable{
fun target():Targety
override fun elements(): List<Targeter>
fun attachFacet(f:Facet)
fun retargetFacets()
}

