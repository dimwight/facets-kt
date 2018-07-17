package fkt.facets
import fkt.facets.core.*
import fkt.java.TTarget
import fkt.facets.util.Tracer
import fkt.facets.util.Util
class FacetWorks(override var doTrace: Boolean, override val supplement:()->Unit={})
    : Facets, Tracer("Facets") {
  override fun newNumericTarget(title: String, coupler: NumericCoupler): TTarget {
    throw Error("Not implemented")
  }
  override fun updateTargetWithNotify(title: String, update: Any) {
    throw Error("Not implemented")
  }
  override fun trace(msg: String) {
    if (doTrace) println(">$msg")
  }
  override val times = object : Times {
    override fun setResetWait(millis: Int) {
      throw Error("Not implemented")
    }
    override fun elapsed(): Int {
      throw Error("Not implemented")
    }
    override fun traceElapsed(msg: String?) {
      throw Error("Not implemented")
    }
    override var doTime = false
  }
  override var activeContentTitle = "[Active Content Tree]"
  val notifiable = object : Notifiable {
    override fun notify(notice: Any) {
      val rt = rootTargeter?:throw Error("Null rootTargeter")
      trace("Notified with" + rt.title())
      rt.retarget(rt.target())
      callOnRetargeted()
      rt.retargetFacets()
    }
  }
  private lateinit var onRetargeted: (title: String) -> Any
  private val titleTargeters = HashMap<String, Targeter?>()
  private val titleTrees = HashMap<String, Targety>()
  private val root: IndexingFrame;
  private var rootTargeter: Targeter? = null
  init {
    activeContentTitle = "FacetsJava#" + identity() + ":Active Content"
    val indexing = Indexing("RootIndexing", object : IndexingCoupler() {
      private var thenTrees: Array<Targety>? = null
      override val getIndexables = fun(_: String): Array<out Any> {
        val trees = titleTrees.values.toTypedArray()
        if (!Util.arraysEqual(trees, thenTrees)) trace("> New trees: ", trees)
        thenTrees = trees
        return trees
      }
    })
    root = object : IndexingFrame("RootFrame", indexing) {
      override fun lazyElements(): Array<out Targety> {
        return arrayOf(
          Textual(activeContentTitle,object:TextualCoupler(){})
        )
      }
    }
    if (false) trace(" > Created trees root ", root)
  }
  override fun buildApp(app: FacetsApp) {
    onRetargeted = { title ->
      app.onRetargeted(title)
    }
    val trees = app.getContentTrees()
    if(trees is Array<*>)
      (trees as Array<TTarget>).forEach { t ->addContentTree(t)}
    else addContentTree(trees as TTarget)
    trace("Building targeter tree for root=${root.title()}")
    if (rootTargeter == null) rootTargeter = (root as TargetCore).newTargeter()
    val rt = rootTargeter!!
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
  override fun addContentTree(tree: TTarget) {
    titleTrees[(tree as Targety).title()] = tree
    root.indexing().setIndexed(tree)
  }
  override fun activateContentTree(title: String) {
    val tree = titleTrees[title] ?: throw Error("No tree for$title")
    root.indexing().setIndexed(tree)
    notifiable.notify(title)
  }
  override fun newTextualTarget(title: String, coupler: TextualCoupler): TTarget {
    val textual = Textual(title, coupler)
    trace("Created textual title=$title")
    return textual
  }
  override fun newTogglingTarget(title: String, coupler: TogglingCoupler): TTarget {
    val toggling = Toggling(title, coupler)
    trace("Created toggling title=$title")
    return toggling
  }
  override fun newTriggerTarget(title: String, coupler: TargetCoupler): TTarget {
    val trigger = TargetCore(title, coupler)
    trace("Created trigger title=$title")
    return trigger
  }
  override fun newTargetGroup(title: String, members: Array<out TTarget>): TTarget {
    val grouped = members.map { it as Targety }
    val group = TargetCore(title, grouped)
    trace("Created group title=$title")
    return group
  }
  private fun addTitleTargeters(t: Targeter) {
    val title = t.title()
    val elements: Array<Targeter> = (t as TargeterCore).titleElements()
    titleTargeters[title] = t
    trace("Added targeter: title=$title: elements=${elements.size}")
    elements.forEach { e -> addTitleTargeters(e) }
  }
  override fun attachFacet(title: String, updater: FacetUpdater){
    val t = titleTargeters[title] ?: throw Error("No targeter for $title")
    trace("Attaching facet: title=$title")
    val facet: Facet = object : Facet {
      override fun retarget(ta: Targety) {
        trace("Facet retargeted title=${ta.title()} state=${ta.state()}")
        updater(ta.state())
      }
    }
    t.attachFacet(facet)
  }
  override fun updateTargetState(title: String, update: Any){
    titleTarget(title).updateState(update)
    notifiable.notify(title)
  }
  override fun getTargetState(title: String): Any? {
    return try {
      titleTarget(title).state()
    }catch (e:Error){
      null
    }
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
  private fun titleTarget(title: String): Targety =
     (titleTargeters[title]?:throw Error("No targeter for$title")).target()
  override fun newIndexingTarget(title: String, coupler: IndexingCoupler): Targety {
    val indexing = Indexing(title, coupler)
    trace("Created indexing title=" + title)
    return indexing
  }
  override fun getIndexingState(title: String): IndexingState {
    val i: Indexing = titleTarget(title) as Indexing
    if (i == null) throw Error("No target for title=" + title)
    else return object : IndexingState() {
      override var uiSelectables = i.uiSelectables()
      override var indexed = i.indexed()
    }
  }
  var indexingFrames = 0
  override fun newIndexingFrame(p: IndexingFramePolicy): Targety {
    val frameTitle = p.frameTitle ?: "IndexingFrame"+indexingFrames++
    val indexingTitle = p.indexingTitle ?: "$frameTitle.Indexing"
    val indexing = Indexing(indexingTitle, object : IndexingCoupler() {
      override val getIndexables = { _: String -> p.getIndexables() }
      override val newUiSelectable = { indexable: Any ->
        p.newUiSelectable?.invoke(indexable) ?: throw Error()
      }
    })
    trace("Created indexing$indexingTitle")
    val frame = object : IndexingFrame(frameTitle!!, indexing) {
      override fun lazyElements(): Array<Targety> =
        (p.newFrameTargets?.invoke() ?: arrayOf()) as Array<Targety>
      override fun newIndexedTargets(indexed: Any): Targety {
        val title = p.newIndexedTreeTitle?.invoke(indexed) ?: title()+"|indexed"
        return (p.newIndexedTree?.invoke(indexed, title) ?: TargetCore(title)) as Targety
      }
    }
    trace("Created indexing frame$frameTitle")
    return frame
  }
}
