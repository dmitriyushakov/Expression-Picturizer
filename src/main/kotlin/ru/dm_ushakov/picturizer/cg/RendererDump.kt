package ru.dm_ushakov.picturizer.cg

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*


object RendererDump {
    private const val RED_VAL_POS = 7
    private const val GREEN_VAL_POS = 8
    private const val BLUE_VAL_POS = 9
    private const val X_POS = 6
    private const val Y_POS = 4
    private const val IMG_WIDTH = 2
    private const val IMG_HEIGHT = 3
    private const val IMG_HEIGHT_OFFSET = 5
    private const val ARGB_POINT = 10
    private const val ARGB_ARRAY = 1

    fun dump(className: String, red: (MethodVisitor) -> Unit, green: (MethodVisitor) -> Unit, blue: (MethodVisitor) -> Unit): ByteArray {
        val classWriter = ClassWriter(0).apply {
            visit(V11, ACC_PUBLIC or ACC_SUPER, className, null, "java/lang/Object", arrayOf("ru/dm_ushakov/picturizer/renderer/Renderer"))
            visitSource("formula", null)

            visitMethod(ACC_PUBLIC, "<init>", "()V", null, null).apply {
                visitCode()
                visitVarInsn(ALOAD, 0)
                visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
                visitInsn(RETURN)
                visitMaxs(1, 1)
                visitEnd()
            }

            visitMethod(ACC_PUBLIC, "render", "([III)V", null, null).apply {
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

                red(methodVisitor)
                visitLdcInsn(255.0)
                visitInsn(IMUL)
                visitInsn(D2I)
                visitVarInsn(ISTORE, RED_VAL_POS) // red = ...

                green(methodVisitor)
                visitLdcInsn(255.0)
                visitInsn(IMUL)
                visitInsn(D2I)
                visitVarInsn(ISTORE, GREEN_VAL_POS) // green = ...

                blue(methodVisitor)
                visitLdcInsn(255.0)
                visitInsn(IMUL)
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

                visitMaxs(3, 11)
                visitEnd()
            }
            visitEnd()
        }

        return classWriter.toByteArray()
    }
}