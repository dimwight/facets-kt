package fkt
import fkt.java.util.Titled

class SelectableType protected constructor(val title:String): Titled {
  fun titleTail():String =
  		if (this === ShowChars) SelectingTitles.CharsTail else ""
  override fun title()=title
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