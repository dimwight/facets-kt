package fkt.swing

import fkt.ContentingSurface
import fkt.SelectingSurface
import fkt.SimpleSurface
import fkt.TargetTest
import fkt.TargetTest.Contenting
import fkt.TargetTest.Selecting
import fkt.TargetTest.TogglingLive
import java.awt.GridLayout
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import javax.swing.BorderFactory.createCompoundBorder
import javax.swing.BorderFactory.createEmptyBorder
import javax.swing.BorderFactory.createEtchedBorder
import javax.swing.JApplet
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.border.EtchedBorder
import fkt.SimpleTitles as Titles
import javax.swing.JLabel

class FacetsApplet : JApplet() {
    public override fun init() {
        val content = getContentPane() as JPanel
        content.addComponentListener(object : ComponentListener {
            public override fun componentResized(e: ComponentEvent) {
                if (false) println("componentResized: " + content.getSize())
            }

            public override fun componentShown(e: ComponentEvent) {}
            public override fun componentMoved(e: ComponentEvent) {}
            public override fun componentHidden(e: ComponentEvent) {}
        })
        val simples = TargetTest.simpleValues()
        val tests = if (true)simples
        else arrayOf<TargetTest>(if (false) Selecting else Contenting)
        content.setLayout(GridLayout(if (tests.contentEquals(simples)) 3 else 2, 1))
        for (test in tests) {
            if (false && (!(!tests.contentEquals(simples) || test === TogglingLive))) continue
            val pane = JPanel()
            pane.border = createCompoundBorder(createEmptyBorder(10, 10, 10, 10),
                    createEtchedBorder(EtchedBorder.LOWERED))
            content.add(pane)
            val trace = true;
            val surface = if (test.isSimple)
                object : SimpleSurface(test, trace) {
                    override fun buildLayout() {
                        facets.times.resetWait = 50
                        facets.times.doTime = false
                        if (test === TogglingLive) {
                            val live = facets.getTargetState(Titles.Toggling) as Boolean
                            facets.setTargetLive(Titles.Toggled, live)
                        }
                        SimpleLayout(pane, test, this).build()
                    }
                }
            else if (test === Contenting)
                object : ContentingSurface(trace) {
                    private val layout = ContentingLayout(pane, test, this)
                    override fun buildLayout() = layout.build()
                }
            else object : SelectingSurface(TargetTest.Selecting, trace) {
                private val layout = SelectingLayout(pane, test, this)
                override fun buildLayout() = layout.build()
            }
            surface.buildSurface()
        }
    }
}

fun main(args: Array<String>) {
    val frame = JFrame("FacetsApplet")
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    if (true) {
        val applet = FacetsApplet()
        frame.getContentPane().add(applet)
        applet.init()
        frame.setSize(applet.getMinimumSize())
    }
    frame.pack()
    javax.swing.SwingUtilities.invokeLater(object : Runnable {
        public override fun run() {
            frame.setVisible(true)
        }
    })
}
