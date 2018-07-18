package fkt.facets.util
object Debug {
  var trace = false
  fun info(o: Any?): String {
    when (o) {
      null -> return "null"
      is Boolean -> return o.toString()
      is Number -> return o.toString()
      is String -> {
        val text = o as String?
        val length = text!!.length
        return text.substring(0, Math.min(length, 60)) + if (true) "" else ": length=$length"
      }
      else -> {
        val kc = o::class
        val name = kc.simpleName?:kc.toString()
        val id = if (o is Identified) " #" + o.identity()else ""
        val title = if (o is Titled) " " + o.title()else ""
        return name + id + title
      }
    }
  }
  fun toStringWithHeader(array: Array<*>): String {
    return info(array) + " [" + array.size + "] " + Objects.toLines(array)
  }
  fun arrayInfo_(array: Array<Any>): String {
    return "arrayInfo:${array.size}"
  }
  fun traceEvent_(string: String) {
    Util.printOut(">>$string")
  }
}
