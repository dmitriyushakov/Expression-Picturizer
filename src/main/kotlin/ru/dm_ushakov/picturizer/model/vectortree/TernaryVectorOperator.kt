package ru.dm_ushakov.picturizer.model.vectortree

import ru.dm_ushakov.picturizer.utils.compilationError

class TernaryVectorOperator(val condition: VectorOperand,val trueExpression:VectorOperand,val falseExpression: VectorOperand):VectorOperator {
    override val operands: List<VectorOperand> get() = listOf(condition,trueExpression,falseExpression)
    override fun replaceOperands(other: List<VectorOperand>): TernaryVectorOperator {
        if(other.size != 2) compilationError("Binary operator should receive 2 operands.")
        val (newCondition,newTrueExpression,newFalseExpression) = other
        return TernaryVectorOperator(condition, trueExpression, falseExpression)
    }

    override fun toString() = "TernaryVectorOperator"
}