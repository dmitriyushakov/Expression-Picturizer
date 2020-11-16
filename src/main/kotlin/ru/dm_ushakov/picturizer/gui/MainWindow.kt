package ru.dm_ushakov.picturizer.gui

import ru.dm_ushakov.picturizer.cg.compileExpression
import ru.dm_ushakov.picturizer.classLoader
import ru.dm_ushakov.picturizer.renderer.Renderer
import java.awt.ComponentOrientation
import java.awt.Dimension
import java.awt.event.ActionListener
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
        upperPanel.layout = BoxLayout(upperPanel,BoxLayout.X_AXIS)
        upperPanel.add(expressionField)
        upperPanel.add(renderButton)
        upperPanel.maximumSize = Dimension(Int.MAX_VALUE,30)
        rootPanel.add(imageShowArea)

        renderButton.addActionListener {
            imageShowArea.renderer = getRenderer(expressionField.text)
        }

        defaultCloseOperation = EXIT_ON_CLOSE
        minimumSize = Dimension(400,300)
        isVisible = true
    }

    private fun getRenderer(expression:String):Renderer {
        val className = "CompiledExpression$exprClassNum"
        val bytecode = compileExpression(expression,className)
        val loadedClass = classLoader.getClassFromBytecode(className,bytecode)
        val renderer = loadedClass.getConstructor().newInstance() as? Renderer ?: error("Returned object couldn't be casted to Renderer interface!")
        exprClassNum++

        return renderer
    }
}