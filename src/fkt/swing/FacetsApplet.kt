package fkt.swing

import fkt.ContentingSurface
import fkt.SelectingSurface
import fkt.SimpleSurface
import fkt.TargetTest
import fkt.TargetTest.*
import fkt.facets.util.Tracer
import java.awt.GridLayout
import java.awt.Point
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import javax.swing.BorderFactory.*
import javax.swing.JApplet
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.border.EtchedBorder
import fkt.SimpleTitles as Titles
val t= Tracer.newTopped("FacetsApplet")

/**
 Superficial Host for [SurfaceCore]s

 @param constructor

 @param [args] passed from [main], specify flavour of surface
 */
class FacetsApplet(private val args: Array<String>) : JApplet() {
  /**
  Calls [SurfaceCore.buildSurface]] on an instance specified by [args]
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

val frame = JFrame("FacetsApplet")
fun main(args: Array<String>) {
  frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
  val applet = FacetsApplet(args)
  frame.contentPane.add(applet)
  applet.init()
  if(false)frame.size = applet.minimumSize
  frame.pack()
  frame.location=Point(1920,0)
  javax.swing.SwingUtilities.invokeLater {frame.isVisible = true}
}