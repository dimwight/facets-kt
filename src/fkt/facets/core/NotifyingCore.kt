package fkt.facets.core

import fkt.facets.util.Debug
import fkt.facets.util.Tracer

abstract class NotifyingCore(val type: String, val title: String)
  :Tracer(type),Notifying {
  lateinit var notifiable_: Notifiable
  override fun title(): String {
    return this.title
  }

  override fun setNotifiable(n: Notifiable) {
    notifiable_ = n
  }

  override fun notifiable(): Notifiable {
    return notifiable_
  }

  override fun notifyParent() {
    if(!this::notifiable_.isInitialized)throw Error("Null notifiable_ in "+Debug.info(this))
    notifiable_.notify(this)
  }

  abstract override fun elements(): Array<out Notifying>
  override fun notify(notice: Any) {
    notifiable_.notify(this.title())
  }
}

