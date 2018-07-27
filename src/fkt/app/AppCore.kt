package fkt.app
import fkt.facets.Facets
import fkt.facets.FacetsApp
import fkt.facets.newFacets
import fkt.facets.util.Titled
import fkt.facets.util.Tracer
import fkt.app.SelectingTitles as Selectings
enum class TargetTest {
	Textual, TogglingLive, Indexing, Numeric, Trigger, Selecting, Contenting;
	val isSimple:Boolean get()=this.ordinal< Selecting.ordinal
	companion object {
		val simpleValues = listOf(Textual, TogglingLive, Indexing, Numeric, Trigger)
	}
}
abstract class AppCore(trace:Boolean, test: TargetTest)
		:Tracer(test.name), Titled, FacetsApp {
  override fun doTraceMsg(msg: String) {
    if(true&&!msg.contains(">"))super.doTraceMsg(msg)
  }
  /**
   Internal instance
   */
  val facets: Facets = newFacets(trace,this)
  val test: TargetTest = if (true || test == TargetTest.Selecting) test else TargetTest.Indexing

  init{
    facets.times.doTime = false || facets.doTrace
  }

  /**
   Calls [Facets.buildApp] on the private instance
   */
  open fun buildSurface() {
    facets.buildApp(this)
  }
  override fun onRetargeted(activeTitle: String) {}
  protected fun generateFacets(vararg titles:String) {
    for (title in titles){
      trace(" > Generating facet for title=", title)
      facets.attachFacet(title) { value->
        trace((" > Facet for $title updated: value="), value)
      }
    }
  }
  override val title=test.name
}
fun main(args: Array<String>) {
  val trace = true
  val tested= mutableListOf<FacetsApp>()
  listOf(
    SimpleApp(TargetTest.Textual, trace)
    , SimpleApp(TargetTest.TogglingLive, trace)
    , SimpleApp(TargetTest.Numeric, trace)
    , SimpleApp(TargetTest.Trigger, trace)
    , SimpleApp(TargetTest.Indexing, trace)
    , SelectingApp(TargetTest.Selecting, trace)
    , ContentingApp(trace)
    /*
    */
  ).forEach{ it ->
    it.buildSurface()
    tested.add(it)
  }
  Tracer.newTopped(AppCore::class.simpleName!!).trace("Tested apps:",tested)
}
