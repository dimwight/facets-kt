package fkt.facets.core

import fkt.facets.util.Titled

interface Notifying : Notifiable, Titled {
  fun setNotifiable(n: Notifiable)
  val notifiable: Notifiable
  val elements: List<Notifying>
  fun notifyParent()
}

