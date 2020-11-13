package ru.dm_ushakov.picturizer.model.vectortree

class MethodVariableAccess(val type:MethodVariableType):VectorOperand {
    override val resultType get() = ResultType.RealNumbers
    override fun toString() = "Method" +
            "VariableAccess = $type"
}