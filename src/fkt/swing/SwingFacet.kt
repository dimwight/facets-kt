package fkt.swing

import fkt.facets.FacetUpdater
import fkt.facets.util.Tracer
import fkt.facets.Facets
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

abstract class SwingFacet<C : JComponent>
(val field: C, val title: String, val facets: Facets,
 val mount: JPanel = JPanel(FlowLayout(FlowLayout.LEFT)))
  : Tracer("SwingFacet"), ActionListener {
  private lateinit var label: JLabel
  protected abstract val fieldState: Any
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
  protected abstract fun updateField(update: Any)

  companion object {
    fun stripTitleTail(title: String) = title.replace(("\\|.*").toRegex(), "")
  }
}