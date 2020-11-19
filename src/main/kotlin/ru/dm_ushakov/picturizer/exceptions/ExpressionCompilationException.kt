package ru.dm_ushakov.picturizer.exceptions

class ExpressionCompilationException:Exception {
    constructor(message: String):super(message)
    constructor(message: String,throwable: Throwable):super(message, throwable)
    val friendlyMessage:String get() = "Compilation error:\n$message"
}