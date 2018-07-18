package fkt.facets.core

import fkt.facets.TargetCoupler
import fkt.facets.util.Debug

const val TargetCoreType = "Targety"

open class TargetCore(title: String, var extra: Any? = null)
  : NotifyingCore(TargetCoreType, title), Targety// any=Targety[]|TargetCoupler
{
  private var live = true
  val NoState = "No state set"
  var state: Any = NoState

  init {
    if(false||extra!=null&&!(extra is TargetCoupler ||extra is Array<*>))
      throw Error("Bad extra ${Debug.info(extra)} in "+Debug.info(this))
  }

  override fun state(): Any {
    return state
  }

  open fun notifiesTargeter(): Boolean {
    return if (extra == null) false else extra is Array<*>
  }

  override fun elements(): Array<Targety> {
    if(extra==null)extra=lazyElements()
    val isArray = extra is Array<*>
    if(false)trace(".elements: extra=",isArray)
    return if(isArray){
      if(false)trace(".elements: extra=",extra!!)
      val elements=extra!! as Array<Targety>
      elements.forEach{it.setNotifiable(this)}
      elements
    } else arrayOf()
  }

  open fun lazyElements(): Array<out Targety> {
    return arrayOf()
  }

  override fun updateState(update: Any) {
    state = update
    val extra = this.extra
    if (!(extra == null || extra is Array<*>))
      (extra as TargetCoupler).targetStateUpdated?.invoke(this.state(), this.title())
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

