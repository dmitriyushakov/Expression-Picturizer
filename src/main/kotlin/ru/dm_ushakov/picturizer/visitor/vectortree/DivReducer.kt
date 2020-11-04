package ru.dm_ushakov.picturizer.visitor.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.BinaryVectorOperation.Div
import ru.dm_ushakov.picturizer.model.vectortree.BinaryVectorOperation.Mul
import ru.dm_ushakov.picturizer.model.vectortree.BinaryVectorOperator
import ru.dm_ushakov.picturizer.model.vectortree.VectorOperand
import ru.dm_ushakov.picturizer.model.vectortree.VectorOperator

object DivReducer:AbstractVectorOperatorVisitor(),CheckableVectorOperatorVisitor {
    override fun visit(operator: VectorOperator): VectorOperand {
        if (operator is BinaryVectorOperator && operator.operation == Div){
            val leftOperand = operator.leftOperand
            if(leftOperand is BinaryVectorOperator && leftOperand.operation == Div) {
                return BinaryVectorOperator(Div,
                        visitIfOperator(leftOperand.leftOperand),
                        BinaryVectorOperator(Mul,
                                visitIfOperator(leftOperand.rightOperand),
                                visitIfOperator(operator.rightOperand)
                        )
                )
            }
        }

        return operator.replaceOperands(operator.operands.map { visitIfOperator(it) })
    }

    override fun checkFor(operator: VectorOperator): Boolean {
        if (operator is BinaryVectorOperator && operator.operation == Div){
            val leftOperand = operator.leftOperand
            if(leftOperand is BinaryVectorOperator && leftOperand.operation == Div) return true
        }

        return operator.operands.mapNotNull { it as? VectorOperator }.any { checkFor(it) }
    }
}