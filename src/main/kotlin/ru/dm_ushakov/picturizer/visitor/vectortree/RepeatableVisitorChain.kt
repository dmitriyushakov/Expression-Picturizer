package ru.dm_ushakov.picturizer.visitor.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.VectorOperand
import ru.dm_ushakov.picturizer.model.vectortree.VectorOperator
import ru.dm_ushakov.picturizer.utils.printDebugInformation

class RepeatableVisitorChain(val visitors: List<CheckableVectorOperatorVisitor>):AbstractVectorOperatorVisitor() {
    override fun visit(operator: VectorOperator): VectorOperand {
        var op = operator as VectorOperand
        var repeat = true
        while (repeat && op is VectorOperator) {
            repeat = false
            for(v in visitors) {
                if (op is VectorOperator) {
                    if(v.checkFor(op)) {
                        repeat = true
                        op = v.visit(op)
                    }
                }
            }
        }

        return op
    }
}