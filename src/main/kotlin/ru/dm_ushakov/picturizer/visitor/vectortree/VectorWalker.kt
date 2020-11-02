package ru.dm_ushakov.picturizer.visitor.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.VectorOperand
import ru.dm_ushakov.picturizer.model.vectortree.VectorOperator

class VectorWalker(
        val mapOperand:(VectorOperand) -> VectorOperand,
        val mapOperator:(VectorOperator) -> VectorOperand
):AbstractVectorOperatorVisitor() {
    override fun visit(operator: VectorOperator): VectorOperand {
        val newOperands = operator.operands.map { if(it is VectorOperator) visit(it) else mapOperand(it) }
        return mapOperator(operator.replaceOperands(newOperands))
    }
}