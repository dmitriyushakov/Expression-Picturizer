package ru.dm_ushakov.picturizer.gui

import ru.dm_ushakov.picturizer.exceptions.ExpressionCompilationException
import java.awt.Component
import java.io.PrintWriter
import java.io.StringWriter
import javax.swing.JOptionPane

fun tryWithErrorDialogs(parentComponent: Component, logic:() -> Unit) {
    try {
        logic()
    } catch (ex: ExpressionCompilationException) {
        JOptionPane.showMessageDialog(
            parentComponent,
            ex.friendlyMessage,
            "Compilation error",
            JOptionPane.ERROR_MESSAGE
        )
    } catch (th:Throwable) {
        val sw = StringWriter()
        th.printStackTrace(PrintWriter(sw))

        JOptionPane.showMessageDialog(
            parentComponent,
            sw.toString(),
            "Operation error",
            JOptionPane.ERROR_MESSAGE
        )

        throw th
    }
}