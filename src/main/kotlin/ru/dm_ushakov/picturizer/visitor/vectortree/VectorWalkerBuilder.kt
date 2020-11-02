package ru.dm_ushakov.picturizer.visitor.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.VectorOperand
import ru.dm_ushakov.picturizer.model.vectortree.VectorOperator

class VectorWalkerBuilder {
    var mapOperand:(VectorOperand) -> VectorOperand = { it }
    var mapOperator:(VectorOperator) -> VectorOperand = { it }
    fun build() = VectorWalker(mapOperand, mapOperator)
}