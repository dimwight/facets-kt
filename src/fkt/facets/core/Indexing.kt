package fkt.facets.core
class Indexing(title: String, coupler: IndexingCoupler) : TargetCore(title, coupler) {
  fun index(): Int {
    return state_ as Int
  }
  fun setIndex(index: Int) {
    val first = this.state_ === NoState
    this.state_ = index
    if (!first) coupler().targetStateUpdated?.invoke(this.state_, this.title())
  }
  fun indexables(): Array<Any> {
    val indexables: Array<Any> = coupler().getIndexables(this.title())as Array<Any>
    if (indexables.isEmpty()) throw Error("Empty indexables in" + this)
    else return indexables
  }
  fun uiSelectables(): Array<String> {
    var selectables = 0
    val coupler = coupler()
    return this.indexables().map{ i ->
      coupler.newUiSelectable?.invoke(i) ?: (title()+ selectables++)
    }.toTypedArray()
  }
  private fun coupler(): IndexingCoupler {
    return this.extra as IndexingCoupler
  }
  fun indexed(): Any {
    if (this.state_ === NoState) throw Error("No index in" + this.title())
    else return this.indexables()[this.state_ as Int]
  }
  fun setIndexed(indexable: Any) {
    this.indexables().forEachIndexed({ at, i ->
      if (i === indexable) this.setIndex(at)
    })
  }
  override fun updateState(update: Any) {
    this.setIndex(update as Int)
  }
}