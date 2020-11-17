package ru.dm_ushakov.picturizer.cg

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import ru.dm_ushakov.picturizer.model.vectortree.MethodVariableAccess
import ru.dm_ushakov.picturizer.model.vectortree.MethodVariableType
import ru.dm_ushakov.picturizer.model.vectortree.VectorOperand
import ru.dm_ushakov.picturizer.model.vectortree.VectorOperator


object RendererDump {
    private const val RED_VAL_POS = 9
    private const val GREEN_VAL_POS = 10
    private const val BLUE_VAL_POS = 11
    private const val X_POS = 8
    private const val Y_POS = 6
    private const val IMG_WIDTH = 2
    private const val IMG_HEIGHT = 3
    private const val TIME_SPENT = 4
    private const val IMG_HEIGHT_OFFSET = 7
    private const val ARGB_POINT = 12
    private const val ARGB_ARRAY = 1
    private const val VAR_CONTEXT_OFFSET = 13

    fun dump(className: String, redTree: VectorOperand, greenTree: VectorOperand, blueTree: VectorOperand): ByteArray {
        val requiredMethodVariables = listOf(redTree,greenTree,blueTree).getRequiredMethodVariables()
        val fillContext: (MethodVariableContext) -> Unit = { ctx -> requiredMethodVariables.forEach { v -> ctx.addVariable(v) } }
        return dump(className,fillContext,TreeBasedCG(redTree),TreeBasedCG(greenTree),TreeBasedCG(blueTree))
    }

