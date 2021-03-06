package fkt.facets.util
abstract class Tracer(protected val top:String?=null): Identified {
  private val id = ++ids
  private class Topped(top: String):Tracer(top) {
    override fun doTraceMsg(msg:String) {
      if (doTrace()) println("$top: $msg")
    }
    private fun doTrace():Boolean {
      return true
    }
  }
  override val identity=id
  fun trace(msg:String) {
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
    println((if (top != null) ("$top #$id") else Debug.info(this)) + " " + msg)
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
    fun newTopped(top:String):Tracer=Topped(top)
  }
}