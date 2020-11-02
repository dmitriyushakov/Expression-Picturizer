package ru.dm_ushakov.picturizer.model.vectortree

class VectorVariableAccess(val variableName:String): VectorOperand {
    override fun toString() = "VectorVariableAccess = $variableName"
}