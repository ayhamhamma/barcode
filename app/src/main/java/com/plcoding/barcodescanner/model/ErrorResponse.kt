package com.plcoding.barcodescanner.model

import android.os.Message

data class ErrorResponse(
    val error:String?,
    val message: String?,
    val summary : Summary?
)
