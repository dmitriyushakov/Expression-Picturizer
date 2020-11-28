package ru.dm_ushakov.picturizer.cg

import ru.dm_ushakov.picturizer.model.ExplainModel
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

fun explainExpression(expression: String, className: String): ExplainModel {
    val parsedExpression = Parser.parseExpression(expression)
    val reducedExpression = Mappings.reduceRGBOperatorsTree(parsedExpression)

    val redScalarTree = Mappings.reduceOperatorsTree(Mappings.convertToRedScalarTree(reducedExpression))
    val greenScalarTree = Mappings.reduceOperatorsTree(Mappings.convertToGreenScalarTree(reducedExpression))
    val blueScalarTree = Mappings.reduceOperatorsTree(Mappings.convertToBlueScalarTree(reducedExpression))

    val bytecode = RendererDump.dump(className,redScalarTree,greenScalarTree,blueScalarTree)

    val explainModel = ExplainModel(
        expression = expression,
        className = className,
        originalTree = parsedExpression,
        redTree = redScalarTree,
        greenTree = greenScalarTree,
        blueTree = blueScalarTree,
        classBytecode = bytecode
    )

    return explainModel
}