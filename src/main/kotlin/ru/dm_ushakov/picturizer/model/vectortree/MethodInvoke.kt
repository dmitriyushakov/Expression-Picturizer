package ru.dm_ushakov.picturizer.model.vectortree

import ru.dm_ushakov.picturizer.utils.compilationError

class MethodInvoke (val owner:String, val name:String, val descriptor:String,
                    override val operands: List<VectorOperand>
):VectorOperator {
    override fun replaceOperands(other: List<VectorOperand>): VectorOperator {
        if (other.size == operands.size) {
            return MethodInvoke(owner, name, descriptor, other)
        } else error("Replacing operands should have same count!")
    }

    override fun validateOperandsTypes() {
        if (operands.any { it.resultType != ResultType.RealNumbers }) {
            compilationError("Method invoke not allow any not real number operand.")
        }
    }

    override val resultType get() = ResultType.RealNumbers

    override fun toString() = "MethodInvoke owner=$owner, name=$name, desc=$descriptor"
}