package ru.dm_ushakov.picturizer.model.vectortree

class VectorBooleanValue(val red:Boolean, val green:Boolean, val blue:Boolean):VectorOperand {
    constructor(value:Boolean):this(value,value,value)

    override val resultType get() = ResultType.Boolean

    override fun toString() = "VectorBooleanValue = $red,$green,$blue"
}