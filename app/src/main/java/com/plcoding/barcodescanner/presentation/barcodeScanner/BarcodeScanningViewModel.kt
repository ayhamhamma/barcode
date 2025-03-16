package com.plcoding.barcodescanner.presentation.barcodeScanner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class BarcodeScanningViewModel : ViewModel() {

    var scannedCode by mutableStateOf("")
        private set
    var scannedText by mutableStateOf("")
        private set

    var isButtonEnabled by mutableStateOf(false)
        private set

    fun updateScannedCode(code: String) {
        scannedCode = code
//        checkIfTextIsValid()
    }

    fun updateScannedText(text: String) {
        scannedText = text
//        checkIfTextIsValid()
    }


}