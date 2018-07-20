package fkt

import fkt.TargetTest.*
import fkt.swing.ContentingLayout
import fkt.swing.SelectingLayout
import fkt.swing.SimpleLayout
import java.awt.GridLayout
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import javax.swing.BorderFactory.*
import javax.swing.JApplet
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.border.EtchedBorder
import fkt.SimpleTitles as Titles

class FacetsApplet : JApplet() {
  override fun init() {
    val content = contentPane as JPanel
    content.addComponentListener(object : ComponentListener {
      override fun componentResized(e: ComponentEvent) {
        if (false) println("componentResized: " + content.size)
      }

      override fun componentShown(e: ComponentEvent) {}
      override fun componentMoved(e: ComponentEvent) {}
      override fun componentHidden(e: ComponentEvent) {}
    })
    val simples = TargetTest.simpleValues()
    val tests =
      when {
        false -> if(false)arrayOf(TargetTest.TogglingLive) else simples
        else -> arrayOf(if (true) Selecting else Contenting)
      }
    content.layout = GridLayout(if (tests.contentEquals(simples)) 3 else 2, 1)
    for (test in tests) {
      if (false && (!(!tests.contentEquals(simples) || test === TogglingLive)))
        continue
      val pane = JPanel()
      pane.border = createCompoundBorder(createEmptyBorder(10, 10, 10, 10),
        createEtchedBorder(EtchedBorder.LOWERED))
      content.add(pane)
      val trace = true
      when {
        test.isSimple -> object : SimpleSurface(test, trace) {
          override fun buildLayout() {
            facets.times.setResetWait(50)
            facets.times.doTime = false
            if (test === TogglingLive) {
              val live = facets.getTargetState(Titles.Toggling) as Boolean
              facets.setTargetLive(Titles.Toggled, live)
            }
            SimpleLayout(pane, test, this).build()
          }
        }
        test === Contenting -> object : ContentingSurface(trace) {
          private val layout = ContentingLayout(pane, test, this)
          override fun buildLayout() = layout.build()
        }
        else -> object : SelectingSurface(Selecting, trace) {
          private val layout = SelectingLayout(pane, test, this)
          override fun buildLayout() = layout.build()
        }
      }.buildSurface()
    }
  }
}

fun main(args: Array<String>) {
  val frame = JFrame("FacetsApplet")
  frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
  if (true) {
    val applet = FacetsApplet()
    frame.contentPane.add(applet)
    applet.init()
    frame.size = applet.minimumSize
  }
  frame.pack()
  javax.swing.SwingUtilities.invokeLater { frame.isVisible = true }
}