package fkt.facets.core
import fkt.facets.util.traceThing
open class IndexingFrame(title: String, private val indexing_: Indexing) : TargetCore(title) {
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

