package ru.dm_ushakov.picturizer.model.vectortree

import ru.dm_ushakov.picturizer.utils.compilationError

class TernaryVectorOperator(val condition: VectorOperand,val trueExpression:VectorOperand,val falseExpression: VectorOperand):VectorOperator {
    override val operands: List<VectorOperand> get() = listOf(condition,trueExpression,falseExpression)
    override fun replaceOperands(other: List<VectorOperand>): TernaryVectorOperator {
        if(other.size != 3) error("Ternary operator should receive 3 operands.")
        val (newCondition,newTrueExpression,newFalseExpression) = other
        return TernaryVectorOperator(newCondition, newTrueExpression, newFalseExpression)
    }

    override fun validateOperandsTypes() {
        if (condition.resultType != ResultType.Boolean) {
            compilationError("Unexpected type for condition!")
        }
        if (trueExpression.resultType != falseExpression.resultType) {
            compilationError("Branches expressions not match!")
        }
    }

    override val resultType get() = if (trueExpression.resultType == falseExpression.resultType) trueExpression.resultType
        else ResultType.Undefined

    override fun toString() = "TernaryVectorOperator"
}