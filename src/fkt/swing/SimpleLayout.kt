package fkt.swing
import fkt.SurfaceCore
import fkt.TargetTest.Indexing
import fkt.TargetTest.Numeric
import fkt.TargetTest.TogglingLive
import fkt.TargetTest.Trigger
import java.awt.Container
import java.awt.GridLayout
import fkt.SimpleTitles as Titles
class SimpleLayout(pane: Container, surface: SurfaceCore):PaneLayout(pane,surface){
  override fun build() {
    pane.layout = GridLayout(4, 1, 5, 5)
    for (facet in when(surface.test){
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
					 newTextFieldFacet(Titles.MasterTextual, 20, false),
					 newTextFieldFacet(Titles.SlaveTextual, 20, false),
					 newLabelFacet(Titles.MasterTextual),
					 newLabelFacet(Titles.SlaveTextual)
    		 )
			})
    pane.add(facet.mount)
  }
}