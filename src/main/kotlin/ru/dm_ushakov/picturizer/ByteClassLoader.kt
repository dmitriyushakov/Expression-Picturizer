package ru.dm_ushakov.picturizer

class ByteClassLoader(name:String,parent:ClassLoader):ClassLoader(name,parent) {
    fun getClassFromBytecode(name:String,buf:ByteArray):Class<*> {
        return defineClass(name,buf,0,buf.size)
    }
}