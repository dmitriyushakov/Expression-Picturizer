package ru.dm_ushakov.picturizer.gui

import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JComponent
import ru.dm_ushakov.picturizer.renderer.Renderer

class PicturePlot:JComponent() {
    var imgBuffer = IntArray(0)
    var bufferedImage:BufferedImage? = null
    var renderer:Renderer? = null
        set(value) {
            field = value
            repaint()
        }
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        var bufferedImage = this.bufferedImage ?: BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB)
        if (this.bufferedImage == null) this.bufferedImage = bufferedImage

        if (height > bufferedImage.height || width > bufferedImage.width) {
            bufferedImage = BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB)
            this.bufferedImage = bufferedImage
        }

        if (width*height > imgBuffer.size) imgBuffer = IntArray(width*height)
        renderer?.let {
            it.render(imgBuffer, width, height)
            bufferedImage.setRGB(0,0,width,height,imgBuffer,0,width)
        }
        g.drawImage(bufferedImage,0,0,this)
    }
}