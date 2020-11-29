package ru.dm_ushakov.picturizer.extensions

import org.objectweb.asm.ClassReader

import java.io.File
import java.io.FileInputStream

class ExtensionClass (
    val fileName: String,
    val className: String,
    val bytecode: ByteArray,
    val methods: List<ExtensionMethod>
) {
    companion object {
        fun load(file: File):ExtensionClass {
            val fileName = file.path

            FileInputStream(fileName).use {
                val bytecode = it.readBytes()
                val methodCollector = BytecodeMethodCollector()
                val classReader = ClassReader(bytecode)
                classReader.accept(methodCollector,ClassReader.SKIP_CODE)

                return ExtensionClass(fileName,methodCollector.className,bytecode,methodCollector.methods)
            }
        }

        fun load(fileName:String) = load(File(fileName))
    }

    override fun toString() = "$className (${methods.size} methods)"
}