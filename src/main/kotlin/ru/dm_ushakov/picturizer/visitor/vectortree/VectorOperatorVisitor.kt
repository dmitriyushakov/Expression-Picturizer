package ru.dm_ushakov.picturizer.visitor.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.VectorOperand
import ru.dm_ushakov.picturizer.model.vectortree.VectorOperator

interface VectorOperatorVisitor:(VectorOperator) -> VectorOperand {
    fun visit(operator: VectorOperator): VectorOperand
}