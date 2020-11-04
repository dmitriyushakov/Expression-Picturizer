package ru.dm_ushakov.picturizer.visitor.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.VectorOperand
import ru.dm_ushakov.picturizer.model.vectortree.VectorOperator

abstract class AbstractVectorOperatorVisitor:VectorOperatorVisitor{
    override fun invoke(operator: VectorOperator) = visit(operator)
    protected fun visitIfOperator(op: VectorOperand) = if (op is VectorOperator) visit(op) else op
}