package fkt.swing
import fkt.SelectableType
import fkt.SelectingSurface
import java.awt.CardLayout
import java.awt.Container
import java.awt.GridLayout
import javax.swing.JComponent
import javax.swing.JPanel
import fkt.SelectingTitles as Titles
open class SelectingLayout(pane:Container, surface:SelectingSurface):PaneLayout(pane, surface){
  protected val cards = CardLayout()
  protected val cardsParent:JComponent = JPanel(cards)
  override fun build() {
    buildFacet()
    pane.add(newListFacet(Titles.Select).mount)
    pane.add(cardsParent)
    for ((at,card) in listOf(
			JPanel(GridLayout(5, 1)),
			JPanel(GridLayout(5, 1))
		).withIndex()){
      cardsParent.add(card)
      val type = SelectableType.values[at]
      cards.addLayoutComponent(card, type.title)
      val tail = type.titleTail
      card.add(newTextFieldFacet(Titles.EditText + tail, 20, false).mount)
      if (type == SelectableType.ShowChars)
      card.add(newLabelFacet(Titles.CharsCount + tail).mount)
      card.add(newCheckBoxFacet(Titles.Live).mount)
    }
  }
  protected fun buildFacet() {
    pane.layout = GridLayout(2, 1)
    object:SwingFacet<JComponent>(cardsParent, facets.activeContentTitle, facets) {
      override val fieldState:String
      get() = "getFieldState"
      override fun addFieldListener() {}
      override fun updateField(update:Any) {
        cards.show(cardsParent, update as String)
      }
    }
  }
}