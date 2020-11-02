package ru.dm_ushakov.picturizer.utils

import ru.dm_ushakov.picturizer.exceptions.ExpressionCompilationException

inline fun compilationError(message:String):Nothing = throw ExpressionCompilationException(message)
inline fun compilationError(message: String,throwable: Throwable):Nothing = throw ExpressionCompilationException(message, throwable)