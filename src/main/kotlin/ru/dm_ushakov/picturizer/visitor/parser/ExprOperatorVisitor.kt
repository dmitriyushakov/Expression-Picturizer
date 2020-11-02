package ru.dm_ushakov.picturizer.visitor.parser

import ru.dm_ushakov.picturizer.antlr.ExprBaseVisitor
import ru.dm_ushakov.picturizer.antlr.ExprParser
import ru.dm_ushakov.picturizer.model.vectortree.*
import ru.dm_ushakov.picturizer.utils.compilationError

class ExprOperatorVisitor:ExprBaseVisitor<VectorOperand>() {
    override fun visitNumber(ctx: ExprParser.NumberContext) = VectorValue(ctx.NUM().text.toDouble())

    override fun visitBoolNot(ctx: ExprParser.BoolNotContext): VectorOperand {
        return ctx.expr().let {
            super.visit(it)
        }.let {
            when (it) {
                is VectorBooleanValue -> VectorBooleanValue(!it.red,!it.green,!it.blue)
                is ScalarBooleanVector -> ScalarBooleanVector(!it.value)
                is BinaryCompareVectorOperator -> it.not
                is BinaryBooleanVectorOperator -> it.not
                is BinaryVectorOperator -> BinaryVectorOperator(BinaryVectorOperation.Sub,VectorValue(1.0),it)
                is VectorValue -> VectorValue(1.0 - it.red,1.0 - it.green,1.0 - it.blue)
                is ScalarValue -> ScalarValue(1.0 - it.value)
                else -> compilationError("Operand \"${it::class.simpleName}\" couldn't be mapped by not operation!")
            }
        }
    }

    override fun visitPlusMinus(ctx: ExprParser.PlusMinusContext) = when (ctx.op.type) {
        ExprParser.PLUS -> BinaryVectorOperator(BinaryVectorOperation.Add,visit(ctx.expr(0)),visit(ctx.expr(1)))
        ExprParser.MINUS -> BinaryVectorOperator(BinaryVectorOperation.Sub,visit(ctx.expr(0)),visit(ctx.expr(1)))
        else -> compilationError("Token \"${ctx.op.text}\" can't be mapped to any add or sub operation!")
    }

    override fun visitComparation(ctx: ExprParser.ComparationContext) =
        BinaryCompareVectorOperator(
            BinaryCompareVectorOperation.fromId(ctx.op.type),
            visit(ctx.expr(0)),
            visit(ctx.expr(1)))

    override fun visitVariable(ctx: ExprParser.VariableContext) = VectorVariableAccess(ctx.text)

    override fun visitNegativeNumber(ctx: ExprParser.NegativeNumberContext) =
        ctx.NUM().text.toDouble().let { VectorValue(it) }

    override fun visitBoolOperation(ctx: ExprParser.BoolOperationContext) = when (ctx.op.type) {
        ExprParser.BOOL_AND -> BinaryBooleanVectorOperator(BinaryBooleanVectorOperation.And,visit(ctx.expr(0)),visit(ctx.expr(1)))
        ExprParser.BOOL_OR  -> BinaryBooleanVectorOperator(BinaryBooleanVectorOperation.Or ,visit(ctx.expr(0)),visit(ctx.expr(1)))
        else -> compilationError("Token \"${ctx.op.text}\" can't be mapped to any binary boolean operation!")
    }

    override fun visitParenthesis(ctx: ExprParser.ParenthesisContext) = visit(ctx.expr())
    override fun visitTernary(ctx: ExprParser.TernaryContext) =
        TernaryVectorOperator(visit(ctx.expr(0)),visit(ctx.expr(1)),visit(ctx.expr(2)))

    override fun visitMulDiv(ctx: ExprParser.MulDivContext) = when (ctx.op.type) {
        ExprParser.MUL -> BinaryVectorOperator(BinaryVectorOperation.Mul,visit(ctx.expr(0)),visit(ctx.expr(1)))
        ExprParser.DIV -> BinaryVectorOperator(BinaryVectorOperation.Div,visit(ctx.expr(0)),visit(ctx.expr(1)))
        else -> compilationError("Token \"${ctx.op.text}\" can't be mapped to any mul or div operation!")
    }

    override fun visitFuncCall(ctx: ExprParser.FuncCallContext): VectorOperand {
        val functionName = ctx.ID().text as String
        val arguments = ctx.expr().map { visit(it) as VectorOperand }
        return VectorFunctionCall(functionName,arguments)
    }
}