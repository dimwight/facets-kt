package fkt.app

enum class SelectableType(){
  Short,Long,Chooser;
  val titleTail:String get() =
    if (this == Long) SelectingTitles.CharsTail else ""
}

