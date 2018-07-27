package fkt.facets.core

import fkt.facets.TTarget

interface Targety : Notifying, TTarget {
override val elements: List<Targety>
var state:Any
var live:Boolean
}

