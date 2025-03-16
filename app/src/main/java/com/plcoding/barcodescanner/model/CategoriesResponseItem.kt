package com.plcoding.barcodescanner.model

data class CategoriesResponseItem(
    val _id: String,
    val code: String,
    val name: String,
    val order: Int,
    val proposed_box : String?
)