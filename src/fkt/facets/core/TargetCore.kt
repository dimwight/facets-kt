package fkt.facets.core

import fkt.facets.TargetCoupler
import fkt.facets.util.Debug

const val TargetCoreType = "Targety"

open class TargetCore(title: String, var extra: Any? = null)
  : NotifyingCore(TargetCoreType, title), Targety {
  private var _live = true
  val NoState = "No state set"
  var state: Any = NoState

  init {
    if (false || extra != null && !(extra is TargetCoupler || extra is List<*>))
      throw Error("Bad extra ${Debug.info(extra)} in " + Debug.info(this))
  }

  override fun state(): Any {
    return state
  }

  open fun notifiesTargeter() = if (extra == null) false else extra is Array<*>

  final override val elements: List<Targety> get(){
    if (extra == null) extra = lazyElements()
    val isList = extra is List<*>
    if (false) trace(".elements: extra=", isList)
    return if (isList) {
      if (false) trace(".elements: extra=", extra!!)
      val elements = extra as List<Targety>
      elements.forEach { it.setNotifiable(this) }
      elements
    } else listOf()
  }

  open fun lazyElements(): List<Targety> =listOf()

  override fun updateState(update: Any) {
    state = update
    val extra = this.extra
    if (!(extra == null || extra is Array<*>))
      (extra as TargetCoupler).targetStateUpdated?.invoke(this.state(), this.title)
  }

  open fun newTargeter(): Targeter =TargeterCore()

  final override var live: Boolean
    get()=_live
    set(live){
        _live = live
      }
}

