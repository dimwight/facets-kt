package fkt.facets.core

import fkt.facets.util.Debug

open class TargeterCore(type: String = "Targeter") : NotifyingCore(type, "Untargeted"), Targeter {
  private val facets = mutableListOf<Facet>()
  private lateinit var _elements: List<Targeter>
  private lateinit var _target: TargetCore
  override fun retarget(target: Targety) {
    _target = target as TargetCore
    val targets = _target.elements
    if(false)trace(".retarget: _target=${Debug.info(_target)} targets=",targets.size)
    if (!::_elements.isInitialized) _elements = targets.map {
      val element = (it as TargetCore).newTargeter()
      element.setNotifiable(this)
      element
    }
    if (targets.size == _elements.size)
      _elements.forEachIndexed { at, e -> e.retarget(targets[at]) }
    if (_target.notifiesTargeter()) _target.setNotifiable(this)
  }

  override val title: String
    get() = if (!::_target.isInitialized) super.title else _target.title

  override fun target() = _target

  override val elements get()= this._elements

  open val titleElements get() = elements

  override fun attachFacet(f: Facet) {
    if (!facets.contains(f)) facets.add(f)
    f.retarget(_target)
  }

  override fun retargetFacets() {
    _elements.forEach { it.retargetFacets() }
    facets.forEach { it.retarget(_target) }
  }
}

