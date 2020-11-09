package ru.dm_ushakov.picturizer.model.vectortree

import ru.dm_ushakov.picturizer.utils.compilationError

class VectorFunctionCall(val functionName:String, override val operands:List<VectorOperand>):VectorOperand,VectorOperator {
    override fun replaceOperands(other: List<VectorOperand>) = VectorFunctionCall(functionName,other)
    override fun validateOperandsTypes() {
        compilationError("Undefined function call - $functionName")
    }
    override val resultType get() = ResultType.Undefined
    override fun toString() = "VectorFunctionCall ($functionName)"
}