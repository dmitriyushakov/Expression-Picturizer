package ru.dm_ushakov.picturizer.visitor.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.*
import ru.dm_ushakov.picturizer.utils.*

object Mappings {
    fun getMapperToScalar(mapping:(VectorValue) -> Double,booleanMapping:(VectorBooleanValue) -> Boolean) = walker {
        mapOperand = {
            when (it) {
                is VectorValue -> ScalarValue(mapping(it))
                is VectorBooleanValue -> ScalarBooleanVector(booleanMapping(it))
                else -> it
            }
        }
    }

    val toRedScalar = getMapperToScalar({ it.red }, { it.red })
    val toGreenScalar = getMapperToScalar({ it.green },{ it.green })
    val toBlueScalar = getMapperToScalar({ it.blue },{ it.blue })

    val wipeMultiplicationZeroes = walker {
        mapOperator = {
            if (it is BinaryVectorOperator) {
                if (it.operation == BinaryVectorOperation.Mul) {
                    if (it.leftOperand.isZero || it.rightOperand.isZero) ScalarValue(0.0)
                    else it
                } else if (it.operation == BinaryVectorOperation.Div) {
                    if (it.leftOperand.isZero) ScalarValue(0.0)
                    else it
                } else it
            } else it
        }
    }

    val wipeAdditionZeroes = walker {
        mapOperator = {
            if (it is BinaryVectorOperator) {
                if (it.operation == BinaryVectorOperation.Add) {
                    when {
                        it.leftOperand.isZero && it.rightOperand.isZero -> ScalarValue(0.0)
                        it.leftOperand.isZero -> it.rightOperand
                        it.rightOperand.isZero -> it.leftOperand
                        else -> it
                    }
                } else if(it.operation == BinaryVectorOperation.Sub) {
                    val right = it.rightOperand
                    val left = it.leftOperand
                    when {
                        left.isZero && right.isZero -> ScalarValue(0.0)
                        right.isZero -> left
                        left.isZero && right is ScalarValue -> right.negative
                        else -> it
                    }
                } else it
            } else it
        }
    }

    val wipeMultiplicationOnes = walker {
        mapOperator = {
            if (it is BinaryVectorOperator) {
                if (it.operation == BinaryVectorOperation.Mul) {
                    when {
                        it.leftOperand.isOne -> it.rightOperand
                        it.rightOperand.isOne -> it.leftOperand
                        else -> it
                    }
                } else it
            } else it
        }
    }

    val replaceLogicOperatorsForReal = walker {
        mapOperator = {
            if (it is BinaryBooleanVectorOperator) {
                if (it.leftOperand.resultType == ResultType.RealNumbers && it.rightOperand.resultType == ResultType.RealNumbers) {
                    val functionName = when(it.operation) {
                        BinaryBooleanVectorOperation.And -> "min"
                        BinaryBooleanVectorOperation.Or -> "max"
                    }
                    VectorFunctionCall(functionName,it.operands)
                } else it
            } else it
        }
    }

    val reduceSimpleTernaryOperator = walker {
        mapOperator = {
            if (it is TernaryVectorOperator) {
                val cond = it.condition
                if (cond is ScalarBooleanVector) {
                    if (cond.value) it.trueExpression
                    else it.falseExpression
                } else it
            } else it
        }
    }

    val methodInvokeReducer = MethodInvokeReducer().apply {
        registerMethod("java/lang/Math","max","(DD)D")
        registerMethod("java/lang/Math","min","(DD)D")
    }

    val vectorVariableAccessReducer = VectorVariableAccessReducer().apply {
        addVariable("pi",Math.PI)
        addVariable("e",Math.E)
        addVariable("x",MethodArgumentVariableAccess(MethodArgumentVariableType.X))
        addVariable("y",MethodArgumentVariableAccess(MethodArgumentVariableType.Y))
        addVariable("a",MethodArgumentVariableAccess(MethodArgumentVariableType.Angle))
        addVariable("r",MethodArgumentVariableAccess(MethodArgumentVariableType.Radius))
        addVariable("red",VectorBooleanValue(true,false,false))
        addVariable("green",VectorBooleanValue(false,true,false))
        addVariable("blue",VectorBooleanValue(false,false,true))
    }

    val replaceRGBFunctionToOperator = walker {
        mapOperator = {
            if (it is VectorFunctionCall && it.functionName=="rgb") RGBVectorFunctionOperator.fromVectorFunctionCall(it)
            else it
        }
    }

    fun getMapperFromRGB(mapper:(RGBVectorFunctionOperator) -> VectorOperand) = walker {
        mapOperator = { if (it is RGBVectorFunctionOperator) mapper(it) else it }
    }

    val extractRGBRed = getMapperFromRGB { it.red }
    val extractRGBGreen = getMapperFromRGB { it.green }
    val extractRGBBlue = getMapperFromRGB { it.blue }

    val convertToRedScalarTree = opVisitorChain(extractRGBRed, toRedScalar)
    val convertToGreenScalarTree = opVisitorChain(extractRGBGreen, toGreenScalar)
    val convertToBlueScalarTree = opVisitorChain(extractRGBBlue, toBlueScalar)

    val reduceRGBOperatorsTree = opVisitorChain(vectorVariableAccessReducer, replaceRGBFunctionToOperator)
    val reduceOperatorsTree = opVisitorChain(
            wipeMultiplicationZeroes,
            wipeAdditionZeroes,
            wipeMultiplicationOnes,
            replaceLogicOperatorsForReal,
            BooleanMulReducer,
            reduceSimpleTernaryOperator,
            repeatableOpVisitorChain(DivReducer, MulReducer, SubAddReducer),
            methodInvokeReducer,
            ValidationVisitor
    )
}