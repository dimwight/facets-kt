package fkt.facets.util
import fkt.facets.util.Debug
import fkt.java.util.Identified
import fkt.java.util.Util
import fkt.java.util.Util.arrayPrintString

/**
Utility superclass that can issue trace messages.
 */
abstract class Tracer(private val top:String?): Identified {
  private val id = ++ids
  class TracerTopped(top: String):Tracer(top) {
    override fun doTraceMsg(msg:String) {
      if (doTrace()) super.doTraceMsg(msg)
    }
    private fun doTrace():Boolean {
      return true
    }
  }
  override fun identity():Any {
    return id
  }
  fun trace(msg:String) {
    doTraceMsg(msg)
  }
  fun trace(msg:String, o:Any) {
    if (o is Array<*>)
      doTraceMsg(msg + newArrayText(o as Array<Any>))
    else
      doTraceMsg(msg + Debug.info(o))
  }
  protected open fun doTraceMsg(msg:String) {
    Util.printOut((if (top != null) ("$top #$id") else Debug.info(this)) + " " + msg)
  }
  private fun newArrayText(array:Array<Any>):String {
    if (false) return arrayPrintString(array)
    var lines = "[\n"
    for (o in array)
      lines += " " + (if (true) Debug.info(o) else o.toString()) + "\n"
    lines += ("]")
    return lines
  }
  companion object {
    var ids:Int = 0
    fun newTopped(top:String):Tracer {
      return TracerTopped(top)
    }
  }
}