package ru.dm_ushakov.picturizer.visitor.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.VectorOperand
import ru.dm_ushakov.picturizer.model.vectortree.VectorOperator
import ru.dm_ushakov.picturizer.utils.identity
import ru.dm_ushakov.picturizer.utils.identityValue

class VisitorChain(val visitors:List<VectorOperatorVisitor>):AbstractVectorOperatorVisitor() {
    override fun visit(operator: VectorOperator): VectorOperand {
        var op: VectorOperand = operator
        for(v in visitors) op = op.let { if(it is VectorOperator) v(it) else v(it.identity) }
        return op.identityValue
    }
}