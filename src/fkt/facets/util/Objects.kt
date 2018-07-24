package fkt.facets.util
import java.util.ArrayList
/**
 * Utility methods for arrays.
 */
object Objects {
  private fun toString(items: Array<Any?>?, spacer: String): String {
    if (items == null)
      return "null"
    else if (items.isEmpty()) return ""
    val list = ArrayList<Any>()
    val trim = false && spacer != "\n"
    var at = 0
    for (item in items)
      list.add(
        (if (item == null) "null" else if (trim) item.toString().trim { it <= ' ' } else item).toString() + if (++at == items.size) "" else spacer
      )
    return list.toString()
  }

  fun toString(array: Array<Any?>): String {
    return toString(array, ",")
  }

  fun toLines(array: Array<*>?): String {
    if (array == null) return "null"
    val list = ArrayList<Any>()
    for (i in array.indices)
      list.add((if (array[i] == null)
        "null"
      else if (true) Debug.info(array[i]) else array[i].toString()) + if (i < array.size - 1) "\n" else "")
    val lines = list.toString()
    return if (false) lines else lines.replace("\n".toRegex(), " ")
  }
}
