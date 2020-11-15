package ru.dm_ushakov.picturizer

import java.util.*
import ru.dm_ushakov.picturizer.cg.compileExpression
import java.io.FileOutputStream

fun main() {
    val sc = Scanner(System.`in`)
    var iteration = 0
    while (true) {
        print(" > ")
        val expression = sc.nextLine()
        println()
        val className = "CompiledExpression$iteration"
        val classFileName = "$className.class"
        val bytecode = compileExpression(expression,className)
        FileOutputStream(classFileName,false).use { fout ->
            fout.write(bytecode)
            println("Expression compiled into \"$classFileName\"")
        }
        iteration++
    }
}