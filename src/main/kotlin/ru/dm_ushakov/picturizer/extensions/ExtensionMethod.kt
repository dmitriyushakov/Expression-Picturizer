package ru.dm_ushakov.picturizer.extensions

class ExtensionMethod (
    val owner:String,
    val name:String,
    val descriptor:String
) {
    override fun toString() = "$owner; $name; $descriptor"
}