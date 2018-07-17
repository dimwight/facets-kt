package fkt.facets.core

import fkt.facets.util.Tracer

abstract class NotifyingCore(val type: String, val title: String) :Tracer(type),
  Notifying {
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
    notifiable_.notify(this)
  }

  abstract override fun elements(): Array<out Notifying>
  override fun notify(notice: Any) {
    if (notifiable_ != null) notifiable_.notify(this.title())
  }
}

