package fkt.app

import java.awt.Container
import java.awt.GridLayout
import javax.swing.JPanel
import java.awt.event.ActionEvent
import fkt.app.SelectingTitles as Titles

class ContentingLayout(pane: Container, app: ContentingApp)
  : SelectingLayout(pane, app) {
  private val openEdit = Titles.OpenEdit
  override fun build() {
    buildCardsBase(checkContent = false)
    pane.layout = GridLayout(1, 1)
    pane.add(cardsParent)
    fun newCard(): (Int) -> JPanel {
      return { at ->
        JPanel(GridLayout(when (at) {
          2 -> 4    //Chooser
          else -> 8 //Short, Long
        }, 1))
      }
    }
    for ((at, card) in Array(3, newCard()).withIndex()) {
      cardsParent.add(card)
      val type = SelectableType.values()[at]
      cards.addLayoutComponent(card, type.name)
      if (type == SelectableType.Chooser) {
        val button = newButtonFacet(openEdit)
        val click2 = {
          if(facets.isTargetLive(openEdit))
            if(false)button.actionPerformed(ActionEvent("", 0, ""))
            else facets.updateTarget(openEdit,"Fire")
        }
        card.add(newListFacet(Titles.Select, click2).mount)
        card.add(button.mount)
      } else {
        val tail = type.titleTail
        card.add(newTextFieldFacet(Titles.EditText + tail, 20, false).mount)
        if (type == SelectableType.Long)
          card.add(newLabelFacet(Titles.CharsCount + tail).mount)
        card.add(newButtonFacet(Titles.Save + tail).mount)
        card.add(newButtonFacet(Titles.Cancel + tail).mount)
      }
    }
    cards.show(cardsParent, SelectableType.Chooser.name)
  }
}