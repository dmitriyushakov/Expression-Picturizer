package ru.dm_ushakov.picturizer.utils

import ru.dm_ushakov.picturizer.visitor.vectortree.*

fun walker(initializer:VectorWalkerBuilder.() -> Unit) = VectorWalkerBuilder().apply(initializer).build()
fun opVisitorChain(vararg visitors:VectorOperatorVisitor) = VisitorChain(visitors.toList())
fun repeatableOpVisitorChain(vararg visitors:CheckableVectorOperatorVisitor) = RepeatableVisitorChain(visitors.toList())