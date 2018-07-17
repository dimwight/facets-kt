package fkt
import fkt.facets.core.*
import fkt.java.util.NumberPolicy
import fkt.java.*
import fkt.java.IndexingFrame
import fkt.java.Notifiable
import fkt.java.TargetCore
import fkt.java.TargeterCore
import fkt.java.util.*
class TimeWorks(private var then: Long = 0, private var start: Long = 0):Times {
  override fun setResetWait(millis: Int) {
    resetWait=millis.toLong()
  }
  override fun elapsed(): Int {
    throw Error("Not implemented")
  }
  override var doTime = false
  private var restarted: Boolean = false
  private val debug = false
  private var resetWait = 1000L
    set(millis){
      if (debug) Util.printOut("Times.resetWait=", millis)
      start = nowMillis
      doTime = true
      field = millis
    }
  private val elapsed:Long get(){
    val now = nowMillis
    if (now - then > resetWait) {
      start = now
      restarted = true
      if (debug) Util.printOut("TimeWorks: reset resetWait=$resetWait")
    } else restarted = false
    then = now
    return then - start
  }
  override fun traceElapsed(msg: String?) {
    if (!doTime) {
      if (!Debug.trace) {
        then = nowMillis
        start = then
        if (debug) Util.printOut("TimeWorks.printElapsed: times=", doTime)
      }
      return
    }
    val elapsed = elapsed
    val elapsedText = if (true && elapsed > 5 * 1000)
      (Util.fxs(elapsed / 1000.0))
    else ("" + elapsed)
    val toPrint = (if (restarted) "\n" else "") +
      elapsedText + (if (msg != null) ":\t$msg" else "")
    Util.printOut(toPrint)
  }
  private val nowMillis get()= System.nanoTime()/1000
}
class FacetsJava(trace: Boolean) : Tracer("Facets"),Facets {
  override val supplement={}
  override val activeContentTitle: String
  override val times = TimeWorks()
  override var doTrace = false
  private val titleTargeters = HashMap<String, STargeter>()
  private val titleTrees = HashMap<String, STarget>()
  private lateinit var root: IndexingFrame
  private val notifiable = object : Notifiable {
    override fun notify(notice: Any) {
      var msg = "> Surface for " + Debug.info(rootTargeter) + " notified by " + notice
      if (times.doTime) times.traceElapsed(msg)
      else trace(msg)
      val target = rootTargeter.target()
      rootTargeter.retarget(target)
      msg = "> Targeters retargeted on " + Debug.info(target)
      if (times.doTime) times.traceElapsed(msg)
      else trace(msg)
      if (false) putTitleTargeters(rootTargeter)
      callOnRetargeted()
      rootTargeter.retargetFacets()
      msg = "> FacetsJava retargeted in " + Debug.info(rootTargeter)
      if (times.doTime) times.traceElapsed(msg)
      else trace(msg)
    }
    override fun title(): String {
      throw RuntimeException("Not implemented in " + this)
    }
  }
  private lateinit var rootTargeter: STargeter
  private lateinit var onRetargeted: (String) -> Unit
  private var indexingFrames: Int = 0
  private fun putTitleTargeters(t: STargeter) {
    val title = t.title()
    val then = titleTargeters[title]
    titleTargeters[title] = t
    val elements = (t as TargeterCore).titleElements()
    if (false && then == null)trace(("> Added targeter: title=$title" +
      (if (true)(": elements=" + elements.size)
      else (": titleTargeters=" + titleTargeters.values.size))))
    for (e in elements) putTitleTargeters(e)
  }
  private fun titleTarget(title: String): STarget? =
    titleTargeters[title]?.target()
  private fun updatedTarget(target: STarget, c: TargetCoupler) {
    val title = target.title()
    trace(" > Updated target ", target)
    val state = target.state
    c.targetStateUpdated?.invoke(state, title)
  }
  private fun callOnRetargeted() {
    val title = (root.indexedTarget()as STarget).title()
    trace(" > Calling onRetargeted with active=$title")
    onRetargeted(title)
  }
  override fun doTraceMsg(msg: String) {
    if (doTrace || (Debug.trace && msg.startsWith(">>"))) super.doTraceMsg(msg)
  }
  init {
    this.doTrace = trace
    activeContentTitle = "FacetsJava#" + identity() + ":Active Content"
    val indexing = SIndexing("RootIndexing", object : SIndexing.Coupler() {
      private var thenTrees: Array<STarget>? = null
      override fun getIndexables(i: SIndexing): Array<STarget> {
        val trees = titleTrees.values.toTypedArray()
        if (!Util.arraysEqual(trees, thenTrees)) trace("> New trees: ", trees)
        thenTrees = trees
        return trees
      }
    })
    root = object : IndexingFrame("RootFrame", indexing) {
      override fun lazyElements(): Array<out STarget> =
        arrayOf(STextual(activeContentTitle, object : STextual.Coupler() {
          override fun getText(t: STextual): String =
            (root.indexedTarget() as STarget).title()
        })
        )
    }
    if (false) trace(" > Created trees root ", root)
  }
  override fun buildApp(app: FacetsApp) {
    this.onRetargeted = { title -> app.onRetargeted(title) }
    trace("Building surface...")
    val trees = app.getContentTrees()
    if (trees is Array<*>)
      for (tree in trees) addContentTree(tree as STarget)
    else addContentTree(trees as STarget)
    trace(" > Building targeter tree for root=", root)
    rootTargeter = (root as TargetCore).newTargeter()
    rootTargeter.setNotifiable(notifiable)
    rootTargeter.retarget(root)
    putTitleTargeters(rootTargeter)
    trace(" > Created targeters=",titleTargeters.size)
    callOnRetargeted()
    this.trace("Built targets, created targeters")
    app.buildLayout()
    trace("Attached and laid out facets")
    trace("Surface built.")
  }
  override fun addContentTree(tree: TTarget) {
    val title = (tree as STarget).title()
    trace(" > Adding content title=$title")
    titleTrees[title] = tree
    root.indexing().setIndexed(tree)
  }
  override fun activateContentTree(title: String) {
    trace(" > Activating content title=$title")
    val tree = titleTrees[title] ?:throw IllegalStateException("Null tree in " + this)
    root.indexing().setIndexed(tree)
    notifiable.notify(root)
  }
  override fun newTextualTarget(title: String, c: TextualCoupler): TTarget {
    if (c.passText == null && c.getText == null)
      throw IllegalArgumentException("No way to get text in $title")
    val textual = STextual(title, object : STextual.Coupler() {
      override fun textSet(target: STextual) {
        updatedTarget(target, c)
      }
      override fun getText(t: STextual): String {
        val getText = c.getText
        val title_ = t.title()
        return getText?.invoke(title_) ?:
        throw IllegalStateException("Null getText for $title_")
      }
      override fun isValidText(t: STextual, text: String): Boolean {
        return c.isValidText?.invoke(t.title(), text) ?: true
      }
    })
    val passText: String? = c.passText
    if (passText != null) textual.setText(passText)
    trace(" > Created textual ", textual)
    return textual
  }
  override fun newTogglingTarget(title: String, c: TogglingCoupler): TTarget {
    val toggling = SToggling(title, c.passSet, object : SToggling.Coupler() {
      override fun stateSet(target: SToggling) {
        updatedTarget(target, c)
      }
    })
    trace(" > Created toggling ", toggling)
    return toggling
  }
  override fun newNumericTarget(title: String, c: NumericCoupler): TTarget {
    val numeric = SNumeric(title, c.passValue, object : SNumeric.Coupler() {
      override fun valueSet(n: SNumeric) {
        updatedTarget(n, c)
      }
      override fun policy(n: SNumeric): NumberPolicy {
        val min = c.min ?: Double.MIN_VALUE
        val max = c.max ?: Double.MAX_VALUE
        return NumberPolicy(min, max)
      }
    })
    trace(" > Created numeric ", numeric)
    return numeric
  }
  override fun newTriggerTarget(title: String, c: TargetCoupler): TTarget {
    val trigger = STrigger(title, object : STrigger.Coupler() {
      override fun fired(t: STrigger) {
        updatedTarget(t, c)
      }
    })
    trace(" > Created trigger ", trigger)
    return trigger
  }
  override fun newTargetGroup(title: String, members: Array<out TTarget>): TTarget {
    val grouped=members.map { it as STarget }.toTypedArray()
    val group = TargetCore(title, *grouped)
    trace(" > Created target group " + Debug.info(group) + " ", members)
    return group
  }
  override fun newIndexingTarget(title: String, c: IndexingCoupler): TTarget {
    val indexing = SIndexing(title, object : SIndexing.Coupler() {
      override fun getIndexables(i: SIndexing): Array<out Any> {
        return c.getIndexables(i.title())
      }
      override fun indexSet(target: SIndexing) {
        updatedTarget(target, c)
      }
      override fun getFacetSelectables(i: SIndexing): Array<String> {
        val getter: ((Any) -> String) = c.newUiSelectable
          ?: return super.getFacetSelectables(i)
        val selectables = ArrayList<String>()
        for (each in i.indexables()) selectables.add(getter(each))
        return selectables.toTypedArray()
      }
    })
    indexing.setIndex(c.passIndex ?: 0)
    trace(" > Created indexing ", indexing)
    return indexing
  }
  override fun getIndexingState(title: String): IndexingState {
    val titleTarget = titleTarget(title)
      ?:throw IllegalStateException("Null target for $title")
    val indexing = titleTarget as SIndexing
    return object : IndexingState() {
      override val uiSelectables = indexing.facetSelectables()
      override val indexed = indexing.indexed()
    }
  }
  override fun newIndexingFrame(p: IndexingFramePolicy): TTarget {
    val frameTitle = p.frameTitle ?: "IndexingFrame" + indexingFrames++
    val indexingTitle = p.indexingTitle ?: "$frameTitle.Indexing"
    val indexing = SIndexing(indexingTitle, object : SIndexing.Coupler() {
      private var thenIndexables: Array<out Any>? = null
      private var thenSelectables: Array<String>? = null
      override fun getIndexables(i: SIndexing): Array<out Any> {
        val got = p.getIndexables()
        if(!Util.arraysEqual(got, thenIndexables))
          trace("> Got new indexables in " + Debug.info(i) + ": ", got)
        thenIndexables = got
        return got
      }
      override fun getFacetSelectables(i: SIndexing): Array<String> {
        val getter = p.newUiSelectable ?: return super.getFacetSelectables(i)
        val selectables = i.indexables().map{getter(it)}
        val got = selectables.toTypedArray()
        if (!Util.arraysEqual(got, thenSelectables))
          trace("> Got new selectables in " + Debug.info(i) + ": ", got)
        thenSelectables = got
        return got
      }
    })
    indexing.setIndex(0)
    class LocalIndexingFrame(title: String,
                                     indexing: SIndexing,
                                     private val p: IndexingFramePolicy)
      : IndexingFrame(title, indexing) {
      override fun lazyElements(): Array<STarget> {
        val got = p.newFrameTargets?.invoke() ?: arrayOf()
        return STarget.newTargets(got) ?: arrayOf()
      }
      override fun newIndexedTargets(indexed: Any): TTarget {
        val indexedTargetsTitle = p.newIndexedTreeTitle?.invoke(indexed)
          ?: title() + "|indexed"
        return p.newIndexedTree?.invoke(indexed, indexedTargetsTitle)
          ?: TargetCore(indexedTargetsTitle)
      }
    }
    val frame = LocalIndexingFrame(frameTitle, indexing, p)
    trace(" > Created indexing frame ", frame)
    return frame
  }
  override fun attachFacet(title: String, updater: FacetUpdater) {
    val targeter = titleTargeters[title]
      ?:throw IllegalArgumentException("Null targeter for $title")
    val facet = object : SFacet {
      private val id = Tracer.ids++
      override fun retarget(target: STarget) {
        val state = target.state
        val title_ = target.title()
        trace(" > Updating UI for $title_ with state=", state)
        updater.invoke(state)
      }
      override fun toString(): String {
        return "#$id"
      }
    }
    trace(" > Attaching facet $facet to", targeter)
    targeter.attachFacet(facet)
  }
  override fun updateTargetState(title: String, update: Any) {
    trace(" > Updating target state for title=$title update=", update)
    titleTarget(title)?.updateState(update)
      ?:throw IllegalStateException("Null target for $title")
  }
  override fun notifyTargetUpdated(title: String) =
    titleTarget(title)?.notifyParent()
      ?:throw IllegalStateException("Null target for $title")
  override fun updateTargetWithNotify(title: String, update: Any) {
    updateTargetState(title, update)
    notifyTargetUpdated(title)
  }
  override fun getTargetState(title: String): Any? {
    val state = titleTarget(title)?.state
    trace(" > Getting target state for title=$title state=", state)
    return state
  }
  override fun setTargetLive(title: String, live: Boolean) =
    titleTarget(title)?.setLive(live)
      ?:throw IllegalStateException("Null target for $title")
  override fun isTargetLive(title: String) =
    titleTarget(title)?.isLive ?:throw IllegalStateException("Null target for $title")
}

