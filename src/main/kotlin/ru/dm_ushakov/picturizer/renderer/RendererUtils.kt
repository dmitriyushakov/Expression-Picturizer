@file:JvmName("RendererUtils")
package ru.dm_ushakov.picturizer.renderer

import kotlin.math.max
import kotlin.math.min

private fun clamp(value:Int,minValue:Int,maxValue:Int) = max(minValue,min(maxValue,value))
fun getRgba(red:Int,green:Int,blue:Int) = 0xf000000 or
        (clamp(red,0,255) shl 16) or
        (clamp(green,0,255) shl 8) or
        (clamp(blue,0,255))