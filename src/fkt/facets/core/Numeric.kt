package fkt.facets.core
import fkt.facets.NumericCoupler

class Numeric(title: String, coupler: NumericCoupler) : TargetCore(title,coupler) {
  init {
    state=coupler.passValue
  }
  override fun state(): Double {
    return state as Double
  }

  override fun updateState(update: Any) {
    state=update as Double
  }
}
