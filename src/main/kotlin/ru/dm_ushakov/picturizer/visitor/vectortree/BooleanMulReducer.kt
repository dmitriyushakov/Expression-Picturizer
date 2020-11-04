package ru.dm_ushakov.picturizer.visitor.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.*

object BooleanMulReducer: AbstractVectorOperatorVisitor() {
    override fun visit(operator: VectorOperator): VectorOperand {
        if (operator is BinaryVectorOperator && operator.operation == BinaryVectorOperation.Mul) {
            val operands = operator.collectOperands().map { visitIfOperator(it) }
            val boolOperands = operands.filter { it.resultType == ResultType.Boolean}
            val otherOperands = operands
                    .filter { it.resultType != ResultType.Boolean }
                    .let {
                        if (it.isEmpty()) listOf(ScalarValue(1.0))
                        else it
                    }

            if(boolOperands.isEmpty()) {
                return otherOperands.buildRealNumExpression()
            } else {
                return TernaryVectorOperator(
                        boolOperands.buildBooleanExpression(),
                        otherOperands.buildRealNumExpression(),
                        ScalarValue(0.0)
                )
            }
        } else {
            val operands = operator.operands.map { visitIfOperator(it) }
            return operator.replaceOperands(operands)
        }
    }

    private fun BinaryVectorOperator.collectOperands():List<VectorOperand> {
        val operands = mutableListOf<VectorOperand>()
        collectOperands(operands)
        return operands
    }

    private fun VectorOperand.collectOperands(operands:MutableList<VectorOperand>) {
        if (this is BinaryVectorOperator && operation == BinaryVectorOperation.Mul) {
            leftOperand.collectOperands(operands)
            rightOperand.collectOperands(operands)
        } else {
            operands.add(this)
        }
    }

    private fun List<VectorOperand>.buildBooleanExpression():VectorOperand {
        var op = first()
        forEachIndexed { index, vectorOperand ->
            if (index != 0) {
                op = BinaryBooleanVectorOperator(BinaryBooleanVectorOperation.And,op,vectorOperand)
            }
        }
        return op
    }

    private fun List<VectorOperand>.buildRealNumExpression():VectorOperand {
        var op = first()
        forEachIndexed { index, vectorOperand ->
            if (index != 0) {
                op = BinaryVectorOperator(BinaryVectorOperation.Mul,op,vectorOperand)
            }
        }
        return op
    }
}