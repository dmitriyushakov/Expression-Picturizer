package ru.dm_ushakov.picturizer.utils

fun countArgsInDescriptor(desc:String):Int {
    var isStart = true
    var argumentsCount = 0
    for (ch in desc) {
        if (isStart) {
            if (ch == '(') {
                isStart = false
            } else {
                error("Unexpected token in method descriptor - \"$ch\"!")
            }
        } else {
            if (ch == 'D') {
                argumentsCount ++
            } else if (ch == ')') {
                break
            } else {
                error("It is not as double or end of arguments in method descriptor!")
            }
        }
    }

    return argumentsCount
}