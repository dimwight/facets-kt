package fkt

import fkt.TargetTest.*
import fkt.facets.util.Tracer
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

/**
 Superficial Host for [SurfaceCore]s

 @constructor

 @param [args]
 */
class FacetsApplet(private val args: Array<String>) : JApplet() {
  companion object {
    val t= Tracer.newTopped("FacetsApplet")
  }
  /**
  Calls [SurfaceCore.buildSurface]] on a receiver specified by [args] passed from [main]
   */
  override fun init() {
    val style=args.firstOrNull { !it.startsWith("_") }?:""
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
    val tests = when (style) {
        "contenting" -> arrayOf(Contenting)
        "selecting" -> arrayOf(Selecting )
        else -> if (false) arrayOf(TargetTest.TogglingLive) else simples
      }
    content.layout = GridLayout(if (tests.contentEquals(simples)) 3 else 1, 1)
    for (test in tests) {
      if (false && (!(!tests.contentEquals(simples) || test == TogglingLive)))
        continue
      val pane = JPanel()
      pane.border = createCompoundBorder(createEmptyBorder(10, 10, 10, 10),
        createEtchedBorder(EtchedBorder.LOWERED))
      content.add(pane)
      val trace = false
      when {
        test.isSimple -> object : SimpleSurface(test, trace) {
          override fun buildLayout() {
            facets.times.setResetWait(50)
            facets.times.doTime = false
            if (test == TogglingLive) {
              val live = facets.getTargetState(Titles.Toggling) as Boolean
              facets.setTargetLive(Titles.Toggled, live)
            }
            SimpleLayout(pane, this).build()
          }
        }
        test == Contenting -> object : ContentingSurface(trace) {
          private val layout = ContentingLayout(pane, this)
          override fun buildLayout() = layout.build()
        }
        else -> object : SelectingSurface(Selecting, trace) {
          private val layout = SelectingLayout(pane, this)
          override fun buildLayout() = layout.build()
        }
      }.buildSurface()
    }
  }
}

fun main(args: Array<String>) {
  val frame = JFrame("FacetsApplet")
  frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
  val applet = FacetsApplet(args)
  frame.contentPane.add(applet)
  applet.init()
  frame.size = applet.minimumSize
  frame.pack()
  javax.swing.SwingUtilities.invokeLater { frame.isVisible = true }
}