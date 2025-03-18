package com.plcoding.barcodescanner.remote

import com.plcoding.barcodescanner.model.BoxItemsResponse
import com.plcoding.barcodescanner.model.CategoriesResponse
import com.plcoding.barcodescanner.model.ErrorResponse
import com.plcoding.barcodescanner.model.ItemResponse
import com.plcoding.barcodescanner.model.MarkBoxAsDoneRequest
import com.plcoding.barcodescanner.model.SkuItemsResponse
import com.plcoding.barcodescanner.model.SortRequest
import com.plcoding.barcodescanner.model.SortResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiServices {

    @GET("box/items")
    suspend fun getBoxItems(@Query("boxNumber") boxNumber: String): Response<BoxItemsResponse>

    @GET("item")
    suspend fun getItemBySKU(@Query("sku") sku: String): Response<SkuItemsResponse>

    @POST("item/sort")
    suspend fun sortItems(@Body sortRequest: SortRequest): Response<SortResponse>

    @PUT("box/complete")
    suspend fun markBoxAsDone(@Body body : MarkBoxAsDoneRequest): Response<ErrorResponse>

    @GET("categories")
    suspend fun getAllCategories(
        @Query("teamNumber") teamNumber:String
    ): Response<CategoriesResponse>

}