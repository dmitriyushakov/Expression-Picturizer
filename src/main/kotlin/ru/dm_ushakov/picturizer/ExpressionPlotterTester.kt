package ru.dm_ushakov.picturizer

import java.util.*
import ru.dm_ushakov.picturizer.cg.compileExpression
import ru.dm_ushakov.picturizer.renderer.Renderer
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

const val height = 800
const val width = 800
val classLoader = ByteClassLoader("Bytecode class loader", ClassLoader.getSystemClassLoader())

fun main() {
    val sc = Scanner(System.`in`)
    var iteration = 0
    while (true) {
        print(" > ")
        val expression = sc.nextLine()
        println()
        val className = "CompiledExpression$iteration"
        val bytecode = compileExpression(expression,className)
        val loadedClass = classLoader.getClassFromBytecode(className,bytecode)
        val renderer = loadedClass.getConstructor().newInstance() as? Renderer ?: error("Returned object couldn't be casted to Renderer interface!")
        val imageBuf = IntArray(height*width)
        renderer.render(imageBuf,width,height,0)
        val img = BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB)
        img.setRGB(0,0, width, height,imageBuf,0, width)
        val imageFileName = "image$iteration.png"
        ImageIO.write(img,"png", File(imageFileName))
        println("Image rendered into \"$imageFileName\"")

        iteration++
    }
}