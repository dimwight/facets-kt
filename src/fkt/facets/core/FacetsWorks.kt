package fkt.facets.core

import fkt.facets.*
import fkt.facets.util.Debug
import fkt.facets.util.Tracer
import fkt.facets.util.Util

class FacetsWorks(override var doTrace: Boolean, override val supplement: () -> Unit = {})
  : Facets, Tracer("Facets") {
  private val titleTargeters = HashMap<String, Targeter?>()
  private val titleTrees = HashMap<String, Targety>()
  private val root: IndexingFrame
  private var rootTargeter: Targeter? = null
  private lateinit var onRetargeted: (title: String) -> Any
  private val notifiable = object : Notifiable {
    override fun notify(notice: Any) {
      val rt = rootTargeter ?: throw Error("Null rootTargeter")
      trace("Notified with" + rt.title)
      rt.retarget(rt.target())
      callOnRetargeted()
      rt.retargetFacets()
    }
  }

  override fun doTraceMsg(msg: String) {
    if (doTrace) println(">$msg")
  }

  override var activeContentTitle = Debug.info(this) + ":Active Content"

  init {
    val indexing = Indexing("RootIndexing", object : IndexingCoupler() {
      private var thenTrees: Collection<Targety>? = null
      override val getIndexables = fun(_: String): List<*> {
        val trees = titleTrees.values
        if (!Util.arraysEqual(trees.toTypedArray(), thenTrees?.toTypedArray()?: arrayOf())) trace("New trees: size=${trees.size}")
        thenTrees = trees
        return trees.toList()
      }
    })
    root = object : IndexingFrame("RootFrame", indexing) {
      override fun lazyElements(): List<Targety> {
        return listOf(
          Textual(activeContentTitle, object : TextualCoupler() {
            override val getText: ((String) -> String)?
              get() = { indexedTargetTitle() }
          })
        )
      }
    }
    trace("Created trees root ", root)
  }

  private val indexedTargetTitle={root.indexedTarget().title}

  override val times = object : Times {
    private var resetWait = 1000

    override fun setResetWait(millis: Int) {
      resetWait = millis
    }

    override fun elapsed(): Int {
      throw Error("Not implemented")
    }

    override fun traceElapsed(msg: String?) {
      throw Error("Not implemented")
    }

    override var doTime = false
  }

  override fun buildApp(app: FacetsApp) {
    onRetargeted = { title ->
      app.onRetargeted(title)
    }
    app.getContentTrees().forEach { addContentTree(it) }
    trace("Building targeter tree for root=${root.title}")
    if (rootTargeter == null) rootTargeter = (root as TargetCore).newTargeter()
    val rt = rootTargeter!!
    rt.setNotifiable(notifiable)
    rt.retarget(root)
    addTitleTargeters(rt)
    trace("Added targeter titles: ", titleTargeters.values)
    callOnRetargeted()
    app.buildLayout()
  }

  override fun addContentTree(tree: TTarget) {
    titleTrees[(tree as Targety).title] = tree
    root.indexing().setIndexed(tree)
  }

  override fun activateContentTree(title: String) {
    val tree = titleTrees[title] ?: throw Error("No tree for$title")
    root.indexing().setIndexed(tree)
    notifiable.notify(title)
  }

  override fun newTextualTarget(title: String, c: TextualCoupler): TTarget {
    val textual = Textual(title, c)
    if(false)trace("Created textual title=$title")
    return textual
  }

  override fun newTogglingTarget(title: String, c: TogglingCoupler): TTarget {
    val toggling = Toggling(title, c)
    trace("Created toggling title=$title")
    return toggling
  }

  override fun newTriggerTarget(title: String, c: TargetCoupler): TTarget {
    val trigger = TargetCore(title, c)
    trace("Created trigger title=$title")
    return trigger
  }

  override fun newNumericTarget(title: String, c: NumericCoupler): TTarget {
    return Numeric(title, c)
  }

  override fun newTargetGroup(title: String, members: List<TTarget>): TTarget {
    val grouped = members.map { it as Targety }
    val group = TargetCore(title, grouped)
    trace("Created group title=$title elements=${group.elements().size}")
    return group
  }

  override fun newIndexingTarget(title: String, c: IndexingCoupler): Targety {
    val indexing = Indexing(title, c)
    if (false && c.passIndex == null) indexing.setIndex(0)
    trace("Created indexing title=$title")
    return indexing
  }

  override fun getIndexingState(title: String): IndexingState {
    val i: Indexing = titleTarget(title) as Indexing
    return object : IndexingState() {
      override var uiSelectables = i.uiSelectables()
      override var indexed = i.indexed()
    }
  }

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
    val frame = object : IndexingFrame(frameTitle, indexing) {
      override fun lazyElements(): List<Targety> {
        val targets = p.newFrameTargets?.invoke() ?: return listOf()
        return targets.map { it as Targety }
      }

      override fun newIndexedTargets(indexed: Any): Targety {
        val title = p.newIndexedTreeTitle?.invoke(indexed) ?: title+"|indexed"
        return (p.newIndexedTree?.invoke(indexed, title) ?: TargetCore(title)) as Targety
      }
    }
    trace("Created indexing frame$frameTitle")
    return frame
  }

  override fun attachFacet(title: String, updater: FacetUpdater) {
    val t = titleTargeters[title] ?: throw Error("No targeter for $title")
    trace("Attaching facet: title=$title")
    val facet: Facet = object : Facet {
      override fun retarget(target: Targety) {
        trace("Facet retargeted title=${target.title} state=${target.state()}")
        updater(target.state())
      }
    }
    t.attachFacet(facet)
  }

  override fun updateTargetState(title: String, update: Any) {
    titleTarget(title).updateState(update)
    notifiable.notify(title)
  }

  override fun updateTargetWithNotify(title: String, update: Any) {
    updateTargetState(title, update)
  }

  override fun notifyTargetUpdated(title: String) {
    val target = titleTarget(title)
    target.notifyParent()
  }

  override fun getTargetState(title: String): Any? {
    return try {
      titleTarget(title).state()
    } catch (e: Error) {
      null
    }
  }

  override fun setTargetLive(title: String, live: Boolean) {
    titleTarget(title).setLive(live)
  }

  override fun isTargetLive(title: String): Boolean {
    return titleTarget(title).isLive()
  }

  private fun callOnRetargeted() {
    val title = root.title
    trace("Calling disableAll with active=$title")
    onRetargeted(title)
  }

  private fun addTitleTargeters(t: Targeter) {
    val title = t.title
    val elements = (t as TargeterCore).titleElements()
    titleTargeters[title] = t
    if (true) trace("Added targeter: title=$title: elements=${elements.size}")
    elements.forEach { e -> addTitleTargeters(e) }
  }

  private fun titleTarget(title: String): Targety =
    (titleTargeters[title] ?: throw Error("No targeter for$title")).target()

  private var indexingFrames = 0
}
