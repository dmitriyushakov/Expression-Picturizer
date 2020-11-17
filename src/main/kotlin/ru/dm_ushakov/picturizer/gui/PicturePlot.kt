package ru.dm_ushakov.picturizer.gui

import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JComponent
import ru.dm_ushakov.picturizer.renderer.Renderer

class PicturePlot:JComponent() {
    private val plottingThread = PlottingThread()
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
        g.drawImage(plottingThread.image,0,0,this)
    }

    override fun setBounds(x: Int, y: Int, width: Int, height: Int) {
        plottingThread.width = width
        plottingThread.height = height
        plottingThread.requirePaint()
        super.setBounds(x, y, width, height)
    }
}