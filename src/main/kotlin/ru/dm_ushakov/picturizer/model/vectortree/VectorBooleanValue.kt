package ru.dm_ushakov.picturizer.model.vectortree

class VectorBooleanValue(val red:Boolean, val green:Boolean, val blue:Boolean):VectorOperand {
    constructor(value:Boolean):this(value,value,value)
    override fun toString() = "VectorBooleanValue = $red,$green,$blue"
}