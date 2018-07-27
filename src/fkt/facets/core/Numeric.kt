package fkt.facets.core
import fkt.facets.NumericCoupler

class Numeric(title: String, coupler: NumericCoupler) : TargetCore(title,coupler) {
  init {
    super.state=coupler.passValue
  }
  override var state:Any
    get()=state as Double
    set(update) {
      state=update as Double
    }
}
