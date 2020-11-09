package ru.dm_ushakov.picturizer.visitor.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.VectorOperand
import ru.dm_ushakov.picturizer.model.vectortree.VectorOperator
import ru.dm_ushakov.picturizer.utils.identity
import ru.dm_ushakov.picturizer.utils.identityValue

class IdentityVectorOperatorVisitor(private val visitor:VectorOperatorVisitor):AbstractVectorOperatorVisitor() {
    override fun visit(operator: VectorOperator): VectorOperand {
        return visitor.visit(operator.identity).identityValue
    }
}