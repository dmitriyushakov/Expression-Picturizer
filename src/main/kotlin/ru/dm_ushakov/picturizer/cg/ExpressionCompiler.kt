package ru.dm_ushakov.picturizer.cg

import ru.dm_ushakov.picturizer.utils.invoke
import ru.dm_ushakov.picturizer.visitor.parser.Parser
import ru.dm_ushakov.picturizer.visitor.vectortree.Mappings

fun compileExpression(expression:String,className:String):ByteArray {
    var parsedExpression = Parser.parseExpression(expression)
    parsedExpression = Mappings.reduceRGBOperatorsTree(parsedExpression)

    val redScalarTree = Mappings.reduceOperatorsTree(Mappings.convertToRedScalarTree(parsedExpression))
    val greenScalarTree = Mappings.reduceOperatorsTree(Mappings.convertToGreenScalarTree(parsedExpression))
    val blueScalarTree = Mappings.reduceOperatorsTree(Mappings.convertToBlueScalarTree(parsedExpression))

    return RendererDump.dump(className,redScalarTree,greenScalarTree,blueScalarTree)
}