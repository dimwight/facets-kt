package fkt.facets.core

import fkt.facets.IndexingCoupler

class Indexing(title: String, coupler: IndexingCoupler) : TargetCore(title, coupler) {
  init {
    setIndex(coupler.passIndex ?: 0)
  }

  fun index(): Int {
    return state as Int
  }

  fun setIndex(index: Int) {
    val first = state == NoState
    super.state = index
    if (!first) coupler().targetStateUpdated?.invoke(state, this.title)
  }

  override var state
    get()=super.state
    set(update) = this.setIndex(update as Int)

  fun indexables(): List<*> {
    val indexables: List<*> = coupler().getIndexables(this.title)
    if (indexables.isEmpty()) throw Error("Empty indexables in" + this)
    else return indexables
  }

  fun uiSelectables(): Array<String> {
    var selectables = 0
    val coupler = coupler()
    return indexables().map {
      coupler.newUiSelectable?.invoke(it!!) ?: (title + selectables++)
    }.toTypedArray()
  }

  private fun coupler(): IndexingCoupler {
    return this.extra as IndexingCoupler
  }

  fun indexed(): Any {
    if (state == NoState) throw Error("No index in" + this.title)
    else return this.indexables()[state as Int]as Any
  }

  fun setIndexed(indexable: Any) {
    this.indexables().forEachIndexed { at, i -> if (i == indexable) this.setIndex(at) }
  }

}
