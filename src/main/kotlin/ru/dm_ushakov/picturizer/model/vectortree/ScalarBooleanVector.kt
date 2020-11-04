package ru.dm_ushakov.picturizer.model.vectortree

class ScalarBooleanVector(val value: Boolean): VectorOperand {
    override val resultType get() = ResultType.Boolean
    override fun toString() = "ScalarBooleanVector = $value"
}