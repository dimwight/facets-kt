package fkt.swing
import fkt.SelectableType
import fkt.SelectingSurface
import fkt.TargetTest
import java.awt.CardLayout
import java.awt.Container
import java.awt.GridLayout
import javax.swing.JComponent
import javax.swing.JPanel
import fkt.SelectingTitles as Titles
open class SelectingLayout(pane:Container, test:TargetTest, surface:SelectingSurface):PaneLayout(pane, test, surface){
  val cards = CardLayout()
  val cardsParent:JComponent = JPanel(cards)
  init{
    pane.setLayout(GridLayout(2, 1))
  }
  override fun build() {
    buildFacet()
    pane.add(newListFacet(Titles.Select).mount)
    pane.add(cardsParent)
    for ((at,card) in arrayOf(
			JPanel(GridLayout(5, 1)),
			JPanel(GridLayout(5, 1))
		).withIndex()){
      cardsParent.add(card)
      val type = SelectableType.values[at]
      val activeTitle = type.title()
      cards.addLayoutComponent(card, activeTitle)
      val tail = type.titleTail()
      card.add(newTextFieldFacet(Titles.EditText + tail, 20, false).mount)
      if (type === SelectableType.ShowChars)
      card.add(newLabelFacet(Titles.CharsCount + tail).mount)
      card.add(newCheckBoxFacet(Titles.Live).mount)
    }
  }
  protected fun buildFacet() {
    object:SwingFacet<JComponent>(cardsParent, facets.activeContentTitle, facets) {
      override val fieldState:String
      get() {
        return "getFieldState"
      }
      override fun addFieldListener() {}
      override fun updateField(update:Any) {
        cards.show(cardsParent, update as String)
      }
    }
  }
}