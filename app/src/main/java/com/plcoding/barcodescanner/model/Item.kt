package com.plcoding.barcodescanner.model

data class Item(
    val __v: Int,
    val _id: String,
    val categoryId: Int,
    val createdAt: String,
    val isBoxCompleted: Boolean,
    val isNotFound: Boolean,
    val newBoxNumber: String,
    val newSKU: String,
    val oldBoxNumber: String,
    val orderNumberMetabase: String,
    val originalSKU: String,
    val quantity: Int,
    val simmaCategory: String,
    val size: String,
    val specificCategory: String,
    val thumbnailImageLink: String,
    val updatedAt: String
)