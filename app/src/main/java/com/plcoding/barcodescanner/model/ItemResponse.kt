package com.plcoding.barcodescanner.model

data class ItemResponse(
    val box: String? = null,
    val category: String,
    val image: String,
    val name: String,
    val size : String?,
)
class SkuItemsResponse : ArrayList<ItemResponse>()