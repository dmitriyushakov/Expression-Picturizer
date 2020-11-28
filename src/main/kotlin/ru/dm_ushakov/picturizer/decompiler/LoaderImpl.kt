package ru.dm_ushakov.picturizer.decompiler

import org.jd.core.v1.api.loader.Loader

class LoaderImpl(vararg classNameBytecodePairs:Pair<String,ByteArray>):Loader {
    private val bytecodeMap = classNameBytecodePairs.toMap()

    override fun canLoad(internalName: String) = bytecodeMap.containsKey(internalName)
    override fun load(internalName: String) = bytecodeMap[internalName]
}