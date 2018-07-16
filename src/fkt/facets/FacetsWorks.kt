package fkt.facets
import fkt.facets.core.*
import fkt.facets.core.Target
import fkt.facets.util.Tracer
import fkt.facets.util.Util
import fkt.java.SIndexing
import fkt.java.STarget

fun newInstance(trace: Boolean): Facets {
  return FacetsWorks(trace)
}
class FacetsWorks(override var doTrace: Boolean) : Facets, Tracer("Facets") {
  override fun supplement() {}
  override fun newNumericTarget(title: String, coupler: NumericCoupler): Target {
    throw Error("Not implemented")
  }
  override fun updateTargetWithNotify(title: String, update: SimpleState) {
    throw Error("Not implemented")
  }
  override fun newInstance(trace: Boolean): Facets {
    throw Error("Not implemented")
  }
  override fun trace(msg: String) {
    if (doTrace) print(">$msg")
  }
  override val times = object : Times {
    override fun setResetWait(millis: Int) {
      throw Error("Not implemented")
    }
    override fun elapsed(): Int {
      throw Error("Not implemented")
    }
    override fun traceElapsed(msg: String) {
      throw Error("Not implemented")
    }
    override var doTime = false
  }
  override var activeContentTitle = "[Active Content Tree]"
  var notifiable: Notifiable = object : Notifiable {
    override fun notify(notice: Any) {
      val rt=rootTargeter!!
      trace("Notified with" + rt.title())
      rt.retarget(rt.target())
      callOnRetargeted()
      rt.retargetFacets()
    }
  }
  lateinit var onRetargeted: (title: String) -> Any
  val titleTargeters = HashMap<String, Targeter>()
  val titleTrees = HashMap<String, Targety>()
  lateinit var root: IndexingFrame;
  var rootTargeter: Targeter?=null
  init {
    activeContentTitle = "FacetsWorks#" + identity() + ":Active Content"
    val indexing = Indexing("RootIndexing", object : IndexingCoupler() {
      private var thenTrees: Array<Targety>? = null
      override val getIndexables=fun(_:String):Array<out Any>{
        val trees = titleTrees.values.toTypedArray()
        if (!Util.arraysEqual(trees, thenTrees)) trace("> New trees: ", trees)
        thenTrees = trees
        return trees
      }
    })
    root = object : IndexingFrame("RootFrame", indexing) {
      override fun lazyElements(): Array<out STarget> {
        return arrayOf(        )}
    }
    if (false) trace(" > Created trees root ", root)
  }
  override fun buildApp(app: FacetsApp) {
    onRetargeted = { title ->
      app.onRetargeted(title)
    }
    val trees = app.getContentTrees()
    (trees as Array<Targety>).forEach{ t ->
      addContentTree(t)
    }
    trace("Building targeter tree for root${root?.title()?:throw Error("No root")}")
    if (rootTargeter == null) rootTargeter = (root as TargetCore).newTargeter()
    val rt=rootTargeter!!
    rt.setNotifiable(notifiable)
    rt.retarget(root)
    addTitleTargeters(rt)
    callOnRetargeted()
    app.buildLayout()
  }
  private fun callOnRetargeted() {
    val title = root.title()
    trace("Calling disableAll with active=$title")
    onRetargeted(title)
  }
  override fun addContentTree(tree: Target) {
    titleTrees[(tree as Targety).title()] = tree
    root.indexing().setIndexed(tree)
  }
  override fun activateContentTree(title: String) {
    val tree = titleTrees[title] ?: throw Error("No tree for$title")
    root.indexing().setIndexed(tree)
    notifiable.notify(title)
  }
  override fun newTextualTarget(title: String, coupler: TextualCoupler): Target {
    val textual = Textual(title, coupler)
    trace("Created textual title=$title")
    return textual
  }
  override fun newTogglingTarget(title: String, coupler: TogglingCoupler): Target {
    val toggling = Toggling(title, coupler)
    trace("Created toggling title=$title")
    return toggling
  }
  override fun newTriggerTarget(title: String, coupler: TargetCoupler): Target {
    val trigger = TargetCore(title, coupler)
    trace("Created trigger title=$title")
    return trigger
  }
  override fun newTargetGroup(title: String, members: Array<Target>): Target {
    return TargetCore(title, members as Array<Targety>)
  }
  private fun addTitleTargeters(t: Targeter) {
    val title = t.title()
    val elements: Array<Targeter> = (t as TargeterCore).titleElements()
    titleTargeters.set(title, t)
    trace("Added targeter: title=" + title + ": elements=" + elements.size)
    elements.forEach { e ->addTitleTargeters(e)}
  }
  override fun attachFacet(title: String, updater: FacetUpdater): Unit {
    val t: Targeter = titleTargeters.get(title) as Targeter
    if (t == null) throw Error("No targeter for" + title)
    trace("Attaching facet: title=" + title)
    val facet: Facet = object : Facet {
      override fun retarget(ta: Targety) {
        trace("Facet retargeted title=" + ta.title())
// +' state='+ta.state()
        updater(ta.state())
      }
    }
    t.attachFacet(facet)
  }
  override fun updateTargetState(title: String, update: SimpleState): Unit {
    titleTarget(title).updateState(update)
    notifiable.notify(title)
  }
  override fun getTargetState(title: String): SimpleState {
    return titleTarget(title).state()
  }
  override fun isTargetLive(title: String): Boolean {
    return titleTarget(title).isLive()
  }
  override fun setTargetLive(title: String, live: Boolean) {
    titleTarget(title).setLive(live)
  }
  override fun notifyTargetUpdated(title: String) {
    val target = titleTarget(title)
    target.notifyParent()
  }
  fun titleTarget(title: String): Targety {
    val got = titleTargeters.get(title)
    if (got == null) throw Error("No targeter for" + title)
    return got.target()
  }
  override fun newIndexingTarget(title: String, coupler: IndexingCoupler): Targety {
    val indexing = Indexing(title, coupler)
    trace("Created indexing title=" + title)
    return indexing
  }
  override fun getIndexingState(title: String): IndexingState {
    val i: Indexing = titleTarget(title) as Indexing
    if (i == null) throw Error("No target for title=" + title)
    else return object : IndexingState {
      override var uiSelectables = i.uiSelectables()
      override var indexed = i.indexed()
    }
  }
  var indexingFrames = 0
  override fun newIndexingFrame(p: IndexingFramePolicy): Targety {
    val frameTitle = if (p.frameTitle != null) p.frameTitle else "IndexingFrame" + indexingFrames
// ++
    val indexingTitle = if (p.indexingTitle != null) p.indexingTitle else frameTitle + ".Indexing"
    val indexing = Indexing(indexingTitle!!, object : IndexingCoupler {
      override fun targetStateUpdated(state: SimpleState, title: String) {
      }
      override var passIndex = 0
      override fun getIndexables(title: String) =
        p.getIndexables()
      // newUiSelectable:indexable=>!p.newUiSelectable?null:p.newUiSelectable(indexable)
      override fun newUiSelectable(indexable: Any) =
        p.newUiSelectable(indexable)
    })
    trace("Created indexing" + indexingTitle)
    val frame = object : IndexingFrame(frameTitle!!, indexing) {
      override fun lazyElements(): Array<Targety> {
        return if (p.newFrameTargets() != null) p.newFrameTargets() as Array<Targety> else arrayOf()
      }
      override fun newIndexedTargets(indexed: Any): Targety {
        val titler = p.newIndexedTreeTitle(indexed)
        val title = if (titler != null) titler else title()
// +'|indexed'
        val newTree = p.newIndexedTree(indexed, title)
        return if (newTree != null) newTree as Targety else TargetCore(title)
      }
    }
// (frameTitle, indexing)
    trace("Created indexing frame" + frameTitle)
    return frame
  }
}
