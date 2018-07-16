package fkt.facets
import fkt.facets.TargetCoupler
import fkt.java.STarget
abstract class TargetCoupler_ {
  open val targetStateUpdated: ((Any, String) -> Unit)? = null
}
abstract class TextualCoupler_ : TargetCoupler() {
  open val passText: String? = null
  open val getText: ((String) -> String)? = null
  open val isValidText: ((String, String) -> Boolean)? = null
}
abstract class TogglingCoupler_ : TargetCoupler() {
  abstract val passSet: Boolean
}
abstract class NumericCoupler_ : TargetCoupler() {
  abstract val passValue: Double
  open val min: Double? = null
  open val max: Double? = null
}
abstract class IndexingState_ {
  abstract val uiSelectables: Array<String>
  abstract val indexed: Any
}
abstract class IndexingFramePolicy_ {
  open val indexingTitle: String? = null
  abstract val getIndexables: () -> (Array<out Any>)
  open val frameTitle: String? = null
  open val newUiSelectable: ((Any) -> String)? = null
  open val newFrameTargets: (() -> (Array<STarget>))? = null
  open val newIndexedTreeTitle: ((Any) -> String)? = null
  open val newIndexedTree: ((Any, String) -> STarget)? = null
}
abstract class IndexingCoupler_ : TargetCoupler() {
  abstract val getIndexables: (String) -> Array<out Any>
  open val passIndex: Int? = null
  open val newUiSelectable: ((Any) -> String)? = null
}
interface FacetsApp_ {
  fun getContentTrees(): Any
  fun onRetargeted(activeTitle: String)
  fun buildLayout()
}

