package ru.dm_ushakov.picturizer.gui

import ru.dm_ushakov.picturizer.model.ExplainModel
import ru.dm_ushakov.picturizer.utils.printDebugInformation
import ru.dm_ushakov.picturizer.decompiler.*

import org.jd.core.v1.ClassFileToJavaSourceDecompiler
import org.objectweb.asm.util.Textifier
import org.objectweb.asm.*
import org.objectweb.asm.util.TraceClassVisitor

import java.awt.Dimension
import java.awt.Frame
import java.io.PrintWriter
import java.io.StringWriter
import javax.swing.*

class ExplainWindow(model:ExplainModel, owner: Frame):JDialog(owner, "Explain expression") {
    val tabbedPane = JTabbedPane()
    val operandsTreeTextArea = JTextArea()
    val bytecodeTextArea = JTextArea()
    val decompiledCodeTextArea = JTextArea()

    init {
        operandsTreeTextArea.isEditable = false
        operandsTreeTextArea.text = getTreesOutput(model)
        val operandsTreeScrollPane = JScrollPane(operandsTreeTextArea)

        bytecodeTextArea.isEditable = false
        bytecodeTextArea.text = getBytecodeOutput(model)
        val bytecodeScrollPane = JScrollPane(bytecodeTextArea)

        decompiledCodeTextArea.isEditable = false
        decompiledCodeTextArea.text = getDecompiledCode(model)
        val decompiledCodeScrollPane = JScrollPane(decompiledCodeTextArea)

        tabbedPane.addTab("Operands trees", operandsTreeScrollPane)
        tabbedPane.addTab("Bytecode", bytecodeScrollPane)
        tabbedPane.addTab("Decompiled", decompiledCodeScrollPane)

        contentPane = tabbedPane
        tabbedPane.border = BorderFactory.createEmptyBorder(8,8,8,8)
        minimumSize = Dimension(400,400)
    }

    private fun getTreesOutput(model: ExplainModel):String {
        val stringWriter = StringWriter()

        stringWriter.appendLine("Original tree:")
        model.originalTree.printDebugInformation(stringWriter)
        stringWriter.appendLine("\n\nRed operands tree:")
        model.redTree.printDebugInformation(stringWriter)
        stringWriter.appendLine("\nGreen operands tree:")
        model.greenTree.printDebugInformation(stringWriter)
        stringWriter.appendLine("\nBlue operands tree:")
        model.blueTree.printDebugInformation(stringWriter)

        return stringWriter.toString()
    }

    private fun getBytecodeOutput(model: ExplainModel):String {
        val classReader = ClassReader(model.classBytecode)
        val textifier = Textifier()
        val stringWriter = StringWriter()
        val stringWriterPrinter = PrintWriter(stringWriter)
        val traceClassVisitor = TraceClassVisitor(null, textifier, stringWriterPrinter)
        classReader.accept(traceClassVisitor,ClassReader.EXPAND_FRAMES)

        return stringWriter.toString()
    }

    private fun getDecompiledCode(model: ExplainModel):String {
        val loader = LoaderImpl(model.className to model.classBytecode)
        val printer = PrinterImpl()
        val decompiler = ClassFileToJavaSourceDecompiler()

        decompiler.decompile(loader,printer,model.className)
        return printer.toString()
    }
}