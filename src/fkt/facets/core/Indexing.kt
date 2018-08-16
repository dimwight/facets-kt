package fkt.facets.core

import fkt.facets.IndexingCoupler

class Indexing(title: String, coupler: IndexingCoupler) : TargetCore(title, coupler) {
  init {
    super.state=coupler.passIndex ?: 0
  }

  var index: Int
    get()=state as Int
    set(index) {
      val first = state == NoState
      super.state = index
      if (!first) coupler().targetStateUpdated?.invoke(state, this.title)
    }

  override var state
    get()=super.state
    set(update){index=update as Int}

  fun indexables(): List<Any> {
    val indexables: List<Any> = coupler().getIndexables()
    if (indexables.isEmpty()) throw Error("Empty indexables in" + this)
    else return indexables
  }

  fun uiSelectables(): List<String> {
    var selectables = 0
    return indexables().map {
      fun dummy() = "${selectables++}: $it"
      if(true)coupler().newUiSelectable?.invoke(it) ?: dummy() else dummy()
    }
  }

  private fun coupler(): IndexingCoupler {
    return extra as IndexingCoupler
  }

  fun indexed(): Any {
    if (state == NoState) throw Error("No index in" + this.title)
    else return this.indexables()[state as Int]
  }

  fun setIndexed(indexable: Any) {
    this.indexables().forEachIndexed { at, i -> if (i == indexable) index=at }
  }

}
