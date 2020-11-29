package ru.dm_ushakov.picturizer.extensions

import ru.dm_ushakov.picturizer.visitor.vectortree.MethodInvokeReducer
import ru.dm_ushakov.picturizer.visitor.vectortree.ProxyVectorOperatorVisitor
import ru.dm_ushakov.picturizer.visitor.vectortree.VectorOperatorVisitor

object ExtensionsRegistry {
    private val proxyReducer = ProxyVectorOperatorVisitor(MethodInvokeReducer())
    val reducer get() = proxyReducer as VectorOperatorVisitor
    var extensionMethods:List<ExtensionMethod> = listOf()
    var extensionsList:List<ExtensionClass> = listOf()
        set(value) {
            field = value
            extensionMethods = value.flatMap { it.methods }
            proxyReducer.targetVisitor = getMethodInvokeReducer()
        }

    private fun getMethodInvokeReducer() = MethodInvokeReducer().apply {
        for (m in extensionMethods) {
            registerMethod(m.owner,m.name,m.descriptor)
        }
    }
}