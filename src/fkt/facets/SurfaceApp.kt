package fkt.facets

abstract class SurfaceApp(val facets: Facets) : FacetsApp {
abstract override fun getContentTrees():Array<TTarget>
override fun onRetargeted(activeTitle:String):Unit{
}
abstract override fun buildLayout():Unit
open fun buildSurface(){
this.facets.buildApp(this)
}
}

