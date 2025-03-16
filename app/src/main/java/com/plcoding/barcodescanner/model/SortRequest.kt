package com.plcoding.barcodescanner.model

data class SortRequest(
    val itemBarcode: String,
    val itemCategory: String,
    val itemSKU: String,
    val sortedBox: String,
    val status : Int,
    val size : String? = null,
    val name : String
)