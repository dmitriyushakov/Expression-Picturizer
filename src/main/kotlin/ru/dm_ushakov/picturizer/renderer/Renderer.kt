package ru.dm_ushakov.picturizer.renderer

interface Renderer {
    fun render(argb:IntArray,width:Int,height:Int,timeDelta:Long)
    val isSupportTime:Boolean
}