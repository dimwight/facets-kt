package fkt.swing
import fkt.facets_.util.Tracer
import fkt.Facets
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
abstract class SwingFacet<C : JComponent>
		(val field:C, val title:String, val facets: Facets):Tracer("SwingFacet"), ActionListener {
  val mount:JPanel
  private lateinit var label:JLabel
  protected abstract val fieldState:Any
  val updater:(Any)->Unit={
	  updateField(it)
	  val live = facets.isTargetLive(title)
	  field.setEnabled(live)
	  label.setEnabled(live)
	}
  init{
    mount = JPanel(FlowLayout(FlowLayout.LEFT))
    label = JLabel(stripTitleTail(title))
    if (!(field is JButton)) mount.add(label)
    mount.add(field)
    addFieldListener()
    facets.attachFacet(title, updater)
  }
  public override fun actionPerformed(e:ActionEvent) {
    val state = fieldState
    facets.updateTargetWithNotify(title, state)
  }
  protected abstract fun addFieldListener()
  protected abstract fun updateField(update:Any)
  companion object {
    fun stripTitleTail(title:String):String {
      return title.replace(("\\|.*").toRegex(), "")
    }
  }
}