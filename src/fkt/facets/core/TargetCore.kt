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
    val extra = this.extra
    return if (extra == null) false else extra is Array<*>
  }

  override fun elements(): Array<Targety> {
    if (this.extra != null) this.extra = this.lazyElements()
    return if (this.extra is Array<*>) {
      val extra = this.extra!! as Array<Targety>
      trace(".elements: extra=",extra.size)
      extra.forEach { e ->e.setNotifiable(this)}
      extra
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

