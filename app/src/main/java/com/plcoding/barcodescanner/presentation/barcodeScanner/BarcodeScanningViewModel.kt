package com.plcoding.barcodescanner.presentation.barcodeScanner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.plcoding.barcodescanner.utils.Constants.BOX_SKUS_LIST

class BarcodeScanningViewModel : ViewModel() {

    var scannedCode by mutableStateOf("")
        private set
    var scannedText by mutableStateOf("")
        private set

    var isButtonEnabled by mutableStateOf(false)
        private set

    fun updateScannedCode(code: String) {
        scannedCode = code
        checkIfTextIsValid()
    }

    fun updateScannedText(text: String) {
        scannedText = text
        checkIfTextIsValid()
    }

    private fun checkIfTextIsValid() {
        // Check if either scannedText or scannedCode is in the BOX_SKUS_LIST
        isButtonEnabled = BOX_SKUS_LIST.contains(scannedText) || BOX_SKUS_LIST.contains(scannedCode)
    }
}