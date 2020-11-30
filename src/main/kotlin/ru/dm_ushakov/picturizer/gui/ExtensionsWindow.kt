package ru.dm_ushakov.picturizer.gui

import ru.dm_ushakov.picturizer.extensions.ExtensionClass

import ru.dm_ushakov.picturizer.extensions.ExtensionsRegistry
import java.awt.*
import java.awt.event.WindowEvent
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter

class ExtensionsWindow(owner:Frame):JDialog(owner,"Extensions list") {
    private val mainPane = JPanel()
    private val extensionsList = JList(ExtensionsRegistry.extensionsList.toTypedArray())
    private val methods = JTextArea()
    private val addButton = JButton("Add")
    private val removeButton = JButton("Remove")

    var closeHandler:(() -> Unit)? = null

    init {
        minimumSize = Dimension(300,400)
        mainPane.layout = BorderLayout()
        contentPane = mainPane

        val listsPane = JPanel().apply {
            layout = BoxLayout(this,BoxLayout.Y_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            border = BorderFactory.createEmptyBorder(8,8,0,8)

            val extensionsListScroll = JScrollPane(extensionsList)
            add(JLabel("Extension classes:"))
            add(extensionsListScroll)

            methods.isEditable = false
            methods.text = ExtensionsRegistry.extensionMethods.joinToString("\n")

            val methodsScroll = JScrollPane(methods)
            add(JLabel("Discovered methods:"))
            add(methodsScroll)
        }

        mainPane.add(listsPane,BorderLayout.CENTER)

        val buttonsPane = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.LINE_AXIS)
            alignmentX = Component.RIGHT_ALIGNMENT
            border = BorderFactory.createEmptyBorder(4,8,8,8)
            add(Box.createHorizontalGlue())
            add(addButton)
            add(Box.createRigidArea(Dimension(5,0)))
            add(removeButton)
        }
        mainPane.add(buttonsPane,BorderLayout.PAGE_END)

        addButton.addActionListener { addButtonPressed() }
        removeButton.addActionListener { removeButtonPressed() }
    }

    private fun addButtonPressed() {
        tryWithErrorDialogs(this) {
            val fileChooser = JFileChooser()
            val classFilter = FileNameExtensionFilter("Class files", "class")

            fileChooser.fileFilter = classFilter

            val returnVal = fileChooser.showOpenDialog(this)
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                val loadedExtension = ExtensionClass.load(fileChooser.selectedFile)
                val newExtensions = ExtensionsRegistry.extensionsList + listOf(loadedExtension)
                ExtensionsRegistry.extensionsList = newExtensions
                refreshLists()
            }
        }
    }

    private fun removeButtonPressed() {
        tryWithErrorDialogs(this) {
            val extensionToRemove = extensionsList.selectedValue
            val newExtensions = ExtensionsRegistry.extensionsList.filter { it != extensionToRemove }
            ExtensionsRegistry.extensionsList = newExtensions
            refreshLists()
        }
    }

    private fun refreshLists() {
        extensionsList.setListData(ExtensionsRegistry.extensionsList.toTypedArray())
        methods.text = ExtensionsRegistry.extensionMethods.joinToString("\n")
    }

    override fun processWindowEvent(e: WindowEvent) {
        if (e.id == WindowEvent.WINDOW_CLOSING) closeHandler?.invoke()
        super.processWindowEvent(e)
    }
}