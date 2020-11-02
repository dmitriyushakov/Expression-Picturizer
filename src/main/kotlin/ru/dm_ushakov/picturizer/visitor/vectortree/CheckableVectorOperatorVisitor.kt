package ru.dm_ushakov.picturizer.visitor.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.VectorOperator

interface CheckableVectorOperatorVisitor:VectorOperatorVisitor {
    fun checkFor(operator: VectorOperator):Boolean
}