package ru.dm_ushakov.picturizer.model.vectortree

class ScalarBooleanVector(val value: Boolean): VectorOperand {
    override fun toString() = "BinaryVectorOperator = $value"
}