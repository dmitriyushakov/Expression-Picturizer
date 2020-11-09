package ru.dm_ushakov.picturizer.model.vectortree

import ru.dm_ushakov.picturizer.utils.compilationError

class BinaryCompareVectorOperator(
    val operation:BinaryCompareVectorOperation,
    val leftOperand:VectorOperand,
    val rightOperand: VectorOperand
) :VectorOperator {
    override val operands: List<VectorOperand> get() = listOf(leftOperand,rightOperand)
    override fun replaceOperands(other: List<VectorOperand>): VectorOperator {
        if(other.size != 2) compilationError("Binary operator should receive 2 operands.")
        val (left,right) = other
        return BinaryCompareVectorOperator(operation,left,right)
    }

    override fun validateOperandsTypes() {
        if (leftOperand.resultType != ResultType.RealNumbers || rightOperand.resultType != ResultType.RealNumbers) {
            compilationError("Compare operator not allow not real numbers as any operand.")
        }
    }

    override val resultType get() = ResultType.Boolean
    val not:BinaryCompareVectorOperator get() = BinaryCompareVectorOperator(operation.not,leftOperand,rightOperand)
    override fun toString() = "BinaryCompareVectorOperator ($operation)"
}