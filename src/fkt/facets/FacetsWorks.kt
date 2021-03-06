package fkt.facets

import fkt.facets.core.Facet
import fkt.facets.core.Indexing
import fkt.facets.core.IndexingFrame
import fkt.facets.core.Notifiable
import fkt.facets.core.Numeric
import fkt.facets.core.TargetCore
import fkt.facets.core.Targeter
import fkt.facets.core.TargeterCore
import fkt.facets.core.Targety
import fkt.facets.core.Textual
import fkt.facets.core.Toggling
import fkt.facets.util.Debug
import fkt.facets.util.Tracer

class FacetsWorks(override val doTrace: Boolean,
                  private val app: FacetsApp)
  : Facets, Tracer("Facets") {
  private val titleTargeters = mutableMapOf<String, Targeter?>()
  private val titleTrees = mutableMapOf<String, Targety>()
  private val root: IndexingFrame
  private var rootTargeter: Targeter? = null
  private val notifiable = object : Notifiable {
    override fun notify(notice: Any) {
      val rt = rootTargeter ?: throw Error("Null rootTargeter")
      trace("Notified with" + rt.title)
      rt.retarget(rt.target)
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
      private var thenTrees: Array<Targety>? = null
      override fun getIndexables (): List<Any> {
        val trees = titleTrees.values.toTypedArray()
        if (!trees.contentEquals(thenTrees?: arrayOf())) trace("New trees: ",trees)
        thenTrees = trees
        return trees.toList()
      }
    })
    root = object : IndexingFrame("RootFrame", indexing) {
      override fun lazyElements(): List<Targety> {
        return listOf(
          Textual(activeContentTitle, object : TextualCoupler() {
            override val getText = {_:String-> indexedContentTitle() }
          })
        )
      }
    }
  }

  private fun indexedContentTitle()=root.indexedTarget().title

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
    trace("Building trees for root ", root)
    val trees = app.newContentTrees()
    trees.forEach { attachContentTree(it) }
    trace("Building targeter tree for root=${root.title}")
    if(false)root.indexing().setIndexed(trees[0])
    if (rootTargeter == null) rootTargeter = (root as TargetCore).newTargeter()
    val rt = rootTargeter!!
    rt.setNotifiable(notifiable)
    rt.retarget(root)
    addTitleTargeters(rt)
    trace("Added targeter titles: ", titleTargeters.size)
    callOnRetargeted()
    app.buildLayout()
    if(false)notifiable.notify("")
  }

  override fun attachContentTree(tree: Target) {
    val title = (tree as Targety).title
    titleTrees[title] = tree
    activateContentTree(title)
  }

  override fun activateContentTree(title: String) {
    val tree = titleTrees[title] ?: throw Error("No tree for $title")
    root.indexing().setIndexed(tree)
    if(false)notifiable.notify(title)
  }

  override fun newTextualTarget(title: String, c: TextualCoupler): Target {
    val textual = Textual(title, c)
    trace("Created textual title=$title")
    return textual
  }

  override fun newTogglingTarget(title: String, c: TogglingCoupler): Target {
    val toggling = Toggling(title, c)
    trace("Created toggling title=$title")
    return toggling
  }

  override fun newTriggerTarget(title: String, c: TargetCoupler): Target {
    val trigger = TargetCore(title, c)
    trigger.state="Trigger"
    trigger.live=c.passLive
    trace("Created trigger title=$title")
    return trigger
  }

  override fun newNumericTarget(title: String, c: NumericCoupler): Target {
    return Numeric(title, c)
  }

  override fun newTargetGroup(title: String, members: List<Target>): Target {
    val grouped = members.map { it as Targety }
    val group = TargetCore(title, grouped)
    if(false)trace("Created group title=$title elements=${group.elements.size}")
    return group
  }

  override fun newIndexingTarget(title: String, c: IndexingCoupler): Target {
    val indexing = Indexing(title, c)
    if (false && c.passIndex == null) indexing.index=0
    trace("Created indexing title=$title")
    return indexing
  }

  override fun getIndexingState(title: String): IndexingState {
    val i: Indexing = titleTarget(title) as Indexing
    return object : IndexingState() {
      override var uiSelectables: List<String> = i.uiSelectables()
      override var indexed = i.indexed()
    }
  }

  override fun newIndexingFrame(p: IndexingFramePolicy): Target {
    val frameTitle = p.frameTitle ?: "IndexingFrame${indexingFrames++}"
    val indexingTitle = p.indexingTitle ?: "$frameTitle.Indexing"
    val indexing = Indexing(indexingTitle, object : IndexingCoupler() {
      override fun getIndexables()= p.getIndexables() 
      override val newUiSelectable = { indexable: Any ->
        p.newUiSelectable?.invoke(indexable) ?: throw Error()
      }
    })
    trace("Created indexing$indexingTitle")
    val frame = object : IndexingFrame(frameTitle, indexing) {
      override fun lazyElements(): List<Targety> {
        if(false)throw Error("Not implemented in "+Debug.info(this))
        return (p.newFrameTargets?.invoke() ?: listOf()).map { it as Targety }
      }

      override fun newIndexedTargets(indexed: Any): Targety {
        val title = p.newIndexedTreeTitle?.invoke(indexed) ?: "$title|indexed"
        return (p.newIndexedTree?.invoke(indexed, title) ?: TargetCore(title)) as Targety
      }
    }
    trace("Created indexing frame $frameTitle")
    return frame
  }

  override fun attachFacet(title: String, updater: FacetUpdater) {
    val t = titleTargeters[title] ?: throw Error("No targeter for $title")
    trace("Attaching facet for title=$title")
    t.attachFacet(object : Facet {
      override fun retarget(target: Targety) {
        trace("Facet retargeted title=${target.title} state=${target.state}")
        updater(target.state)
      }
    })
  }

  override fun updateTarget(title: String, state: Any?) {
    val target = titleTarget(title)
    if(state!=null)target.state = when (target) {
      is Textual -> state as String
      is Toggling -> state as Boolean
      is Indexing -> state as Int
      is Numeric -> state as Double
      else -> state//Trigger
    }
    if(false)target.notifyParent() else notifiable.notify(title)
  }

  override fun getTargetState(title: String): Any? {
    return try {
      titleTarget(title).state
    } catch (e: Error) {
      null
    }
  }

  override fun setTargetLive(title: String, live: Boolean) {
    titleTarget(title).live=live
  }

  override fun isTargetLive(title: String): Boolean {
    return titleTarget(title).live
  }

  private fun callOnRetargeted() {
    val title = root.indexedTarget().title
    trace("Calling onRetargeted with active=$title")
    app.onRetargeted(title)
  }

  private fun addTitleTargeters(t: Targeter) {
    val title = t.title
    val elements = (t as TargeterCore).titleElements
    titleTargeters[title] = t
    if (false) trace("Added targeter: title=$title: elements=${elements.size}")
    elements.forEach { e -> addTitleTargeters(e) }
  }

  private fun titleTarget(title: String): Targety =
    (titleTargeters[title] ?: throw Error("No targeter for $title")).target

  private var indexingFrames = 0
}
