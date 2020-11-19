package ru.dm_ushakov.picturizer.gui

import java.awt.Graphics
import ru.dm_ushakov.picturizer.renderer.Renderer

import javax.swing.JComponent

class PicturePlot:JComponent() {
    private val plottingThread = PlottingThread()
    var message:String? = null
        set(value) {
            field = value
            repaint()
        }

    var renderer:Renderer?
        get() = plottingThread.renderer
        set(value) {
            plottingThread.renderer = value
            plottingThread.requirePaint()
        }

    init {
        plottingThread.repaintCallback = {
            repaint()
        }
    }

    override fun paintComponent(g: Graphics) {
        if(message == null) plottingThread.image?.let { img -> g.drawImage(img,0,0,this) }
        message?.let { msg ->
            msg.split('\n').forEachIndexed { idx,line ->
                g.drawString(line,0,20 + idx*16)
            }
        }
        super.paintComponent(g)
    }

    override fun setBounds(x: Int, y: Int, width: Int, height: Int) {
        plottingThread.width = width
        plottingThread.height = height
        plottingThread.requirePaint()
        super.setBounds(x, y, width, height)
    }
}