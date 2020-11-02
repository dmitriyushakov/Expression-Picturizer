package ru.dm_ushakov.picturizer.model.vectortree

import ru.dm_ushakov.picturizer.antlr.ExprParser

enum class BinaryCompareVectorOperation {
    Equal,
    NotEqual,
    GreaterThan,
    GreaterOrEqual,
    LessThan,
    LessThanOrEqual;

    val not:BinaryCompareVectorOperation get() = when(this) {
        Equal -> NotEqual
        NotEqual -> Equal
        GreaterThan -> LessThanOrEqual
        GreaterOrEqual -> LessThan
        LessThan -> GreaterOrEqual
        LessThanOrEqual -> GreaterThan
    }

    companion object {
        fun fromId(id: Int) = when (id) {
            ExprParser.COMP_EQ -> Equal
            ExprParser.COMP_NEQ -> NotEqual
            ExprParser.COMP_GT -> GreaterThan
            ExprParser.COMP_GTE -> GreaterOrEqual
            ExprParser.COMP_LT -> LessThan
            ExprParser.COMP_LTE -> LessThanOrEqual
            else -> error("Parser identifier \"$id\" not match to any compare operator!")
        }
    }
}