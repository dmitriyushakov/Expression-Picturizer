package ru.dm_ushakov.picturizer.gui

import ru.dm_ushakov.picturizer.ByteClassLoader
import ru.dm_ushakov.picturizer.cg.compileExpression
import ru.dm_ushakov.picturizer.exceptions.ExpressionCompilationException
import ru.dm_ushakov.picturizer.renderer.Renderer
import java.awt.Dimension
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.PrintWriter
import java.io.StringWriter
import javax.swing.*

class MainWindow:JFrame("Expression Picturizer") {
    private var exprClassNum = 0
    val rootPanel = JPanel()
    val upperPanel = JPanel()
    val expressionField = JTextField()
    val renderButton = JButton("Render")
    val imageShowArea = PicturePlot()
    init {
        contentPane = rootPanel
        rootPanel.layout = BoxLayout(rootPanel,BoxLayout.Y_AXIS)
        rootPanel.add(upperPanel)
        expressionField.addKeyListener(object:KeyListener{
            override fun keyTyped(ev: KeyEvent) {}
            override fun keyReleased(ev: KeyEvent) {}

            override fun keyPressed(ev: KeyEvent) {
                if (ev.keyCode == 10) renderPressed()
            }
        })
        upperPanel.layout = BoxLayout(upperPanel,BoxLayout.X_AXIS)
        upperPanel.add(expressionField)
        upperPanel.add(renderButton)
        upperPanel.maximumSize = Dimension(Int.MAX_VALUE,30)
        rootPanel.add(imageShowArea)

        renderButton.addActionListener { renderPressed() }

        defaultCloseOperation = EXIT_ON_CLOSE
        minimumSize = Dimension(400,300)
        isVisible = true
    }

    private fun renderPressed() {
        try {
            imageShowArea.renderer = getRenderer(expressionField.text)
            imageShowArea.message = null
        } catch (compilationException:ExpressionCompilationException) {
            imageShowArea.renderer = null
            imageShowArea.message = compilationException.friendlyMessage
        } catch (th:Throwable) {
            val sw = StringWriter()
            th.printStackTrace(PrintWriter(sw))
            imageShowArea.renderer = null
            imageShowArea.message = sw.toString()
            throw th
        }
    }

    private fun getRenderer(expression:String):Renderer {
        val className = "CompiledExpression$exprClassNum"
        val bytecode = compileExpression(expression,className)
        val classLoader = ByteClassLoader("Bytecode class loader", ClassLoader.getSystemClassLoader())
        val loadedClass = classLoader.getClassFromBytecode(className,bytecode)
        val renderer = loadedClass.getConstructor().newInstance() as? Renderer ?: error("Returned object couldn't be casted to Renderer interface!")
        exprClassNum++

        return renderer
    }
}