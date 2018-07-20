package fkt.swing

import fkt.SurfaceCore
import fkt.TargetTest
import fkt.facets.Facets
import fkt.facets.util.Tracer
import java.awt.Container
import java.awt.Font
import java.awt.event.ActionEvent
import java.text.DecimalFormat
import javax.swing.AbstractListModel
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JFormattedTextField
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener

abstract class PaneLayout(protected val pane: Container,
                          protected val test: TargetTest,
                          private val surface: SurfaceCore,
                          protected val facets: Facets = surface.facets)
  : Tracer("PaneLayout") {
  abstract fun build()
  protected fun newButtonFacet(title: String): SwingFacet<JButton> {
    val button = JButton(SwingFacet.stripTitleTail(title))
    return object : SwingFacet<JButton>(button, title, facets) {
      override val fieldState: Any
        get() {
          return "Fired"
        }

      override fun addFieldListener() {
        button.addActionListener(this)
      }

      override fun updateField(update: Any) {}
    }
  }

  protected fun newLabelFacet(title: String): SwingFacet<JLabel> {
    val label = object : JLabel() {
      override fun getFont(): Font? = super.getFont()?.deriveFont(Font.PLAIN)
    }
    return object : SwingFacet<JLabel>(label, title, facets) {
      override val fieldState: String
        get() {
          return this.field.getText()
        }

      override fun updateField(update: Any) {
        field.setText(update as String)
      }

      override fun addFieldListener() {}
    }
  }

  protected fun newCheckBoxFacet(title: String): SwingFacet<JCheckBox> {
    return object : SwingFacet<JCheckBox>(JCheckBox(), title, facets) {
      override val fieldState: Boolean
        get() {
          return this.field.isSelected()
        }

      override fun updateField(update: Any) {
        field.setSelected(update as Boolean)
      }

      override fun addFieldListener() {
        field.addActionListener(this)
      }
    }
  }

  protected fun newNumberFieldFacet(title: String, cols: Int): SwingFacet<JFormattedTextField> {
    val field = JFormattedTextField()
    field.setHorizontalAlignment(JFormattedTextField.RIGHT)
    field.setColumns(cols)
    val formatter = DecimalFormat.getInstance()
    formatter.setMaximumFractionDigits(0)
    formatter.setMinimumFractionDigits(0)
    return object : SwingFacet<JFormattedTextField>(field, title, facets) {
      override val fieldState: Double
        get() {
          return java.lang.Double.valueOf(this.field.getText())
        }

      override fun updateField(update: Any) {
        field.setText(formatter.format(update as Double))
      }

      override fun addFieldListener() {
        field.addActionListener(this)
      }
    }
  }

  protected fun newComboBoxFacet(title: String): SwingFacet<JComboBox<String>> {
    val field = JComboBox<String>(facets.getIndexingState(title).uiSelectables)
    return object : SwingFacet<JComboBox<String>>(
      field,
      title, facets) {
      override val fieldState: Int
        get() {
          return this.field.getSelectedIndex()
        }

      override fun actionPerformed(e: ActionEvent) {
        if (facets.getTargetState(title) != field.getSelectedIndex())
          super.actionPerformed(e)
      }

      override fun updateField(update: Any) {
        field.setSelectedIndex(update as Int)
      }

      override fun addFieldListener() {
        field.addActionListener(this)
      }
    }
  }

  protected fun newListFacet(title: String): SwingFacet<JList<String>> {
    val facet = object : SwingFacet<JList<String>>(
      JList<String>(), title, facets) {
      override val fieldState: Int
        get() {
          return this.field.getSelectedIndex()
        }

      override fun actionPerformed(e: ActionEvent) {
        if (field.getSelectedIndex() < 0) return
        if (facets.getTargetState(title) != (field.getSelectedIndex()))
          super.actionPerformed(e)
      }

      override fun updateField(update: Any) {
        val selectables = facets.getIndexingState(title).uiSelectables
        field.setModel(object : AbstractListModel<String>() {
          override fun getSize() = selectables.size
          override fun getElementAt(index: Int): String {
            return selectables[index]
          }
        })
        field.setSelectedIndex(update as Int)
        field.repaint()
      }

      override fun addFieldListener() {
        field.addListSelectionListener(object : ListSelectionListener {
          override fun valueChanged(e: ListSelectionEvent) {
            actionPerformed(ActionEvent(e.getSource(), e.hashCode(), e.toString()))
          }
        })
      }
    }
    facet.field.setBorder(BorderFactory.createLoweredBevelBorder())
    return facet
  }

  protected fun newTextFieldFacet(title: String, cols: Int, interim: Boolean): SwingFacet<JTextField> {
    val field = JTextField()
    val facet = object : SwingFacet<JTextField>(field, title, facets) {
      override val fieldState: Any get() = this.field.getText()
      override fun updateField(update: Any) {
        field.setText(update as String)
      }

      override fun addFieldListener() {
        field.addActionListener(this)
      }
    }
    field.setColumns(cols)
    if (interim)
      field.getDocument().addDocumentListener(object : DocumentListener {
        private lateinit var then: String
        override fun removeUpdate(e: DocumentEvent) {
          changedUpdate(e)
        }

        override fun insertUpdate(e: DocumentEvent) {
          changedUpdate(e)
        }

        override fun changedUpdate(doc: DocumentEvent) {
          val now = field.getText()
          val action = ActionEvent(field, now.length, "changedUpdate")
          if (field.hasFocus() && !now.isEmpty() && now !== then)
            SwingUtilities.invokeLater {
              facet.actionPerformed(action)
              then = now
            }
        }
      })
    return facet
  }
}
