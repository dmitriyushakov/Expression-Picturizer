package ru.dm_ushakov.picturizer.model.vectortree

interface VectorOperator:VectorOperand {
    val operands:List<VectorOperand>
    fun replaceOperands(other:List<VectorOperand>):VectorOperator
    fun validateOperandsTypes()
}