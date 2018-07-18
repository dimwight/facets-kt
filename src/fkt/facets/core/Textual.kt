package fkt.facets.core
import fkt.facets.TextualCoupler
import fkt.facets.util.Debug
import java.awt.SystemColor.text

class Textual(title:String,coupler: TextualCoupler) : TargetCore(title,coupler){
  init {
    state=coupler.passText?:NoState
  }
  override fun state():String{
    val coupler=extra as TextualCoupler
    return when {
      state!=NoState -> state as String
      else -> coupler.getText?.invoke(title)?:throw Error("Missing text in ${Debug.info(this)}")
    }
  }

}

