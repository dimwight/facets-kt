package fkt.swing

import java.awt.Container
import java.awt.GridLayout
import javax.swing.JPanel
import fkt.ContentingSurface
import fkt.SelectableType
import fkt.TargetTest
import java.awt.event.ActionEvent
import fkt.SelectingTitles as Titles

internal class ContentingLayout(pane: Container, test: TargetTest, surface: ContentingSurface) : SelectingLayout(pane, test, surface) {
  override fun build() {
    buildFacet()
    pane.setLayout(GridLayout(1, 1))
    pane.add(cardsParent)
    for ((at, card) in arrayOf(
      JPanel(GridLayout(8, 1)),
      JPanel(GridLayout(8, 1)),
      JPanel(GridLayout(4, 1))
    ).withIndex()) {
      cardsParent.add(card)
      val type = SelectableType.values[at]
      val typeTitle = type.title()
      cards.addLayoutComponent(card, typeTitle)
      if (type == SelectableType.Chooser) {
        val button = newButtonFacet(Titles.OpenEdit)
        val click2 = { button.actionPerformed(ActionEvent("", 2, "")) }
        card.add(newListFacet(Titles.Select, click2).mount)
        card.add(button.mount)
      } else {
        val tail = type.titleTail()
        if (false)
          card.add((if (false)
            newTextFieldFacet(facets.activeContentTitle, 20, false)
          else
            newLabelFacet(facets.activeContentTitle)).mount)
        card.add(newTextFieldFacet(Titles.EditText + tail, 20, false).mount)
        if (type == SelectableType.ShowChars)
          card.add(newLabelFacet(Titles.CharsCount + tail).mount)
        card.add(newButtonFacet(Titles.Save + tail).mount)
        card.add(newButtonFacet(Titles.Cancel + tail).mount)
      }
    }
  }
}