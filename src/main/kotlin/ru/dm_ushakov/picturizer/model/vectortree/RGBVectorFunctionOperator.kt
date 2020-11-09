package ru.dm_ushakov.picturizer.model.vectortree

import ru.dm_ushakov.picturizer.utils.compilationError

class RGBVectorFunctionOperator(val red:VectorOperand, val green:VectorOperand, val blue:VectorOperand) :VectorOperator {
    override val operands: List<VectorOperand> get() = listOf(red,green,blue)
    override fun replaceOperands(other: List<VectorOperand>): VectorOperator {
        val (red,green,blue) = other
        return RGBVectorFunctionOperator(red, green, blue)
    }

    override fun validateOperandsTypes() {
        error("Not allowed RGB call in final tree.")
    }

    override val resultType get() = if (red.resultType == green.resultType && red.resultType == blue.resultType) red.resultType
        else ResultType.Undefined

    companion object {
        fun fromVectorFunctionCall(call: VectorFunctionCall):RGBVectorFunctionOperator {
            val operands = call.operands
            if(operands.size!=3) compilationError("RGB operation should receive 3 arguments")
            val (red,green,blue) = operands
            return RGBVectorFunctionOperator(red, green, blue)
        }
    }
}