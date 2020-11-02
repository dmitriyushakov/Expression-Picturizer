package ru.dm_ushakov.picturizer

import ru.dm_ushakov.picturizer.visitor.parser.Parser
import ru.dm_ushakov.picturizer.utils.printDebugInformation
import ru.dm_ushakov.picturizer.visitor.vectortree.Mappings
import ru.dm_ushakov.picturizer.utils.invoke
import java.util.*

fun main() {
    val sc = Scanner(System.`in`)

    while (true) {
        print(" > ")
        val expression = sc.nextLine()
        println()
        var parsedExpression = Parser.parseExpression(expression)
        var redScalarTree = Mappings.convertToRedScalarTree(parsedExpression)
        println("Red tree:")
        redScalarTree = Mappings.reduceOperatorsTree(redScalarTree)
        redScalarTree.printDebugInformation()
        println()
    }
}