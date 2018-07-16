package fkt
import fkt.java.util.Titled
import fkt.facets_.util.Tracer
import fkt.SelectingTitles as Selectings
enum class TargetTest {
	Textual, TogglingLive, Indexing, Numeric, Trigger, Selecting, Contenting;
	fun indexingTitle():String {
		if (this.ordinal < Selecting.ordinal)
			throw RuntimeException("Not implemented in " + this)
		return if (this == Selecting) Selectings.Select else Selectings.Switch
	}
	val isSimple:Boolean get()=this.ordinal<Selecting.ordinal
	companion object {
		fun simpleValues():Array<TargetTest> {
			return arrayOf(Textual, TogglingLive, Indexing, Numeric, Trigger)
		}
	}
}
abstract class SurfaceCore(facets: Facets, test:TargetTest)
		:Tracer(test.name), Titled, FacetsApp {
  val facets: Facets
  protected val test:TargetTest
  init{
    this.facets = facets
    this.test = if (true || test == TargetTest.Selecting) test else TargetTest.Indexing
    facets.times.doTime = false || facets.doTrace
  }
  open fun buildSurface() {
    facets.buildApp(this)
  }
  protected fun generateFacets(vararg titles:String) {
    for (title in titles){
      trace(" > Generating facet for title=", title)
      facets.attachFacet(title, { value->
				trace((" > Facet for " + title + " updated: value="), value)
			})
    }
  }
  override fun title():String {
    return test.name
  }
}