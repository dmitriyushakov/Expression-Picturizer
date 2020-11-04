package ru.dm_ushakov.picturizer.model.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.BinaryBooleanVectorOperation.*
import ru.dm_ushakov.picturizer.utils.compilationError

class BinaryBooleanVectorOperator(
    val operation:BinaryBooleanVectorOperation,
    val leftOperand:VectorOperand,
    val rightOperand: VectorOperand
) :VectorOperator {
    override val operands: List<VectorOperand> get() = listOf(leftOperand,rightOperand)
    override fun replaceOperands(other: List<VectorOperand>): VectorOperator {
        if(other.size != 2) compilationError("Binary operator should receive 2 operands.")
        val (left,right) = other
        return BinaryBooleanVectorOperator(operation,left,right)
    }

    override val resultType get() = ResultType.Boolean

    val not:BinaryBooleanVectorOperator get() {
        val otherOperation = when(operation) {
            And -> Or
            Or -> And
        }

        return BinaryBooleanVectorOperator(otherOperation,leftOperand.not,rightOperand.not)
    }

    private val VectorOperand.not:VectorOperand get() = when (this) {
        is BinaryCompareVectorOperator -> this.not
        is BinaryBooleanVectorOperator -> this.not
        else -> compilationError("Operand \"${this::class.simpleName}\" couldn't be mapped by \"not\" operation!")
    }

    override fun toString() = "BinaryBooleanVectorOperator ($operation)"
}