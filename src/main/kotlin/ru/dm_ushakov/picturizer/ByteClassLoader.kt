package ru.dm_ushakov.picturizer

class ByteClassLoader(parent:ClassLoader):ClassLoader(parent) {
    fun getClassFromBytecode(name:String,buf:ByteArray):Class<*> {
        return defineClass(name,buf,0,buf.size)
    }
}