package ru.dm_ushakov.picturizer.cg

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import ru.dm_ushakov.picturizer.model.vectortree.*
import ru.dm_ushakov.picturizer.model.vectortree.BinaryBooleanVectorOperation.*
import ru.dm_ushakov.picturizer.model.vectortree.BinaryCompareVectorOperation.*
import ru.dm_ushakov.picturizer.model.vectortree.BinaryVectorOperation.*
import ru.dm_ushakov.picturizer.utils.compilationError

class TreeBasedCG(val operand: VectorOperand):(MethodVariableContext, MethodVisitor) -> Unit {
    fun visit(context: MethodVariableContext, visitor: MethodVisitor) = visit(context,visitor,operand)
    fun visit(context: MethodVariableContext, visitor: MethodVisitor, operand: VectorOperand) {
        with(visitor) {
            when (operand) {
                is ScalarValue -> {
                    visitLdcInsn(operand.value)
                }
                is ScalarBooleanVector -> {
                    visitLdcInsn(operand.value)
                }
                is MethodVariableAccess -> {
                    visitVarInsn(DLOAD,context[operand.type].slot)
                }
                is MethodInvoke -> {
                    operand.operands.forEach { op ->
                        visit(context,visitor,op)
                    }
                    visitMethodInsn(INVOKESTATIC, operand.owner, operand.name, operand.descriptor, false)
                }
                is TernaryVectorOperator -> {
                    val trueBranchLbl = Label()
                    val finalBranchLbl = Label()
                    visit(context,visitor,operand.condition)
                    visitJumpInsn(IFNE,trueBranchLbl)
                    visit(context, visitor,operand.falseExpression)
                    visitJumpInsn(GOTO,finalBranchLbl)
                    visitLabel(trueBranchLbl)
                    visit(context,visitor,operand.trueExpression)
                    visitLabel(finalBranchLbl)
                }
                is BinaryBooleanVectorOperator -> {
                    when(operand.operation) {
                        And -> {
                            val falseBranchLbl = Label()
                            val finalBranchLbl = Label()
                            visit(context,visitor,operand.leftOperand)
                            visitJumpInsn(IFEQ,falseBranchLbl)
                            visit(context,visitor,operand.rightOperand)
                            visitJumpInsn(IFEQ,falseBranchLbl)
                            visitInsn(ICONST_1)
                            visitJumpInsn(GOTO,finalBranchLbl)
                            visitLabel(falseBranchLbl)
                            visitInsn(ICONST_0)
                            visitLabel(finalBranchLbl)
                        }
                        Or -> {
                            val trueBranchLbl = Label()
                            val finalBranchLbl = Label()
                            visit(context,visitor,operand.leftOperand)
                            visitJumpInsn(IFNE,trueBranchLbl)
                            visit(context,visitor,operand.rightOperand)
                            visitJumpInsn(IFNE,trueBranchLbl)
                            visitInsn(ICONST_0)
                            visitJumpInsn(GOTO,finalBranchLbl)
                            visitLabel(trueBranchLbl)
                            visitInsn(ICONST_1)
                            visitLabel(finalBranchLbl)
                        }
                    }
                }
                is BinaryCompareVectorOperator -> {
                    val trueBranchLbl = Label()
                    val finalBranchLbl = Label()
                    visit(context, visitor, operand.leftOperand)
                    visit(context, visitor, operand.rightOperand)
                    visitInsn(DCMPL)
                    val cmpOp = when(operand.operation) {
                        Equal -> IFEQ
                        NotEqual -> IFNE
                        GreaterThan -> IFGT
                        GreaterOrEqual -> IFGE
                        LessThan -> IFLT
                        LessThanOrEqual -> IFLE
                    }
                    visitJumpInsn(cmpOp,trueBranchLbl)
                    visitInsn(ICONST_0)
                    visitJumpInsn(GOTO,finalBranchLbl)
                    visitLabel(trueBranchLbl)
                    visitInsn(ICONST_1)
                    visitLabel(finalBranchLbl)
                }
                is BinaryVectorOperator -> {
                    visit(context, visitor, operand.leftOperand)
                    visit(context, visitor, operand.rightOperand)
                    val operation = when(operand.operation) {
                        Add -> DADD
                        Sub -> DSUB
                        Mul -> DMUL
                        Div -> DDIV
                    }
                    visitInsn(operation)
                }
                is IdentityOperator -> {
                    visit(context, visitor, operand.operand)
                }
                else -> compilationError("Unexpected operation in operation tree!")
            }
        }
    }

    override fun invoke(context: MethodVariableContext, visitor: MethodVisitor) = visit(context,visitor)
}