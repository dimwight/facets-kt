package fkt.app

import java.awt.Container
import java.awt.GridLayout
import javax.swing.JPanel
import java.awt.event.ActionEvent
import fkt.app.SelectingTitles as Titles

class ContentingLayout(pane: Container, app: ContentingApp)
  : SelectingLayout(pane, app) {
  override fun build() {
    buildCardsBase(checkContent = false)
    pane.layout = GridLayout(1, 1)
    pane.add(cardsParent)
    for ((at, card) in arrayOf(
      JPanel(GridLayout(8, 1)),//Short
      JPanel(GridLayout(8, 1)),//Long
      JPanel(GridLayout(4, 1))//Chooser
    ).withIndex()) {
      cardsParent.add(card)
      val type = SelectableType.values()[at]
      val typeTitle = type.toString()
      cards.addLayoutComponent(card, typeTitle)
      if (type == SelectableType.Chooser) {
        val button = newButtonFacet(Titles.OpenEdit)
        val click2 = {
          if(facets.isTargetLive(Titles.OpenEdit))
            button.actionPerformed(ActionEvent("", 0, ""))
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
  }
}