package ru.dm_ushakov.picturizer.gui

import ru.dm_ushakov.picturizer.extensions.ExtensionClass
import java.awt.Frame

import ru.dm_ushakov.picturizer.extensions.ExtensionsRegistry
import java.awt.Dimension
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
        mainPane.layout = BoxLayout(mainPane,BoxLayout.Y_AXIS)
        contentPane = mainPane

        val extensionsListScroll = JScrollPane(extensionsList)
        mainPane.add(JLabel("Extension classes:"))
        mainPane.add(extensionsListScroll)

        methods.isEditable = false
        methods.text = ExtensionsRegistry.extensionMethods.joinToString("\n")

        val methodsScroll = JScrollPane(methods)
        mainPane.add(JLabel("Discovered methods:"))
        mainPane.add(methodsScroll)

        val buttonsPane = JPanel()
        buttonsPane.layout = BoxLayout(buttonsPane,BoxLayout.X_AXIS)
        buttonsPane.add(addButton)
        buttonsPane.add(removeButton)
        mainPane.add(buttonsPane)

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