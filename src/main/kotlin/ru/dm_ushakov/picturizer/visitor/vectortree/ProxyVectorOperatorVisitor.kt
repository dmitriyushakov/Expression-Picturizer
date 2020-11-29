package ru.dm_ushakov.picturizer.visitor.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.VectorOperator

class ProxyVectorOperatorVisitor(var targetVisitor:VectorOperatorVisitor):AbstractVectorOperatorVisitor() {
    override fun visit(operator: VectorOperator) = targetVisitor.visit(operator)
}