package ru.dm_ushakov.picturizer.visitor.parser

import org.antlr.v4.runtime.*;
import ru.dm_ushakov.picturizer.antlr.ExprLexer
import ru.dm_ushakov.picturizer.antlr.ExprParser
import ru.dm_ushakov.picturizer.model.vectortree.VectorOperand

object Parser {
    private fun getTokenStream(expression:String):CommonTokenStream {
        val input = CharStreams.fromString(expression)
        val lexer = ExprLexer(input)
        val tokens = CommonTokenStream(lexer)

        return tokens
    }

    fun parseExpression(expression: String): VectorOperand {
        val tokens = getTokenStream(expression)
        val parser = ExprParser(tokens)
        val tree = parser.expr()

        val eval = ExprOperatorVisitor()
        return eval.visit(tree)
    }
}