package ru.dm_ushakov.picturizer.decompiler

import org.jd.core.v1.api.printer.Printer

class PrinterImpl:Printer {
    private companion object {
        const val TAB = "    "
        const val NEWLINE = "\n"
    }

    private var identationCount = 0
    private val sb = StringBuilder()

    override fun start(maxLineNumber: Int, majorVersion: Int, minorVersion: Int) = Unit
    override fun end() = Unit

    override fun printText(text: String) {
        sb.append(text)
    }

    override fun printNumericConstant(constant: String) {
        sb.append(constant)
    }

    override fun printStringConstant(constant: String, ownerInternalName: String) {
        sb.append(constant)
    }

    override fun printKeyword(keyword: String) {
        sb.append(keyword)
    }

    override fun printDeclaration(type: Int, internalTypeName: String, name: String, descriptor: String?) {
        sb.append(name)
    }

    override fun printReference(
        type: Int,
        internalTypeName: String,
        name: String,
        descriptor: String?,
        ownerInternalName: String?
    ) {
        sb.append(name)
    }

    override fun indent() {
        identationCount++
    }

    override fun unindent() {
        identationCount--
    }

    override fun startLine(lineNumber: Int) {
        for(i in 0 until identationCount) sb.append(TAB)
    }

    override fun endLine() {
        sb.append(NEWLINE)
    }

    override fun extraLine(count: Int) {
        for (i in 0 until count) sb.append(NEWLINE)
    }

    override fun startMarker(type: Int) = Unit
    override fun endMarker(type: Int) = Unit

    override fun toString() = sb.toString()
}