package fkt.facets.core
open class IndexingFrame(title: String, private val indexing_: Indexing) : TargetCore(title) {
  init {
    indexing_.setNotifiable(this);
  }
  fun indexedTarget(): Targety {
    val indexed = indexing_.indexed()
    return indexed as? Targety?:this.newIndexedTargets(indexed)
  }
  open fun newIndexedTargets(indexed: Any): Targety {
    throw Error("Not implemented in" + this.title())
  }
  fun indexing(): Indexing {
    return indexing_
  }
  final override fun newTargeter(): Targeter {
    return IndexingFrameTargeter()
  }
  final override fun notifiesTargeter(): Boolean {
    return true
  }
}

