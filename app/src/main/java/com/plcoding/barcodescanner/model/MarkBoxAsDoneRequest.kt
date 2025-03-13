package com.plcoding.barcodescanner.model

data class MarkBoxAsDoneRequest(
    val boxNumber: String,
    val confirmed : Boolean,
)