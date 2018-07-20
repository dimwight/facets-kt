package fkt.facets.core

import fkt.facets.util.Debug
import fkt.facets.util.Tracer

abstract class NotifyingCore(val type: String, val title: String)
  :Tracer(type),Notifying {
  private lateinit var _notifiable: Notifiable
  override fun title(): String {
    return this.title
  }

  override fun setNotifiable(n: Notifiable) {
    _notifiable = n
  }

  override fun notifiable(): Notifiable {
    return _notifiable
  }

  override fun notifyParent() {
    if(!this::_notifiable.isInitialized)throw Error("Null _notifiable in "+Debug.info(this))
    _notifiable.notify(this)
  }

  abstract override fun elements(): Array<out Notifying>
  override fun notify(notice: Any) {
    _notifiable.notify(this.title())
  }
}

