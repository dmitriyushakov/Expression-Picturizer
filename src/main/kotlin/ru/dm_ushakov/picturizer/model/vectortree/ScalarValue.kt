package ru.dm_ushakov.picturizer.model.vectortree

class ScalarValue(val value: Double): VectorOperand {
    override fun toString() = "ScalarValue = $value"
}