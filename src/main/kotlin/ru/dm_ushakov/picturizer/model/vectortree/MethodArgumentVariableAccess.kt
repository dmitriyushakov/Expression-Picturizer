package ru.dm_ushakov.picturizer.model.vectortree

class MethodArgumentVariableAccess(val type:MethodArgumentVariableType):VectorOperand {
    override val resultType get() = ResultType.RealNumbers
    override fun toString() = "MethodArgumentVariableAccess = $type"
}