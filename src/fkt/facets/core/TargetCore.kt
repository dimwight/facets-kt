package fkt.facets.core

import fkt.facets.TargetCoupler
import fkt.facets.util.Debug

const val TargetCoreType = "Targety"

open class TargetCore(title: String, var extra: Any? = null)
  : NotifyingCore(TargetCoreType, title), Targety {
  private var _live = true
  val NoState = "No _state set"
  private var _state: Any = NoState

  init {
    if (false || extra != null && !(extra is TargetCoupler || extra is List<*>))
      throw Error("Bad extra ${Debug.info(extra)} in " + Debug.info(this))
  }

  override var state get()=_state
  set(update: Any) {
    val first=_state==NoState
    _state = update
    val extra = this.extra
    if (!(first||extra == null || extra is Array<*>))
      (extra as TargetCoupler).targetStateUpdated?.invoke(_state, this.title)
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

  open fun newTargeter(): Targeter =TargeterCore()

  final override var live: Boolean
    get()=_live
    set(live){
        _live = live
      }
}

