package fkt.facets.core
open class IndexingFrame(title: String, private val indexing: Indexing) : TargetCore(title) {
  init {
    indexing.setNotifiable(this);
  }
  fun indexedTarget(): Targety {
    val indexed = indexing.indexed()
    return indexed as? Targety?:this.newIndexedTargets(indexed)
  }
  open fun newIndexedTargets(indexed: Any): Targety {
    throw Error("Not implemented in" + this.title())
  }
  fun indexing(): Indexing {
    return indexing
  }
  final override fun newTargeter(): Targeter {
    return IndexingFrameTargeter()
  }
  final override fun notifiesTargeter(): Boolean {
    return true
  }
}

