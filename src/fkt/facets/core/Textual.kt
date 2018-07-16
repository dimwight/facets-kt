package fkt.facets.core
open class Textual(title:String,coupler:TextualCoupler) : TargetCore(title,coupler){
override fun state():Any{
	throw Error()
}
}

