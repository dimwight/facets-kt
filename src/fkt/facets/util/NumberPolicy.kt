package fkt.facets.util
class NumberPolicy(private val min: Double, private val max: Double) : Tracer("NumberPolicy") {  init {
    if (min > max) throw IllegalArgumentException("Bad values $min>=$max")
  }  
  fun max(): Double {
    return max
  }  
  fun min(): Double {
    return min
  }
  open fun validValue(existing: Double, proposed: Double): Double {
    val min = min()
    val max = max()
    val cycled = proposed
    val adjusted = if (cycled < min) min else if (cycled > max) max else cycled
    val jump = unit() * unitJump()
    val rounded = if (false) Math.rint(adjusted) else Math.rint(adjusted / jump) * jump
    return if (rounded < min) min else if (rounded > max) max else rounded
  }  protected fun debug(): Boolean {
    return false
  }  
  fun unit(): Double {
    val format = format()
    if (format > FORMAT_DECIMALS_4)
      throw IllegalStateException(
        format.toString() + " decimals not implemented in " + Debug.info(this))
    return when {
      format < FORMAT_DECIMALS_1 -> 1.0
      format == FORMAT_DECIMALS_1 -> 0.1
      format == FORMAT_DECIMALS_2 -> 0.01
      format == FORMAT_DECIMALS_3 -> 0.001
      else -> 0.0001
    }
  }  
  fun canCycle(): Boolean {
    return false
  }  
  fun format(): Int {
    return FORMAT_DECIMALS_0
  }  
  fun columns(): Int {
    return COLUMNS_DEFAULT
  }  
  open fun unitJump(): Double {
    return UNIT_JUMP_DEFAULT
  }  companion object {
    val MIN_VALUE = java.lang.Double.MAX_VALUE * -1
    val MAX_VALUE = java.lang.Double.MAX_VALUE
    var COLUMNS_DEFAULT = 3
    var UNIT_JUMP_DEFAULT = 1.0
    val debug = false
    val FORMAT_DECIMALS_0 = 0
    val FORMAT_DECIMALS_1 = 1
    val FORMAT_DECIMALS_2 = 2
    val FORMAT_DECIMALS_3 = 3
    val FORMAT_DECIMALS_4 = 4
    val FORMAT_HEX = -1
  }
}