    fun dump(className: String, fillContext:(MethodVariableContext) -> Unit, red: (MethodVariableContext,MethodVisitor) -> Unit, green: (MethodVariableContext,MethodVisitor) -> Unit, blue: (MethodVariableContext,MethodVisitor) -> Unit): ByteArray {
        val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS).apply {
            visit(V11, ACC_PUBLIC or ACC_SUPER, className, null, "java/lang/Object", arrayOf("ru/dm_ushakov/picturizer/renderer/Renderer"))
            visitSource("formula", null)

            val variableContext = MethodVariableContext(VAR_CONTEXT_OFFSET)
            fillContext(variableContext)

            visitMethod(ACC_PUBLIC, "<init>", "()V", null, null).apply {
                visitCode()
                visitVarInsn(ALOAD, 0)
                visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
                visitInsn(RETURN)
                visitMaxs(0,0)
                visitEnd()
            }

            visitMethod(ACC_PUBLIC, "render", "([IIIJ)V", null, null).apply {
                val methodVisitor = this

                visitAnnotableParameterCount(3, false)
                visitParameterAnnotation(0, "Lorg/jetbrains/annotations/NotNull;", false).visitEnd()
                visitCode()

                visitInsn(ICONST_0)
                visitVarInsn(ISTORE, Y_POS) // y = 0
                val yCycle = Label()
                visitLabel(yCycle)
                visitFrame(F_APPEND, 1, arrayOf<Any>(INTEGER), 0, null)
                visitVarInsn(ILOAD, Y_POS)
                visitVarInsn(ILOAD, IMG_HEIGHT)
                val endYCycle = Label()
                visitJumpInsn(IF_ICMPGE, endYCycle) // if y >= height

                visitVarInsn(ILOAD, IMG_WIDTH)
                visitVarInsn(ILOAD, Y_POS)
                visitInsn(IMUL) // heightOffset = width * y
                visitVarInsn(ISTORE, IMG_HEIGHT_OFFSET)

                visitInsn(ICONST_0) // x = 0
                visitVarInsn(ISTORE, X_POS)
                val xCycle = Label()
                visitLabel(xCycle)
                visitFrame(F_APPEND, 2, arrayOf<Any>(INTEGER, INTEGER), 0, null)
                visitVarInsn(ILOAD, X_POS)
                visitVarInsn(ILOAD, IMG_WIDTH)
                val endXCycle = Label()
                visitJumpInsn(IF_ICMPGE, endXCycle) // if x >= width

                variableContext.variables.forEach { methodVar ->
                    when(methodVar.type) {
                        MethodVariableType.X -> {
                            visitVarInsn(ILOAD, X_POS)
                            visitInsn(I2D)
                            visitVarInsn(DSTORE,methodVar.slot)
                        }
                        MethodVariableType.Y -> {
                            visitVarInsn(ILOAD, Y_POS)
                            visitInsn(I2D)
                            visitVarInsn(DSTORE,methodVar.slot)
                        }
                        MethodVariableType.Radius -> {
                            visitVarInsn(ILOAD, X_POS)
                            visitVarInsn(ILOAD, IMG_WIDTH)
                            visitInsn(ICONST_2)
                            visitInsn(IDIV)
                            visitInsn(ISUB)
                            visitVarInsn(ILOAD, Y_POS)
                            visitVarInsn(ILOAD, IMG_HEIGHT)
                            visitInsn(ICONST_2)
                            visitInsn(IDIV)
                            visitInsn(ISUB)
                            visitMethodInsn(INVOKESTATIC, "ru/dm_ushakov/picturizer/renderer/RendererUtils", "getRadius", "(II)D", false)
                            visitVarInsn(DSTORE,methodVar.slot)
                        }
                        MethodVariableType.Angle -> {
                            visitVarInsn(ILOAD, X_POS)
                            visitVarInsn(ILOAD, IMG_WIDTH)
                            visitInsn(ICONST_2)
                            visitInsn(IDIV)
                            visitInsn(ISUB)
                            visitVarInsn(ILOAD, Y_POS)
                            visitVarInsn(ILOAD, IMG_HEIGHT)
                            visitInsn(ICONST_2)
                            visitInsn(IDIV)
                            visitInsn(ISUB)
                            visitMethodInsn(INVOKESTATIC, "ru/dm_ushakov/picturizer/renderer/RendererUtils", "getAngle", "(II)D", false)
                            visitVarInsn(DSTORE,methodVar.slot)
                        }
                        MethodVariableType.Width -> {
                            visitVarInsn(ILOAD, IMG_WIDTH)
                            visitInsn(I2D)
                            visitVarInsn(DSTORE,methodVar.slot)
                        }
                        MethodVariableType.Height -> {
                            visitVarInsn(ILOAD, IMG_HEIGHT)
                            visitInsn(I2D)
                            visitVarInsn(DSTORE,methodVar.slot)
                        }
                        MethodVariableType.Time -> {
                            visitVarInsn(LLOAD, TIME_SPENT)
                            visitInsn(L2D)
                            visitLdcInsn(1000.0)
                            visitInsn(DDIV)
                            visitVarInsn(DSTORE,methodVar.slot)
                        }
                    }
                }

                red(variableContext,methodVisitor)
                visitLdcInsn(255.0)
                visitInsn(DMUL)
                visitInsn(D2I)
                visitVarInsn(ISTORE, RED_VAL_POS) // red = ...

                green(variableContext,methodVisitor)
                visitLdcInsn(255.0)
                visitInsn(DMUL)
                visitInsn(D2I)
                visitVarInsn(ISTORE, GREEN_VAL_POS) // green = ...

                blue(variableContext,methodVisitor)
                visitLdcInsn(255.0)
                visitInsn(DMUL)
                visitInsn(D2I)
                visitVarInsn(ISTORE, BLUE_VAL_POS) // blue = ...

                visitVarInsn(ILOAD, RED_VAL_POS)
                visitVarInsn(ILOAD, GREEN_VAL_POS)
                visitVarInsn(ILOAD, BLUE_VAL_POS)
                visitMethodInsn(INVOKESTATIC, "ru/dm_ushakov/picturizer/renderer/RendererUtils", "getRgba", "(III)I", false)
                visitVarInsn(ISTORE, ARGB_POINT)

                visitVarInsn(ALOAD, ARGB_ARRAY)
                visitVarInsn(ILOAD, IMG_HEIGHT_OFFSET)
                visitVarInsn(ILOAD, X_POS)
                visitInsn(IADD)
                visitVarInsn(ILOAD, ARGB_POINT)
                visitInsn(IASTORE) // argb[heightOffset + x] = argbPoint

                visitIincInsn(X_POS, 1) // x++
                visitJumpInsn(GOTO, xCycle)
                visitLabel(endXCycle)
                visitFrame(F_CHOP, 2, null, 0, null)
                visitIincInsn(Y_POS, 1) // y++
                visitJumpInsn(GOTO, yCycle)
                visitLabel(endYCycle)
                visitFrame(F_CHOP, 1, null, 0, null)
                visitInsn(RETURN)

                visitMaxs(0,0)
                visitEnd()
            }

            visitMethod(ACC_PUBLIC, "isSupportTime", "()Z", null, null).apply {
                visitCode()
                val timeSupport = variableContext.variables.any { it.type == MethodVariableType.Time }
                visitInsn(if(timeSupport) ICONST_1 else ICONST_0)
                visitInsn(IRETURN)

                visitMaxs(1,0)
                visitEnd()
            }

            visitEnd()
        }

        return classWriter.toByteArray()
    }

    fun VectorOperand.getRequiredMethodVariables():List<MethodVariableType> {
        val result = mutableListOf<MethodVariableType>()
        var operands = listOf(this)

        while(operands.isNotEmpty()) {
            operands.mapNotNull { it as? MethodVariableAccess }.map { it.type }.forEach {
                if(it !in result) result.add(it)
            }
            operands = operands.mapNotNull { it as? VectorOperator }.flatMap { it.operands }
        }

        return result
    }

    fun List<VectorOperand>.getRequiredMethodVariables() =
            flatMap { it.getRequiredMethodVariables() }.distinct()
}