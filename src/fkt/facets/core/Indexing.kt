package fkt.facets.core

import fkt.facets.IndexingCoupler

class Indexing(title: String, coupler: IndexingCoupler) : TargetCore(title, coupler) {
  init {
    setIndex(coupler.passIndex?:0)
  }
  fun index(): Int {
    return state as Int
  }
  fun setIndex(index: Int) {
    val first = this.state === NoState
    this.state = index
    if (!first) coupler().targetStateUpdated?.invoke(this.state, this.title())
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
    if (this.state === NoState) throw Error("No index in" + this.title())
    else return this.indexables()[this.state as Int]
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
