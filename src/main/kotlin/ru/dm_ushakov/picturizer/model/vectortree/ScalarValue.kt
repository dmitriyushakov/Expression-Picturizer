package ru.dm_ushakov.picturizer.model.vectortree

class ScalarValue(val value: Double): VectorOperand {
    override val resultType get() = ResultType.RealNumbers
    override fun toString() = "ScalarValue = $value"
}