package ru.dm_ushakov.picturizer.visitor.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.ScalarValue
import ru.dm_ushakov.picturizer.model.vectortree.VectorOperand
import ru.dm_ushakov.picturizer.model.vectortree.VectorOperator
import ru.dm_ushakov.picturizer.model.vectortree.VectorVariableAccess

class VectorVariableAccessReducer:AbstractVectorOperatorVisitor() {
    private val variableMap:MutableMap<String,VectorOperand> = mutableMapOf()
    override fun visit(operator: VectorOperator): VectorOperand {
        val operands = operator
                .operands
                .map { visitIfOperator(it) }
                .map { if (it is VectorVariableAccess) mapVariable(it) else it }

        return operator.replaceOperands(operands)
    }

    private fun mapVariable(variableAccess: VectorVariableAccess) = variableMap.getOrDefault(variableAccess.variableName,variableAccess)

    fun addVariable(key:String,op:VectorOperand) {
        variableMap[key] = op
    }
    fun addVariable(key:String,op:Double) {
        addVariable(key, ScalarValue(op))
    }
}