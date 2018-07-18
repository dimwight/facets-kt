package fkt.facets.core
const val TargetCoreType = "Targety"

open class TargetCore(title: String, var extra: Any? = null)
  : NotifyingCore(TargetCoreType, title), Targety// any=Targety[]|TargetCoupler
{
  private var live = true
  val NoState = "No state set"
  var state_: Any = NoState

  override fun state(): Any {
    return this.state_
  }

  open fun notifiesTargeter(): Boolean {
    return if (extra == null) false else extra is Array<*>
  }

  override fun elements(): Array<Targety> {
    if (extra == null) extra = this.lazyElements()
    return if (extra is Array<*>) {
      val elements = this.extra!! as Array<Targety>
      if(true)trace(".elements: elements=",elements.size)
      elements.forEach { e ->e.setNotifiable(this)}
      elements
    } else arrayOf()
  }

  open fun lazyElements(): Array<out Targety> {
    return arrayOf()
  }

  override fun updateState(update: Any) {
    this.state_ = update
    val extra = this.extra
    if (!(extra == null || extra is Array<*>)) (extra as TargetCoupler).targetStateUpdated?.invoke(this.state(), this.title())
  }

  open fun newTargeter(): Targeter {
    return TargeterCore()
  }

  override fun isLive(): Boolean {
    return this.live
  }

  override fun setLive(live: Boolean) {
    this.live = live
  }
}

