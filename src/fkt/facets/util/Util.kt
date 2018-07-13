package fkt.facets.util

import fkt.java.util.Util.sf

object Util {
  private const val DIGITS_SF = 3
  private const val DECIMALS_FX = 2
  fun printOut(s: String?) {
    val text = s ?: "null"
    println(text)
  }

  fun printOut(msg: String, o: Any) {
    printOut(msg + o)
  }

  fun arrayPrintString(toPrint: Array<Any>?): String {
    return if (toPrint == null) "null" else Debug.toStringWithHeader(toPrint)
  }


  internal fun sfs(`val`: Double): String {
    val sf = sf(`val`).toString()
    val sfs = sf.replace("(\\d{$DIGITS_SF,})\\.0(\\D?)".toRegex(), "$1$2").replace("\\.0\\z".toRegex(), "")
    return if (false) "[$sf>$sfs]" else sfs
  }

  fun fxs(`val`: Double): String {
    return "0." + if (DECIMALS_FX == 1)
      "0"
    else if (DECIMALS_FX == 2) "00" else "000"
  }

  private fun shortName(className: String): String {
    val semiColon = className.lastIndexOf(';')
    val stop = if (semiColon > 0)
      semiColon
    else
      className.length
    return if (false)
      className
    else
      className.substring(className.lastIndexOf('.') + 1,
        stop)
  }


  fun arraysEqual(now: Array<Any>, then: Array<Any>?): Boolean {
    var equal = false
    if (then != null && then.size == now.size) {
      equal = true
      for (i in now.indices) {
        val equals = now[i] == then[i]
        if (false && !equals)
          println("longEquals: equal=" + equal
            + " " + now[i] + ">" + then[i])
        equal = equal and equals
      }
    }
    return equal
  }
}
