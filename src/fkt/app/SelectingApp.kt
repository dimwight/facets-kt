package fkt.app

import fkt.facets.IndexingFramePolicy
import fkt.facets.SimpleState
import fkt.facets.TextualCoupler
import fkt.facets.TogglingCoupler
import fkt.facets.Target
import fkt.app.SelectingTitles as Titles
import fkt.app.SimpleTitles as Simples

object SelectingTitles {
  const val Select = "Select Content"
  const val Chooser = "Chooser"
  const val OpenEdit = "Edit Selection"
  const val Save = "Save"
  const val Cancel = "Cancel"
  const val EditText = "Edit Text"
  const val CharsCount = "Characters"
  const val Live = "Live"
  const val CharsTail = "|Long"
}

enum class SelectableType(){
  Short,Long,Chooser;
  val titleTail:String get() = if (this == Long) fkt.app.SelectingTitles.CharsTail else ""
}

class TextContent(var text: String) {
  val selectableType: SelectableType
    get() =
      if (text.length > 20) SelectableType.Long else SelectableType.Short

  override fun toString() = text
  override fun equals(other: Any?) =
    other != null && text == (other as TextContent).text

  fun clone() = TextContent(text)
  fun copyClone(clone: TextContent) {
    this.text = clone.text
  }

  override fun hashCode(): Int {
    return text.hashCode()
  }
}

open class SelectingApp(test: TargetTest, trace: Boolean)
  : AppCore(trace, test) {
  protected val list = mutableListOf(
    TextContent("Hello world!"),
    TextContent("Hello Dolly!"),
    TextContent("Hello, good evening and welcome!"))

  override fun newContentTrees(): List<Target> {
    val appTitle = TargetTest.Selecting.toString()
    return listOf(facets.newIndexingFrame(object : IndexingFramePolicy() {
      override val frameTitle = appTitle
      override val indexingTitle = Titles.Select
      override fun getIndexables() =  list
      override val newUiSelectable = { indexable: Any -> (indexable as TextContent).text }
      override val newFrameTargets = {
        listOf(
          facets.newTextualTarget(Simples.Indexed, object : TextualCoupler() {
            override val getText = { _: String ->
              val indexed = facets.getIndexingState(Titles.Select).indexed as TextContent
              indexed.selectableType.toString()
            }
          }),
          facets.newTogglingTarget(Titles.Live, object : TogglingCoupler() {
            override val passSet = true
          })
        )
      }
      override val newIndexedTreeTitle = if(true)fun( indexed: Any)=
        appTitle + (indexed as TextContent).selectableType.titleTail
      else null
      override val newIndexedTree = { indexed: Any, indexedTreeTitle: String ->
        val content = indexed as TextContent
        val type = content.selectableType
        val tail = type.titleTail
        facets.newTargetGroup(indexedTreeTitle,
          if (type == SelectableType.Short)
            listOf(newEditTarget(content, tail))
          else listOf(newEditTarget(content, tail), newCharsTarget(tail))
        )
      }
    }))
  }

  private fun getIndexedType(): SelectableType {
    val content = facets.getIndexingState(
      Titles.Select).indexed as TextContent
    return content.selectableType
  }

  override fun buildSurface() {
    super.buildSurface()
    if (true) return
    val add = {
      list.add(TextContent("Hello sailor!"))
      trace(" > Simulating input: update=", list[list.size - 1].text)
      facets.updateTarget(Titles.Select)
    }
    val edit = {
      val update = "Hello !"
      trace(" > Simulating input: update=", update)
      facets.updateTarget(Titles.EditText, update)
    }
    val select = {
      val update = 2
      trace(" > Simulating input: update=", update)
      facets.updateTarget(Titles.Select, update)
    }
    for (update in listOf(add, edit, select)) update()
  }

  override fun onRetargeted(activeTitle: String) {
    val live = (facets.getTargetState(Titles.Live) ?: true) as Boolean
    val type = getIndexedType()
    val tail = type.titleTail
    facets.setTargetLive(Titles.EditText + tail,live)
    if (type == SelectableType.Long)
      facets.setTargetLive(Titles.CharsCount + tail,live)
  }

  override fun buildLayout() {
    generateFacets(
      Titles.Select,
      Titles.EditText)
  }

  protected fun newEditTarget(indexed: TextContent,
                              tail:String,
                              onStateChange:()->Unit={}): Target =
    facets.newTextualTarget(
      Titles.EditText + tail,
      object : TextualCoupler() {
        override val passText = indexed.text
        override val targetStateUpdated = { state: Any, _: String ->
          indexed.text = state as String
          onStateChange()
        }
      }
    )

  protected fun newCharsTarget(tail: String): Target =
    facets.newTextualTarget(Titles.CharsCount + tail, object : TextualCoupler() {
      override val getText = { _: String ->
        "" + (facets.getTargetState(Titles.EditText + Titles.CharsTail) as String).length
      }
    })
}
