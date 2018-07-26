package fkt

enum class SelectableType(){
  Standard,ShowChars,Chooser;
  val titleTail:String get() =
    if (this == ShowChars) SelectingTitles.CharsTail else ""
}

