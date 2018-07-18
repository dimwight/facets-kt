package fkt.facets.util
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
  open fun trace(msg:String) {
    doTraceMsg(msg)
  }
  fun trace(msg:String, o:Any) {
      val trace = msg + when (o) {
        is Collection<*> -> newArrayText(o.toTypedArray())
        is Array<*> -> newArrayText(o)
        else -> Debug.info(o)
      }
      doTraceMsg(trace)
  }
  protected open fun doTraceMsg(msg:String) {
    Util.printOut((if (false&&top != null) ("$top #$id") else Debug.info(this)) + " " + msg)
  }
  private fun newArrayText(array:Array<*>):String {
    if (false) return Util.arrayPrintString(array)
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