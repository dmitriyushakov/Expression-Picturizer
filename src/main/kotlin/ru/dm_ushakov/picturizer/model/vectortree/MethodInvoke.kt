package ru.dm_ushakov.picturizer.model.vectortree

class MethodInvoke (val owner:String, val name:String, val descriptor:String,
                    override val operands: List<VectorOperand>
):VectorOperator {
    override fun replaceOperands(other: List<VectorOperand>): VectorOperator {
        if (other.size == operands.size) {
            return MethodInvoke(owner, name, descriptor, other)
        } else error("Replacing operands should have same count!")
    }
    override fun toString() = "MethodInvoke owner=$owner, name=$name, desc=$descriptor"
}