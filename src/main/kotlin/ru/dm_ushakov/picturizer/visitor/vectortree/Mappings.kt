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

    val booleanToRealReducer = walker {
        mapOperator = {
            if ((it is BinaryVectorOperator && (
                            it.operation == BinaryVectorOperation.Add ||
                            it.operation == BinaryVectorOperation.Sub)) ||
                            it is IdentityOperator) {
                val operands = it.operands.map { op ->
                    if (op.resultType == ResultType.Boolean) {
                        TernaryVectorOperator(op,ScalarValue(1.0),ScalarValue(0.0))
                    } else op
                }

                it.replaceOperands(operands)
            } else it
        }
    }.identity

    val methodInvokeReducer = MethodInvokeReducer().apply {
        registerMethod("java/lang/Math","max","(DD)D")
        registerMethod("java/lang/Math","min","(DD)D")
        registerMethod("java/lang/Math","sin","(D)D")
        registerMethod("java/lang/Math","cos","(D)D")
        registerMethod("java/lang/Math","tan","(D)D")
        registerMethod("java/lang/Math","asin","(D)D")
        registerMethod("java/lang/Math","acos","(D)D")
        registerMethod("java/lang/Math","atan","(D)D")
        registerMethod("java/lang/Math","atan2","(DD)D")
        registerMethod("java/lang/Math","pow","(DD)D")
        registerMethod("java/lang/Math","abs","(D)D")
        registerMethod("java/lang/Math","sinh","(D)D")
        registerMethod("java/lang/Math","cosh","(D)D")
        registerMethod("java/lang/Math","tanh","(D)D")
        registerMethod("java/lang/Math","sqrt","(D)D")
        registerMethod("java/lang/Math","floor","(D)D")
        registerMethod("java/lang/Math","ceil","(D)D")
        registerMethod("java/lang/Math","log","(D)D")
        registerMethod("java/lang/Math","log10","(D)D")
        registerMethod("java/lang/Math","exp","(D)D")
    }

    val vectorVariableAccessReducer = VectorVariableAccessReducer().apply {
        addVariable("pi",Math.PI)
        addVariable("e",Math.E)
        addVariable("x",MethodVariableAccess(MethodVariableType.X))
        addVariable("y",MethodVariableAccess(MethodVariableType.Y))
        addVariable("a",MethodVariableAccess(MethodVariableType.Angle))
        addVariable("r",MethodVariableAccess(MethodVariableType.Radius))
        addVariable("w",MethodVariableAccess(MethodVariableType.Width))
        addVariable("h",MethodVariableAccess(MethodVariableType.Height))
        addVariable("t",MethodVariableAccess(MethodVariableType.Time))
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
            methodInvokeReducer,
            booleanToRealReducer,
            reduceSimpleTernaryOperator,
            repeatableOpVisitorChain(DivReducer, MulReducer, SubAddReducer),
            ValidationVisitor
    )
}