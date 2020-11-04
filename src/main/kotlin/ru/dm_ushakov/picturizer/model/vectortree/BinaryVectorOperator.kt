package ru.dm_ushakov.picturizer.model.vectortree

import ru.dm_ushakov.picturizer.utils.compilationError

class BinaryVectorOperator(
        val operation:BinaryVectorOperation,
        val leftOperand:VectorOperand,
        val rightOperand: VectorOperand
) :VectorOperator {
    override val operands: List<VectorOperand> get() = listOf(leftOperand,rightOperand)
    override fun replaceOperands(other: List<VectorOperand>): VectorOperator {
        if(other.size != 2) compilationError("Binary operator should receive 2 operands.")
        val (left,right) = other
        return BinaryVectorOperator(operation,left,right)
    }

    override val resultType get() = ResultType.RealNumbers

    override fun toString() = "BinaryVectorOperator ($operation)"
}