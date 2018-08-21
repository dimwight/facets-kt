package fkt.swing

import fkt.facets.FacetUpdater
import fkt.facets.util.Tracer
import fkt.facets.Facets
import fkt.facets.SimpleState
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

abstract class SwingFacet<C : JComponent>(val field: C,
                                          val title: String,
                                          val facets: Facets
) : Tracer("SwingFacet"), ActionListener {
  val mount: JPanel = object: JPanel(FlowLayout(FlowLayout.LEFT)){
    override fun getPreferredSize() = Dimension(200,super.getPreferredSize().height)
  }
  private lateinit var label: JLabel
  protected abstract val fieldState: SimpleState
  private val updater: FacetUpdater = {
    updateField(it)
    val live = facets.isTargetLive(title)
    field.isEnabled = live
    label.isEnabled = live
  }

  init {
    label = JLabel(stripTitleTail(title))
    if (field !is JButton) mount.add(label)
    mount.add(field)
    addFieldListener()
    facets.attachFacet(title, updater)
  }

  override fun actionPerformed(e: ActionEvent) {
    val state = fieldState
    facets.updateTarget(title, state)
  }

  protected abstract fun addFieldListener()
  protected abstract fun updateField(update: SimpleState)

  companion object {
    fun stripTitleTail(title: String) = title.replace(("\\|.*").toRegex(), "")
  }
}