package ru.dm_ushakov.picturizer.gui

import ru.dm_ushakov.picturizer.renderer.Renderer
import java.awt.Dimension
import java.awt.Frame
import java.awt.GridLayout
import java.awt.image.BufferedImage
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Exception
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.filechooser.FileNameExtensionFilter

class ExportImageDialog(owner:Frame,private val renderer:Renderer):JDialog(owner,"Export image") {
    private infix fun pane(func:JPanel.() -> Unit) = JPanel().apply(func)
    private val widthSpinner = JSpinner(SpinnerNumberModel(800,1,10000,1))
    private val heightSpinner = JSpinner(SpinnerNumberModel(600,1,10000,1))
    private val fileNameTextField = JTextField()
    private val selectFileButton = JButton("Select file")
    private val saveButton = JButton("Save")
    private val cancelButton = JButton("Cancel")

    init {
        val fixedSize = Dimension(300,200)
        preferredSize = fixedSize
        minimumSize = fixedSize
        maximumSize = fixedSize
        isResizable = false

        val rootPane = pane {
            border = EmptyBorder(7,7,7,7)
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            val fieldsPane = pane {
                layout = GridLayout(4,2,7,3)

                add(JLabel("Width:"))
                add(widthSpinner)

                add(JLabel("Height:"))
                add(heightSpinner)

                add(JLabel("File name:"))
                add(fileNameTextField)

                add(JPanel())
                add(selectFileButton)
            }

            add(fieldsPane)

            val buttonsPane = pane {
                border = EmptyBorder(10,0,0,0)
                layout = BoxLayout(this,BoxLayout.X_AXIS)

                add(saveButton)
                add(Box.createRigidArea(Dimension(5,0)))
                add(cancelButton)
            }

            add(buttonsPane)
        }

        selectFileButton.addActionListener { selectFilePressed() }
        cancelButton.addActionListener { dispose() }
        saveButton.addActionListener { saveImagePressed() }

        contentPane = rootPane

    }

    private fun selectFilePressed() {
        val fileChooser = JFileChooser()
        val pngFilter = FileNameExtensionFilter("PNG files", "png")
        val bmpFilter = FileNameExtensionFilter("Bitmap images", "bmp")
        val jpgFilter = FileNameExtensionFilter("JPEG images", "jpg", "jpeg")

        fileChooser.addChoosableFileFilter(pngFilter)
        fileChooser.addChoosableFileFilter(bmpFilter)
        fileChooser.addChoosableFileFilter(jpgFilter)

        fileChooser.fileFilter = pngFilter

        val returnVal = fileChooser.showSaveDialog(this)
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            fileNameTextField.text = fileChooser.selectedFile.path
        }
    }

    private fun saveImagePressed() {
        val fileName = fileNameTextField.text ?: error("Can't read value from filename text field")
        if (fileName == "") {
            JOptionPane.showMessageDialog(this,"File name shouldn't be empty!","Image export error",JOptionPane.ERROR_MESSAGE)
            return
        }

        try {
            val width = (widthSpinner.value as? Number)?.toInt() ?: error("Can't extract value from width spinner!")
            val height = (heightSpinner.value as? Number)?.toInt() ?: error("Can't extract value from height spinner!")

            val imageBuf = IntArray(height * width)
            renderer.render(imageBuf, width, height, 0)
            val img = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            img.setRGB(0, 0, width, height, imageBuf, 0, width)

            val extension = fileName.substring(fileName.lastIndexOf('.'))
            val format = when (extension) {
                ".jpg", ".jpeg" -> "jpg"
                ".bmp" -> "bmp"
                else -> "png"
            }

            ImageIO.write(img, format, File(fileName))
            dispose()
        } catch (ex:Exception) {
            val sw = StringWriter()
            ex.printStackTrace(PrintWriter(sw))
            JOptionPane.showMessageDialog(this,"Exception caused during save image:\n$sw","Image export error",JOptionPane.ERROR_MESSAGE)
        }
    }
}