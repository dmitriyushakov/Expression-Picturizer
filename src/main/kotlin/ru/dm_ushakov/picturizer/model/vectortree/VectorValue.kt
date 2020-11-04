package ru.dm_ushakov.picturizer.model.vectortree

class VectorValue(val red:Double, val green:Double, val blue:Double):VectorOperand {
    constructor(value:Double):this(value,value,value)

    override val resultType get() = ResultType.RealNumbers

    override fun toString() = "VectorValue = $red,$green,$blue"
}