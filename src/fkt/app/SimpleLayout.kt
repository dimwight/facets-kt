package fkt.app
import fkt.app.TargetTest.Indexing
import fkt.app.TargetTest.Numeric
import fkt.app.TargetTest.TogglingLive
import fkt.app.TargetTest.Trigger
import fkt.swing.PaneLayout
import java.awt.Container
import java.awt.GridLayout
import fkt.app.SimpleTitles as Titles
class SimpleLayout(pane: Container, app: AppCore): PaneLayout(pane,app){
  override fun build() {
    pane.layout = GridLayout(4, 1, 5, 5)
    for (facet in when(app.test){
			Indexing->arrayOf(
					 newComboBoxFacet(Titles.Indexing),
					 newLabelFacet(Titles.Index),
					 newLabelFacet(Titles.Indexed)
				 )
       TogglingLive->arrayOf(
					 newCheckBoxFacet(Titles.Toggling),
					 newLabelFacet(Titles.Toggled)
    		 )
       Numeric->arrayOf(
					 newNumberFieldFacet(Titles.NumericField, 5),
					 newLabelFacet(Titles.NumericValue)
    		 )
       Trigger->arrayOf(
					 newButtonFacet(Titles.Trigger),
					 newLabelFacet(Titles.Triggerings)
    		 )
       else->arrayOf(
					 newTextFieldFacet(Titles.MasterTextual, 12, false),
					 newTextFieldFacet(Titles.SlaveTextual, 12, false),
					 newLabelFacet(Titles.MasterTextual),
					 newLabelFacet(Titles.SlaveTextual)
    		 )
			})
    pane.add(facet.mount)
  }
}