package ru.dm_ushakov.picturizer.utils

import ru.dm_ushakov.picturizer.model.vectortree.VectorOperand
import ru.dm_ushakov.picturizer.model.vectortree.VectorOperator
import java.io.OutputStreamWriter
import java.io.Writer

private fun VectorOperand.printDebugInformation(level:Int,writer: Writer) {
    for(i in 0..(level*2)) writer.append(' ')
    writer.appendLine(toString())
    writer.flush()

    if (this is VectorOperator) {
        for(child in operands) {
            child.printDebugInformation(level + 1, writer)
        }
    }
}

fun VectorOperand.printDebugInformation(writer: Writer) = printDebugInformation(0,writer)
fun VectorOperand.printDebugInformation() = printDebugInformation(OutputStreamWriter(System.out))