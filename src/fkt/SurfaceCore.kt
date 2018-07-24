package fkt
import fkt.facets.Facets
import fkt.facets.FacetsApp
import fkt.facets.newFacets
import fkt.facets.util.Titled
import fkt.facets.util.Tracer
import fkt.SelectingTitles as Selectings
enum class TargetTest {
	Textual, TogglingLive, Indexing, Numeric, Trigger, Selecting, Contenting;
	val isSimple:Boolean get()=this.ordinal<Selecting.ordinal
	companion object {
		fun simpleValues():Array<TargetTest> {
			return arrayOf(Textual, TogglingLive, Indexing, Numeric, Trigger)
		}
	}
}
abstract class SurfaceCore(trace:Boolean, test:TargetTest)
		:Tracer(test.name), Titled, FacetsApp {
  /**
   Internal instance
   */
  val facets: Facets = newFacets(trace)
  val test:TargetTest = if (true || test == TargetTest.Selecting) test else TargetTest.Indexing

  init{
    facets.times.doTime = false || facets.doTrace
  }

  /**
   Calls [Facets.buildApp] on the private instance
   */
  open fun buildSurface() {
    facets.buildApp(this)
  }
  protected fun generateFacets(vararg titles:String) {
    for (title in titles){
      trace(" > Generating facet for title=", title)
      facets.attachFacet(title) { value->
        trace((" > Facet for $title updated: value="), value)
      }
    }
  }
  override fun title():String {
    return test.name
  }
}
fun main(args: Array<String>) {
  val trace = false
  val tested= mutableListOf<FacetsApp>()
  listOf(
    SimpleSurface(TargetTest.Textual, trace)
    ,SimpleSurface(TargetTest.TogglingLive, trace)
    ,SimpleSurface(TargetTest.Numeric, trace)
    ,SimpleSurface(TargetTest.Trigger, trace)
    ,SimpleSurface(TargetTest.Indexing, trace)
    ,SelectingSurface(TargetTest.Selecting, trace)
    ,ContentingSurface(trace)
    /*
    */
  ).forEach{ it ->
    it.buildSurface()
    tested.add(it)
  }
  Tracer.newTopped(SurfaceCore::class.simpleName!!).trace("Tested apps:",tested)
}
