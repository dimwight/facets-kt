package fkt.facets.core

class IndexingFrameTargeter : TargeterCore("IndexingFrameTargeter") {
  private var titleTargeters = HashMap<String, Targeter>()
  private lateinit var indexing: Targeter
  private lateinit var indexed: Targeter
  private lateinit var indexingTarget: Indexing
  private lateinit var indexedTarget: Targety
  private lateinit var indexedTitle: String
  override fun retarget(target: Targety) {
    super.retarget(target)
    updateToTarget()
    if(!this::indexing.isInitialized){
      indexing = indexingTarget.newTargeter()
      trace(".retarget: indexing=",indexing)
    }
    indexing.setNotifiable(this)
    if (titleTargeters.size == 0) {
      val atThen = indexingTarget.index()
      for (at in indexingTarget.indexables().indices) {
        indexingTarget.setIndex(at)
        updateToTarget()
        indexed = (indexedTarget as TargetCore).newTargeter()
        val indexed = indexed
        indexed.setNotifiable(this)
        indexed.retarget(indexedTarget)
        titleTargeters[indexedTitle] = indexed
      }
      indexingTarget.setIndex(atThen)
      updateToTarget()
    }
    indexing.retarget(indexingTarget)
    indexed = titleTargeters[indexedTitle] as Targeter
    indexed.retarget(indexedTarget)
  }

  override fun retargetFacets() {
    super.retargetFacets()
    indexing.retargetFacets()
    titleTargeters.values.forEach { it.retargetFacets()}
  }

  override fun titleElements(): Array<Targeter> {
    val list = elements().toMutableList()
    list.add(indexing)
    titleTargeters.values.forEach{ it ->list.add(it)}
    return list.toTypedArray()
  }

  private fun updateToTarget() {
    val frame = target() as IndexingFrame
    indexingTarget = frame.indexing()
    indexedTarget = frame.indexedTarget()
    indexedTitle = indexedTarget.title()
  }
}

