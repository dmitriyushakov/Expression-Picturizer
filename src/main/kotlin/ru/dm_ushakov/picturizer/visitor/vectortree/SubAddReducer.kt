package ru.dm_ushakov.picturizer.visitor.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.*
import ru.dm_ushakov.picturizer.model.vectortree.BinaryVectorOperation.Add
import ru.dm_ushakov.picturizer.model.vectortree.BinaryVectorOperation.Sub

object SubAddReducer:AbstractVectorOperatorVisitor(),CheckableVectorOperatorVisitor {
    override fun visit(operator: VectorOperator) =
            if (operator is BinaryVectorOperator && (operator.operation == Add || operator.operation == Sub)) {
                reduce(operator)
            } else visitOthers(operator)


    private fun visitOthers(operator: VectorOperator): VectorOperand {
        val newOperands = operator.operands.map { if(it is VectorOperator) visit(it) else it }
        return operator.replaceOperands(newOperands)
    }

    override fun checkFor(operator: VectorOperator): Boolean {
        if (operator is BinaryVectorOperator && (operator.operation == Add || operator.operation == Sub)) {
            val cnt = operator.collectOperandsAndOperations().count { it.second is VectorValue || it.second is ScalarValue }
            if(cnt>1) return true
        }

        return operator.operands.mapNotNull { it as? VectorOperator }.any { checkFor(it) }
    }

    private fun reduce(operator: VectorOperator): VectorOperand {
        val operands = operator.collectOperandsAndOperations()
        val values = operands.filter { it.second is VectorValue || it.second is ScalarValue }

        if (values.size > 1) {
            val notValues = operands.filter { it !in values }
            val firstPair = values.first()
            var reducedValue = if (firstPair.first == Add) firstPair.second else ScalarValue(0.0) - firstPair.second
            values.forEachIndexed { index, pair ->
                if (index > 0) {
                    reducedValue = if(pair.first == Add) {
                        reducedValue + pair.second
                    } else {
                        reducedValue - pair.second
                    }
                }
            }

            return (listOf(Add to reducedValue) + notValues).makeNewOperations()
        } else return operands.makeNewOperations()
    }

    private fun VectorOperator.collectOperandsAndOperations():List<Pair<BinaryVectorOperation,VectorOperand>> {
        val result = mutableListOf<Pair<BinaryVectorOperation,VectorOperand>>()
        collectOperandsAndOperations(this,result,true)
        return result
    }

    private fun collectOperandsAndOperations(operand: VectorOperand, result:MutableList<Pair<BinaryVectorOperation,VectorOperand>>,positive:Boolean) {
        if (operand is BinaryVectorOperator && (operand.operation == Add || operand.operation == Sub)) {
            collectOperandsAndOperations(operand.leftOperand,result,positive)
            collectOperandsAndOperations(operand.rightOperand,result,if (operand.operation == Add) positive else !positive)
        } else {
            val operation = if (positive) Add else Sub
            result.add(operation to operand)
        }
    }

    private fun List<Pair<BinaryVectorOperation,VectorOperand>>.makeNewOperations():VectorOperand {
        val firstPair = first()
        var operand:VectorOperand = if (firstPair.first == Add) visitIfOperator(firstPair.second) else BinaryVectorOperator(Sub,ScalarValue(0.0), visitIfOperator(firstPair.second))
        forEachIndexed { index, pair ->
            if (index > 0) {
                operand = BinaryVectorOperator(pair.first, visitIfOperator(operand), visitIfOperator(pair.second))
            }
        }

        return operand
    }

    private operator fun VectorOperand.plus(other:VectorOperand):VectorOperand =
            if (this is ScalarValue && other is ScalarValue) {
                ScalarValue(value + other.value)
            } else {
                val first = toVectorValue()
                val second = other.toVectorValue()
                VectorValue(first.red + second.red, first.green + second.green, first.blue + second.blue)
            }

    private operator fun VectorOperand.minus(other:VectorOperand):VectorOperand =
            if (this is ScalarValue && other is ScalarValue) {
                ScalarValue(value - other.value)
            } else {
                val first = toVectorValue()
                val second = other.toVectorValue()
                VectorValue(first.red - second.red, first.green - second.green, first.blue - second.blue)
            }

    private fun VectorOperand.toVectorValue() = when (this) {
        is ScalarValue -> VectorValue(value)
        is VectorValue -> this
        else -> error("Undefined operand for reducing operations.")
    }
}