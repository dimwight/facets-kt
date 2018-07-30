package fkt.swing

import fkt.app.AppCore
import fkt.facets.Facets
import fkt.facets.util.Tracer
import java.awt.Container
import java.awt.Font
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
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

/**
Implements the Superficial layout concept for [AppCore] in [FacetsApplet].

@property [pane] container for layout elements

@param[app] defined the targets the layout will expose, provides instance of [Facets]
 that creates and manages them
 */
abstract class PaneLayout(protected val pane: Container,
                          val app: AppCore) : Tracer("PaneLayout") {
  val facets = app.facets
  /**
   Called from [fkt.facets.FacetsApp.buildLayout] in [AppCore].

   Targets accessed in [AppCore.facets] created by [app].
   */
  abstract fun build()
  protected fun newListFacet(title: String, click2: () -> Unit = {
    trace(".newListFacet: doubleClick!")
  }): SwingFacet<JList<String>> {
    return object : SwingFacet<JList<String>>(JList(), title, facets) {
      init {
        field.border = BorderFactory.createLoweredBevelBorder()
        field.addMouseListener(object : MouseAdapter() {
          override fun mouseClicked(e: MouseEvent) {
            if (e.clickCount == 2) click2.invoke()
          }
        })
      }

      override val fieldState get() = field.selectedIndex

      override fun actionPerformed(e: ActionEvent) {
        if (field.selectedIndex < 0) return
        if (this@PaneLayout.facets.getTargetState(title) != (field.selectedIndex))
          super.actionPerformed(e)
      }

      override fun updateField(update: Any) {
        val selectables = this@PaneLayout.facets.getIndexingState(title).uiSelectables
        field.model = object : AbstractListModel<String>() {
          override fun getSize() = selectables.size
          override fun getElementAt(index: Int): String {
            return selectables[index]
          }
        }
        field.selectedIndex = update as Int
        field.repaint()
      }

      override fun addFieldListener() = field.addListSelectionListener { it ->
        actionPerformed(ActionEvent(it.source, it.hashCode(), it.toString()))
      }
    }
  }

  protected fun newButtonFacet(title: String): SwingFacet<JButton> {
    val button = JButton(SwingFacet.stripTitleTail(title))
    return object : SwingFacet<JButton>(button, title, facets) {
      override val fieldState: Any
        get() = "Fired"

      override fun addFieldListener() = button.addActionListener(this)

      override fun updateField(update: Any) {}
    }
  }

  protected fun newLabelFacet(title: String): SwingFacet<JLabel> {
    val label = object : JLabel() {
      override fun getFont(): Font? = super.getFont()?.deriveFont(Font.PLAIN)
    }
    return object : SwingFacet<JLabel>(label, title, facets) {
      override val fieldState: String
        get() = this.field.text

      override fun updateField(update: Any) {
        field.text = update as String
      }

      override fun addFieldListener() {}
    }
  }

  protected fun newCheckBoxFacet(title: String): SwingFacet<JCheckBox> {
    return object : SwingFacet<JCheckBox>(JCheckBox(), title, facets) {
      override val fieldState: Boolean
        get() = this.field.isSelected

      override fun updateField(update: Any) {
        field.isSelected = update as Boolean
      }

      override fun addFieldListener() = field.addActionListener(this)
    }
  }

  protected fun newNumberFieldFacet(title: String, cols: Int): SwingFacet<JFormattedTextField> {
    val field = JFormattedTextField()
    field.horizontalAlignment = JFormattedTextField.RIGHT
    field.columns = cols
    val formatter = DecimalFormat.getInstance()
    formatter.maximumFractionDigits = 0
    formatter.minimumFractionDigits = 0
    return object : SwingFacet<JFormattedTextField>(field, title, facets) {
      override val fieldState: Double
        get() = java.lang.Double.valueOf(this.field.text)

      override fun updateField(update: Any) {
        field.text = formatter.format(update as Double)
      }

      override fun addFieldListener() = field.addActionListener(this)
    }
  }

  protected fun newComboBoxFacet(title: String): SwingFacet<JComboBox<String>> {
    val field = JComboBox<String>(facets.getIndexingState(title).uiSelectables.toTypedArray())
    return object : SwingFacet<JComboBox<String>>(
      field,
      title, facets) {
      override val fieldState: Int
        get() = this.field.selectedIndex

      override fun actionPerformed(e: ActionEvent) {
        if (facets.getTargetState(title) != field.selectedIndex)
          super.actionPerformed(e)
      }

      override fun updateField(update: Any) {
        field.selectedIndex = update as Int
      }

      override fun addFieldListener() = field.addActionListener(this)
    }
  }

  protected fun newTextFieldFacet(title: String, cols: Int, interim: Boolean): SwingFacet<JTextField> {
    val field = JTextField()
    val facet = object : SwingFacet<JTextField>(field, title, facets) {
      override val fieldState: Any get() = this.field.text
      override fun updateField(update: Any) {
        field.text = update as String
      }

      override fun addFieldListener() = field.addActionListener(this)
    }
    field.columns = cols
    if (interim)
      field.document.addDocumentListener(object : DocumentListener {
        private lateinit var then: String
        override fun removeUpdate(e: DocumentEvent) = changedUpdate(e)

        override fun insertUpdate(e: DocumentEvent) = changedUpdate(e)

        override fun changedUpdate(doc: DocumentEvent) {
          val now = field.text
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
