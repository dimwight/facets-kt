package fkt.facets.util
object Debug {
  var trace = false
  fun info(o: Any?): String {
    if (o == null)
      return "null"
    else if (o is Boolean)
      return o.toString()
    else if (o is Number)
      return o.toString()
    else if (o is String) {
      val text = o as String?
      val length = text!!.length
      return text.substring(0, Math.min(length, 60)) + if (true) "" else ": length=$length"
    }
    val classe = o.javaClass
    val name = classe.simpleName
    var id = ""
    var title = if (false) "o instanceof Titled" else classe.name
    if (o is Identified) id = " #" + o.identity()
    if (o is Titled) title = " " + o.title()
    return name + id + title
  }
  fun arrayInfo(array: Array<Any>): String {
    return "arrayInfo"
  }
  fun traceEvent(string: String) {
    Util.printOut(">>$string")
  }
  fun toStringWithHeader(array: Array<Any>): String {
    return info(array) + " [" + array.size + "] " + Objects.toLines(array)
  }
}
