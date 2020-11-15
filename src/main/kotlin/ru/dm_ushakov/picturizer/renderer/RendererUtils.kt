@file:JvmName("RendererUtils")
package ru.dm_ushakov.picturizer.renderer

import kotlin.math.sqrt
import kotlin.math.atan2

private fun clamp(value:Int, minValue:Int, maxValue:Int) = when {
    value < minValue -> minValue
    value > maxValue -> maxValue
    else -> value
}

fun getRgba(red:Int,green:Int,blue:Int) = 0xf000000 or
        (clamp(red,0,255) shl 16) or
        (clamp(green,0,255) shl 8) or
        (clamp(blue,0,255))

fun getRadius(x:Int,y:Int):Double {
    val xd = x.toDouble()
    val yd = y.toDouble()
    return sqrt(xd * xd + yd * yd)
}

fun getAngle(x:Int,y:Int) = atan2(x.toDouble(),y.toDouble())