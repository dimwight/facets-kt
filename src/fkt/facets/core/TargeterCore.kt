package fkt.facets.core

open class TargeterCore(type:String="Targeter") : NotifyingCore(type, "Untargeted"), Targeter {
  private lateinit var _elements: Array<Targeter>
  private lateinit var _target: TargetCore
  var facets: MutableList<Facet> = mutableListOf()
  override fun retarget(target: Targety) {
    _target = target as TargetCore
    val targets = _target.elements()
    if(!this::_elements.isInitialized)_elements = targets.map {
      val element = (it as TargetCore).newTargeter()
      element.setNotifiable(this)
      element
    }.toTypedArray()
    if (targets.size == _elements.size) _elements.forEachIndexed { at, e ->
      e.retarget(targets[at])
    }
    if (_target.notifiesTargeter()) _target.setNotifiable(this)
  }

  override fun title(): String =
    if(!this::_target.isInitialized)"Untargeted" else _target.title()

  override fun target() = _target

  override fun elements() = this._elements

  open fun titleElements() = elements()

  override fun attachFacet(f: Facet) {
    if (!facets.contains(f)) facets.add(f)
    f.retarget(_target)
  }

  override fun retargetFacets() {
    this._elements.forEach { it.retargetFacets() }
    this.facets.forEach { it.retarget(_target) }
  }
}

