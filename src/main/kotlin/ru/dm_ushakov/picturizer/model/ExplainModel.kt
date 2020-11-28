package ru.dm_ushakov.picturizer.model

import ru.dm_ushakov.picturizer.model.vectortree.VectorOperand

class ExplainModel (
    val expression:String,
    val className:String,
    val originalTree: VectorOperand,
    val redTree: VectorOperand,
    val greenTree: VectorOperand,
    val blueTree: VectorOperand,
    val classBytecode: ByteArray
)