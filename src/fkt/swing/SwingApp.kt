package fkt.swing

import fkt.app.ContentingApp
import fkt.app.ContentingLayout
import fkt.app.SelectingApp
import fkt.app.SelectingLayout
import fkt.app.SimpleApp
import fkt.app.SimpleLayout
import fkt.app.TargetTest
import fkt.app.TargetTest.*
import java.awt.GridLayout
import java.awt.Point
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import javax.swing.BorderFactory.*
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.border.EtchedBorder
import fkt.app.SimpleTitles as Titles

/**
 Superficial host for [fkt.facets.FacetsApp]s.

 @param [args] passed from [main], specify flavour of app
 */
class SwingApp(private val args: Array<String>) : JComponent() {
  /**
  Calls [fkt.app.AppCore.buildSurface]] on an instance specified by [args]
   */
  fun init() {
    if(false)addComponentListener(object : ComponentListener {
      override fun componentResized(e: ComponentEvent) {
        println("componentResized: $size")
      }

      override fun componentShown(e: ComponentEvent) {}
      override fun componentMoved(e: ComponentEvent) {}
      override fun componentHidden(e: ComponentEvent) {}
    })
    val simples = TargetTest.simpleValues
    val style=args.firstOrNull { !it.startsWith("_") }?:""
    val tests = when (style) {
        "contenting" -> listOf(Contenting)
        "selecting" -> listOf(Selecting )
        else -> if (false) listOf(TargetTest.TogglingLive) else simples
      }
    layout = GridLayout(if (tests==simples) 3 else 1, 1)
    for (test in tests) {
      if (false && !(tests!=simples || test == TogglingLive)) continue
      val pane = JPanel()
      pane.border = createCompoundBorder(createEmptyBorder(10, 10, 10, 10),
        createEtchedBorder(EtchedBorder.LOWERED))
      add(pane)
      val trace = false
      when {
        test.isSimple -> object : SimpleApp(test, trace) {
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
        test == Contenting -> object : ContentingApp(trace) {
          override fun buildLayout() = ContentingLayout(pane, this).build()
        }
        else -> object : SelectingApp(Selecting, trace) {
          override fun buildLayout() = SelectingLayout(pane, this).build()
        }
      }.buildSurface()
    }
  }
}

fun main(args: Array<String>) {
  val frame = JFrame("SwingApp")
  frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
  val applet = SwingApp(args)
  frame.contentPane.add(applet)
  applet.init()
  if(false)frame.size = applet.minimumSize
  frame.pack()
  frame.location=Point(1920,0)
  javax.swing.SwingUtilities.invokeLater {frame.isVisible = true}
}