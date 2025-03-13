package com.plcoding.barcodescanner.utils

import android.content.Context

object Constants {
    var BOX_NUMBER = ""
    var BOX_SKUS_LIST = mutableListOf<String>()
    const val BASE_URL = "https://ss.simma.app/api/v1/orderbrain/" // todo change this when build

    const val INVENTORY_SCREEN = "inventory"
    const val BARCODE_SCREEN = "barcodeScanner"
}