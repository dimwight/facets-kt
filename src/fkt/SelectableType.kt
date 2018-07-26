package fkt

enum class SelectableType(){
  Standard,ShowChars,Chooser;
  val titleTail:String
    get() = if (this == ShowChars) SelectingTitles.CharsTail else ""
  companion object {
    fun getContentType(content:TextContent):SelectableType =
      if (content.text.length > 20) ShowChars else Standard
  }
}

