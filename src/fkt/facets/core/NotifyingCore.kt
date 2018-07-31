package fkt.facets.core

import fkt.facets.util.Debug
import fkt.facets.util.Tracer

abstract class NotifyingCore(override val title: String)
    :Tracer(),Notifying {
  private lateinit var _notifiable: Notifiable

  override fun setNotifiable(n: Notifiable) {
    if(false)trace(".setNotifiable: this=",this)
    _notifiable = n
  }

  override val notifiable get()=_notifiable

  override fun notifyParent() {
    if(!this::_notifiable.isInitialized)throw Error("Null _notifiable in "+Debug.info(this))
    _notifiable.notify(this)
  }

  abstract override val elements: List<Notifying>
  override fun notify(notice: Any) {
    _notifiable.notify(title)
  }
}

