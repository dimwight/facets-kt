package fkt.facets.core
import fkt.facets.core.TextualCoupler
import fkt.facets.core.SimpleState
open class Textual(title:String,coupler:TextualCoupler) : TargetCore(title,coupler){
override fun state():SimpleState{
	throw Error()
}
}

