package ru.dm_ushakov.picturizer.extensions

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class BytecodeMethodCollector():ClassVisitor(Opcodes.ASM9) {
    private companion object {
        val allowableDescriptorChars = listOf('D','(',')')
    }

    private val mutableMethods = mutableListOf<ExtensionMethod>()
    val methods = mutableMethods as List<ExtensionMethod>
    lateinit var className:String

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        className = name
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {
        if (access.isStaticPublic && descriptor.isDoubleDescriptor) {
            mutableMethods.add(ExtensionMethod(className, name, descriptor))
        }

        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

    private val Int.isStaticPublic get() = (this and Opcodes.ACC_PUBLIC) != 0 && (this and Opcodes.ACC_STATIC) != 0
    private val String.isDoubleDescriptor get() = all { it in allowableDescriptorChars }
}