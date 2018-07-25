package fkt
import fkt.facets.util.Titled

class SelectableType (override val title:String): Titled {
  val titleTail:String =
  		if (this == ShowChars) SelectingTitles.CharsTail else ""
  companion object {
    val Standard = SelectableType("Standard")
    val ShowChars = SelectableType("ShowChars")
    val Chooser = SelectableType("Chooser")
    val values = arrayOf<SelectableType>(Standard, ShowChars, Chooser)
    fun getContentType(content:TextContent):SelectableType {
      return if (content.text.length > 20) ShowChars else Standard
    }
  }
}