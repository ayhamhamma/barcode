package com.plcoding.barcodescanner.remote

import com.plcoding.barcodescanner.model.BoxItemsResponse
import com.plcoding.barcodescanner.model.CategoriesResponse
import com.plcoding.barcodescanner.model.ErrorResponse
import com.plcoding.barcodescanner.model.ItemResponse
import com.plcoding.barcodescanner.model.MarkBoxAsDoneRequest
import com.plcoding.barcodescanner.model.SortRequest
import com.plcoding.barcodescanner.model.SortResponse
import com.plcoding.barcodescanner.utils.Resource
import com.plcoding.barcodescanner.utils.getErrorFromErrorBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object Repository {

    fun getBoxItems(
        boxNumber: String
    ): Flow<Resource<BoxItemsResponse>> {
        return flow {
            emit(Resource.Loading(true))

            val response = RetrofitInstance.api.getBoxItems(boxNumber)

            if(response.isSuccessful) {
                emit(Resource.Success(response.body()!!))
            }else {
                val errorBody = response.errorBody()?.string() ?: """
                    {
                        "message": "Unknown error"
                    }
                """.trimIndent()


                emit(Resource.Error(getErrorFromErrorBody(errorBody)))
            }

            emit(Resource.Loading(false))

        }
    }

    fun getItem(
        sku: String
    ): Flow<Resource<ItemResponse>> {
        return flow {
            emit(Resource.Loading(true))

            val response = RetrofitInstance.api.getItemBySKU(sku)

            if(response.isSuccessful) {
                emit(Resource.Success(response.body()!!))
            } else {
                val errorBody = response.errorBody()?.string() ?: """
                    {
                        "message": "Unknown error"
                    }
                """.trimIndent()


                emit(Resource.Error(getErrorFromErrorBody(errorBody)))
            }

            emit(Resource.Loading(false))

        }
    }

    fun sortItem(
        sortRequest: SortRequest
    ): Flow<Resource<SortResponse>>{
        return flow {
            emit(Resource.Loading(true))

            val response = RetrofitInstance.api.sortItems(sortRequest)

            if(response.isSuccessful) {
                emit(Resource.Success(response.body()!!))
            } else {
                val errorBody = response.errorBody()?.string() ?: """
                    {
                        "message": "Unknown error"
                    }
                """.trimIndent()



                emit(Resource.Error(getErrorFromErrorBody(errorBody)))
            }

            emit(Resource.Loading(false))

        }
    }

    fun markBoxAsDone(body : MarkBoxAsDoneRequest): Flow<Resource<ErrorResponse>>{
        return flow {
            emit(Resource.Loading(true))

            val response = RetrofitInstance.api.markBoxAsDone(body)

            if(response.isSuccessful) {
                emit(Resource.Success(response.body()!!))
                } else {
                val errorBody = response.errorBody()?.string() ?: """
                    {
                        "message": "Unknown error"
                    }
                """.trimIndent()


                emit(Resource.Error(getErrorFromErrorBody(errorBody)))
            }

            emit(Resource.Loading(false))
        }
    }

    fun getAllCategories(): Flow<Resource<CategoriesResponse>> {
        return flow {
            emit(Resource.Loading(true))
            val response = RetrofitInstance.api.getAllCategories()
            if(response.isSuccessful) {
                emit(Resource.Success(response.body()!!))
            }
            else {
                val errorBody = response.errorBody()?.string() ?: """
                    {
                        "message": "Unknown error"
                    }
                    """.trimIndent()
                emit(Resource.Error(getErrorFromErrorBody(errorBody)))
            }
            emit(Resource.Loading(false))
        }
    }
}