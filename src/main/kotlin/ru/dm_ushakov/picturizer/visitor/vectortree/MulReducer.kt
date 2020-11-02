package ru.dm_ushakov.picturizer.visitor.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.*
import ru.dm_ushakov.picturizer.utils.collectOperands

object MulReducer:AbstractVectorOperatorVisitor(),CheckableVectorOperatorVisitor {

    override fun visit(operator: VectorOperator): VectorOperand {
        return if (operator is BinaryVectorOperator && operator.operation == BinaryVectorOperation.Mul) {
            reduce(operator)
        } else visitOthers(operator)
    }

    private fun visitOthers(operator: VectorOperator): VectorOperand {
        val newOperands = operator.operands.map { if(it is VectorOperator) visit(it) else it }
        return operator.replaceOperands(newOperands)
    }

    private fun visitIfOperator(op: VectorOperand) = if (op is VectorOperator) visit(op) else op

    override fun checkFor(operator: VectorOperator): Boolean {
        if(operator is BinaryVectorOperator && operator.operation == BinaryVectorOperation.Mul) {
            val cnt = operator.collectOperands().count { it is VectorValue || it is ScalarValue }
            if (cnt > 1) return true
        }

        return operator.operands.mapNotNull { it as? VectorOperator }.any { checkFor(it) }
    }

    private fun reduce(operator:BinaryVectorOperator):VectorOperand {
        val operandsList = operator.collectOperands()
        val values = operandsList.filter { it is VectorValue || it is ScalarValue }
        if(values.size > 1) {
            val others = operandsList.filter { it !in values }
            val targetValue = if ( values.any { it is VectorValue } ) {
                var acc = values.first().asVector()
                values.forEachIndexed { index, vectorOperand ->
                    val vector = vectorOperand.asVector()
                    if (index != 0) acc = VectorValue(
                        acc.red * vector.red,
                        acc.green * vector.green,
                        acc.blue * vector.blue)
                }

                acc
            } else {
                var acc = values.first().asScalar()
                values.forEachIndexed { index, vectorOperand ->
                    val vector = vectorOperand.asScalar()
                    if (index != 0) acc = ScalarValue(
                        acc.value * vector.value)
                }

                acc
            }
            val resultOperands = listOf(targetValue) + others

            return resultOperands.makeMultiply()
        } else return operandsList.makeMultiply()
    }

    private fun List<VectorOperand>.makeMultiply() = if (size==1) {
        visitIfOperator(get(0))
    } else if(size>1) {
        var acc = BinaryVectorOperator(BinaryVectorOperation.Mul,
            visitIfOperator(get(0)),
            visitIfOperator(get(1)))

        forEachIndexed { index, vectorOperand ->
            if (index > 1) {
                acc = BinaryVectorOperator(BinaryVectorOperation.Mul,acc,visitIfOperator(vectorOperand))
            }
        }

        acc
    } else error("Unexpected situation, operand list can't be empty.")

    private fun VectorOperand.asVector() = when (this) {
        is VectorValue -> this
        is ScalarValue -> VectorValue(value)
        else -> error("Unexpected values among VectorValue|ScalarValue list!")
    }

    private fun VectorOperand.asScalar() = when (this) {
        is ScalarValue -> this
        else -> error("Unexpected values among ScalarValue list!")
    }
}