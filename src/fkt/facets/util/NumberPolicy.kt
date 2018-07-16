package fkt.facets.util

/**
 * Validation and display of numbers.
 *
 * [NumberPolicy] supplies the policy Debug.information required when
 * validating a number or displaying it in the application surface.
 */
open class NumberPolicy
/**
 * Construct a policy that constrains valid values within the immutable range
 * `min` to `max`.
 */
(private val min: Double, private val max: Double) : Tracer("NumberPolicy") {
  /**
   * Validation and tick-based display of numbers.
   *
   * [Ticked] supplies validation and display policy for
   * numbers displayed on a scale such as that of a slider.
   */
  /**
   * Convenience constructor for a full-range tick scale.
   */
  /**
   * Convenience constructor for a scale that dynamically defines its constraints.
   */
  init {
    if (min > max) throw IllegalArgumentException("Bad values $min>=$max")
  }

  /**
   * The highest possible value under the policy.
   *
   * Set immutably during construction but only accessed via
   * this method, enabling subclasses to define it dynamically by overriding.
   */
  fun max(): Double {
    return max
  }

  /**
   * The lowest possible value under the policy.
   *
   * Set immutably during construction but only accessed via
   * this method, enabling subclasses to define it dynamically by overriding.
   */
  fun min(): Double {
    return min
  }

  /**
   * Returns the nearest valid value to `proposed`.
   *
   * Though not used in the basic implementation, specifying `existing`
   * as a parameter allows for non-linear validation in subclasses.
   */
  open fun validValue(existing: Double, proposed: Double): Double {
    val min = min()
    val max = max()
    val cycled = proposed
    val adjusted = if (cycled < min) min else if (cycled > max) max else cycled
    val jump = unit() * unitJump()
    val rounded = if (false) Math.rint(adjusted) else Math.rint(adjusted / jump) * jump
    return if (rounded < min) min else if (rounded > max) max else rounded
  }

  protected fun debug(): Boolean {
    return false
  }

  /**
   * The smallest possible increment under the policy.
   *
   * The default implementation deduces this value from [.format].
   */
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

  /**
   * If `true`, values outside the range will be normalised to within
   * it.
   *
   * Default is `false`.
   */
  fun canCycle(): Boolean {
    return false
  }

  /**
   * Hint on formatting the number.
   *
   * These are defined by the class FORMAT_x constants, which are mutually exclusive.
   *
   * For FORMAT_PLACES_x constants
   * the format should normally be consistent with `unit`.
   */
  fun format(): Int {
    return FORMAT_DECIMALS_0
  }

  /**
   * The column width for text boxes in which values are to be displayed.
   *
   * Follows AWT convention.
   */
  fun columns(): Int {
    return COLUMNS_DEFAULT
  }

  /**
   * Returns the multiple of `unit` defining the current minimum
   * valid value change.
   */
  open fun unitJump(): Double {
    return UNIT_JUMP_DEFAULT
  }

  companion object {
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
