package ru.dm_ushakov.picturizer.visitor.vectortree

import ru.dm_ushakov.picturizer.model.vectortree.VectorOperand
import ru.dm_ushakov.picturizer.model.vectortree.VectorOperator
import ru.dm_ushakov.picturizer.utils.countArgsInDescriptor
import ru.dm_ushakov.picturizer.model.vectortree.MethodInvoke
import ru.dm_ushakov.picturizer.model.vectortree.VectorFunctionCall

class MethodInvokeReducer:AbstractVectorOperatorVisitor() {
    class MethodInformation(val aliasName:String,val owner:String,val name:String,val descriptor:String,val operandsCount:Int)
    private val registeredMethods = mutableListOf<MethodInformation>()

    override fun visit(operator: VectorOperator): VectorOperand {
        val operands = operator.operands.map { visitIfOperator(it) }
        if (operator is VectorFunctionCall) {
            val method = registeredMethods.firstOrNull { it.aliasName == operator.functionName && operands.size == it.operandsCount }
            if (method != null) {
                with(method) {
                    return MethodInvoke(owner, name, descriptor, operands)
                }
            }
        }

        return operator.replaceOperands(operands)
    }

    private fun registerMethod(aliasName:String,owner:String,name:String,descriptor:String,operandsCount: Int) {
        registeredMethods.add(MethodInformation(aliasName,owner,name,descriptor,operandsCount))
    }

    fun registerMethod(aliasName:String,owner: String,name: String,descriptor: String){
        registeredMethods.add(MethodInformation(aliasName,owner,name,descriptor,countArgsInDescriptor(descriptor)))
    }

    fun registerMethod(owner: String,name: String,descriptor: String) = registerMethod(name, owner, name, descriptor)

    fun debugList() {
        registeredMethods.forEach {
            with(it) {
                println("$aliasName, $owner, $name, $descriptor, $operandsCount")
            }
        }
    }
}