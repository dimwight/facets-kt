package fkt.app

import fkt.facets.IndexingFramePolicy
import fkt.facets.Target
import fkt.facets.TargetCoupler
import fkt.facets.TextualCoupler
import fkt.app.SelectingTitles as Titles
import fkt.app.SimpleTitles as Simples

open class ContentingApp(trace: Boolean): SelectingApp(TargetTest.Contenting, trace) {
  private lateinit var active: TextContent
  private lateinit var edit: TextContent
  override fun newContentTrees(): List<Target> {
    return listOf(
      newContentTree(list[0]),
      newContentTree(list[2]),
      facets.newIndexingFrame(object : IndexingFramePolicy() {
        override val frameTitle = Titles.Chooser
        override val indexingTitle = Titles.Select
        override fun getIndexables() =  list
        override val newUiSelectable = { indexable: Any ->
          (indexable as TextContent).text
        }
        override val newFrameTargets = {
          listOf(facets.newTriggerTarget(Titles.OpenEdit,
            object : TextualCoupler() {
              override val targetStateUpdated = { _: Any, _: String ->
                active = facets.getIndexingState(Titles.Select).indexed as TextContent
                edit = active.clone()
                facets.attachContentTree(newContentTree(edit))
              }
            }
          ))
        }
      }))
  }

  private fun newContentTree(content: TextContent): Target {
    val type = content.selectableType
    val tail = type.titleTail
    if(false)trace(".newContentTree: type=$type content=", content.text)
    val members = mutableListOf<Target>()
    val saveTitle = Titles.Save + tail
    members.add(newEditTarget(content, tail) {
      facets.setTargetLive(saveTitle,true)
    })
    if (type == SelectableType.Long) members.add(newCharsTarget(tail))
    fun activateChooser () = facets.activateContentTree(Titles.Chooser)
    members.add(facets.newTriggerTarget(saveTitle, object : TargetCoupler() {
      override val passLive = false
      override val targetStateUpdated = { _: Any, _: String ->
        active.copyClone(edit)
        activateChooser()
      }
    }))
    members.add(facets.newTriggerTarget(Titles.Cancel + tail, object : TargetCoupler() {
      override val targetStateUpdated = { _: Any, _: String ->activateChooser()}
    }))
    return facets.newTargetGroup(type.toString(), members.toList())
  }

  override fun onRetargeted(activeTitle: String) {
    val content = facets.getIndexingState(Titles.Select).indexed as TextContent
    if(false)facets.setTargetLive(Titles.OpenEdit,
      content.selectableType != SelectableType.Long)
  }
  override fun buildLayout() {
    generateFacets(Titles.Select, Titles.EditText, Titles.EditText + Titles.CharsTail,
      Titles.CharsCount + Titles.CharsTail)
  }
}