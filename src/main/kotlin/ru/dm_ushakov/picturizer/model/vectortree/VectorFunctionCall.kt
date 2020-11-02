package ru.dm_ushakov.picturizer.model.vectortree

class VectorFunctionCall(val functionName:String, override val operands:List<VectorOperand>):VectorOperand,VectorOperator {
    override fun replaceOperands(other: List<VectorOperand>) = VectorFunctionCall(functionName,other)
    override fun toString() = "VectorFunctionCall ($functionName)"
}