package ru.dm_ushakov.picturizer.model.vectortree

class VectorVariableAccess(val variableName:String): VectorOperand {
    override val resultType get() = ResultType.Undefined
    override fun toString() = "VectorVariableAccess = $variableName"
}