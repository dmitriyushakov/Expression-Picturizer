package ru.dm_ushakov.picturizer.visitor.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.VectorOperand
import ru.dm_ushakov.picturizer.model.vectortree.VectorOperator

object ValidationVisitor: AbstractVectorOperatorVisitor() {
    override fun visit(operator: VectorOperator): VectorOperand {
        operator.operands.forEach(this::visitIfOperator)
        operator.validateOperandsTypes()
        return operator
    }
}