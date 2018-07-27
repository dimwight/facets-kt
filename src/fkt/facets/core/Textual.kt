package fkt.facets.core
import fkt.facets.TextualCoupler
import fkt.facets.util.Debug

class Textual(title:String,coupler: TextualCoupler) : TargetCore(title,coupler){
  init {
    super.state=coupler.passText?:NoState
  }
  override var state:Any
    get(){
      val coupler=extra as TextualCoupler
      return when {
        state!=NoState -> state
        else -> coupler.getText?.invoke(title)?:throw Error("Missing text in ${Debug.info(this)}")
      }
    }
  set(update){super.state=update}

}

