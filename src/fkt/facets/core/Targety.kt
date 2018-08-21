package fkt.facets.core

import fkt.facets.Target

interface Targety : Notifying, Target {
  override val elements: List<Targety>
  var state: Any
  var live: Boolean
}

