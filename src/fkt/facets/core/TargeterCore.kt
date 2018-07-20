package fkt.facets.core

open class TargeterCore(type:String="Targeter") : NotifyingCore(type, "Untargeted"), Targeter {
  private lateinit var elements: Array<Targeter>
  private lateinit var _target: TargetCore
  var facets: MutableList<Facet> = mutableListOf()
  override fun retarget(target: Targety) {
    this._target = target as TargetCore
    val targets = target.elements()
    if(false)trace(".retarget: _target=", target)
    elements = targets.map {
      val element = (it as TargetCore).newTargeter()
      element.setNotifiable(this)
      element
    }.toTypedArray()
    if (targets.size == elements.size) elements.forEachIndexed { at, e ->
      e.retarget(targets[at])
    }
    if (this._target.notifiesTargeter()) target.setNotifiable(this)
  }

  override fun title(): String =
    if(!this::_target.isInitialized)"Untargeted" else _target.title()

  override fun target() = _target

  override fun elements() = this.elements

  open fun titleElements() = elements()

  override fun attachFacet(f: Facet) {
    if (!facets.contains(f)) facets.add(f)
    f.retarget(_target)
  }

  override fun retargetFacets() {
    this.elements.forEach { it.retargetFacets() }
    this.facets.forEach { it.retarget(_target) }
  }
}

