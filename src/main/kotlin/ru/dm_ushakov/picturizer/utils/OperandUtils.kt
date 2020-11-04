package ru.dm_ushakov.picturizer.utils

import ru.dm_ushakov.picturizer.model.vectortree.*

fun VectorOperand.isEqualTo(number:Double):Boolean {
    if (this is VectorValue) {
        return this.red == number && this.green == number && this.blue == number
    } else if (this is ScalarValue) {
        return this.value == number
    }
    return false
}

val VectorOperand.isZero:Boolean get() = isEqualTo(0.0)
val VectorOperand.isOne:Boolean get() = isEqualTo(1.0)

val ScalarValue.negative:ScalarValue get() = ScalarValue(-value)

operator fun ((VectorOperator) -> VectorOperand).invoke(op:VectorOperand):VectorOperand =
    if (op is VectorOperator) invoke(op) else invoke(op.identity).identityValue

fun BinaryVectorOperator.collectOperands():List<VectorOperand> {
    val operandsList = mutableListOf<VectorOperand>()
    collectOperands(operation,this,operandsList)
    return operandsList
}

private fun collectOperands(operation: BinaryVectorOperation, op: BinaryVectorOperator, operandsList:MutableList<VectorOperand>) {
    if (op.operation == operation) {
        for(child in op.operands) {
            if (child is BinaryVectorOperator) collectOperands(operation, child, operandsList)
            else operandsList.add(child)
        }
    } else operandsList.add(op)
}

val VectorOperand.identity get() = IdentityOperator(this)
val VectorOperand.identityValue:VectorOperand get() = if (this is IdentityOperator) operand.identityValue else this