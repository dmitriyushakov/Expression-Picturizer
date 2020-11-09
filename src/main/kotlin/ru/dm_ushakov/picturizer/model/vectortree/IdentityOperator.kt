package ru.dm_ushakov.picturizer.model.vectortree

class IdentityOperator(val operand: VectorOperand):VectorOperator {
    override val operands get() = listOf(operand)
    override val resultType get() = operand.resultType
    override fun replaceOperands(other: List<VectorOperand>): VectorOperator {
        if (other.size != 1) error("Identity operator should have 1 value!")
        else return IdentityOperator(other[0])
    }

    override fun validateOperandsTypes() {

    }
}