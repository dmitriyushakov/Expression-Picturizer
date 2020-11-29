package ru.dm_ushakov.picturizer

import ru.dm_ushakov.picturizer.extensions.ExtensionsRegistry

class ByteClassLoader(parent:ClassLoader):ClassLoader(parent) {
    override fun loadClass(name: String): Class<*> {
        val bytecode = ExtensionsRegistry.extensionsList.find { it.className matchTo name }?.bytecode
        return if (bytecode != null) {
            defineClass(name,bytecode,0,bytecode.size)
        } else {
            super.loadClass(name)
        }
    }

    fun getClassFromBytecode(name:String,buf:ByteArray):Class<*> {
        return defineClass(name,buf,0,buf.size)
    }

    infix fun String.matchTo(other:String):Boolean {
        if(length != other.length) return false
        for(i in 0 until length) {
            val ch1 = this[i]
            val ch2 = other[i]

            if(!(ch1 == ch2 || (ch1 == '.' && ch2 == '/') || (ch1 == '/' && ch2 == '.'))) return false
        }

        return true
    }
}