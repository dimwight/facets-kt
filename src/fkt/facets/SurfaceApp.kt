package fkt.facets
import fkt.facets.core.Facets
import fkt.facets.core.FacetsApp
import fkt.java.Target
abstract class SurfaceApp(val facets:Facets) : FacetsApp{
abstract override fun getContentTrees():Array<Target> 
override fun onRetargeted(activeTitle:String):Unit{
}
abstract override fun buildLayout():Unit
open fun buildSurface(){
this.facets.buildApp(this)
}
}

