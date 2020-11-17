package ru.dm_ushakov.picturizer.gui

import ru.dm_ushakov.picturizer.renderer.Renderer
import java.awt.image.BufferedImage
import javax.swing.SwingUtilities

class PlottingThread:Runnable {
    private val waitMon = Object()
    private val thread = Thread(this).apply { start() }
    private var imgBuffer = IntArray(0)
    private var bufferedImage: BufferedImage? = null
    private var timeStart = 0L
    var height = 0
    var width = 0
    var renderer: Renderer? = null
        set(value) {
            field = value
            if(value != null && value.isSupportTime) {
                timeStart = System.currentTimeMillis()
            }
        }

    private val timeDelta get() = if (renderer?.isSupportTime == true) System.currentTimeMillis() - timeStart else 0L

    private fun plot() {
        val width = this.width
        val height = this.height
        if (width == 0 || height == 0) return

        var bufferedImage = this.bufferedImage ?: BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB)
        if (this.bufferedImage == null) this.bufferedImage = bufferedImage

        if (height > bufferedImage.height || width > bufferedImage.width) {
            bufferedImage = BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB)
            this.bufferedImage = bufferedImage
        }

        if (width*height > imgBuffer.size) imgBuffer = IntArray(width*height)
        renderer?.let {
            it.render(imgBuffer, width, height, timeDelta)
            bufferedImage.setRGB(0,0,width,height,imgBuffer,0,width)
        }
    }

    fun requirePaint() {
        synchronized(waitMon) {
            waitMon.notify()
        }
    }

    val image get() = bufferedImage ?: error("Buffered image yet not loaded!")
    var repaintCallback:(() -> Unit)?=null

    override fun run() {
        while (true) {
            val lastTime = System.currentTimeMillis()
            plot()
            repaintCallback?.let { SwingUtilities.invokeAndWait(it) }
            if (renderer?.isSupportTime == true) {
                val timeDelta = System.currentTimeMillis() - lastTime - 33
                if (timeDelta > 0) Thread.sleep(timeDelta)
            } else {
                synchronized(waitMon) {
                    waitMon.wait()
                }
            }
        }
    }
}