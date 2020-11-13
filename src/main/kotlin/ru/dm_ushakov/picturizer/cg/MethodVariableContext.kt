package ru.dm_ushakov.picturizer.cg

import ru.dm_ushakov.picturizer.model.vectortree.MethodVariableType

class MethodVariableContext (val variablesOffset:Int) {
    var usedSlots = 0
        private set
    private val variablesList:MutableList<MethodVariable> = mutableListOf()
    val variables = variablesList.toList()

    fun addVariable(type:MethodVariableType) {
        variablesList.add(MethodVariable(variablesOffset + usedSlots * 2,type))
    }

    operator fun get(type:MethodVariableType) = variablesList.find { it.type == type } ?: error("Method variable not found! Type - $type")
}