package fkt.facets.core

open class TargeterCore() : NotifyingCore("Targeter", "Untargeted"), Targeter {
  private lateinit var elements: Array<Targeter>
  private lateinit var target: TargetCore
  var facets: MutableList<Facet> = mutableListOf()
  override fun retarget(target: Targety) {
    this.target = target as TargetCore
    val targets: Array<Targety> = target.elements()
    trace(".retarget: target=", target)
    elements = targets.map {
      val element = (it as TargetCore).newTargeter()
      element.setNotifiable(this)
      element
    }.toTypedArray()
    if (targets.size == elements.size) elements.forEachIndexed { at, e ->
      e.retarget(targets[at])
    }
    if (this.target.notifiesTargeter()) target.setNotifiable(this)
  }

  override fun title() = target.title()

  override fun target() = target

  override fun elements() = this.elements

  open fun titleElements() = elements()

  override fun attachFacet(f: Facet) {
    if (!facets.contains(f)) facets.add(f)
    f.retarget(target)
  }

  override fun retargetFacets() {
    this.elements.forEach { it.retargetFacets() }
    this.facets.forEach { it.retarget(target) }
  }
}

