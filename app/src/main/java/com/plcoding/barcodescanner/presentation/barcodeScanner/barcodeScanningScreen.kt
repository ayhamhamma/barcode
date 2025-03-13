package com.plcoding.barcodescanner.presentation.barcodeScanner

import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.plcoding.barcodescanner.presentation.MainActivity
import com.plcoding.barcodescanner.utils.Constants.INVENTORY_SCREEN
import java.util.concurrent.Executors

@Composable
fun BarcodeScannerDemo(
    navController: NavController,
    scannedCode: String,
    scannedText: String,
    isButtonEnabled: Boolean,
    onScannedCodeUpdate: (String) -> Unit,
    onScannedTextUpdate: (String) -> Unit
) {

    val length = "sh2305263686018639".length
    val context = LocalContext.current
    Box {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BarcodeScannerScreen({ code ->
                onScannedCodeUpdate(code)
            }, {

                try {
                    val startsWithS = it.startsWith("s",ignoreCase = true)
                    val matchesLength = (it.length == length)
                    val lastCharIsNumbers = it.takeLast(length - 2).all { it.isDigit() }
                    val firstTwoCharactersIsLetters = it.take(2).all { it.isLetter() }

                    if (startsWithS && matchesLength && lastCharIsNumbers && firstTwoCharactersIsLetters)
                        onScannedTextUpdate(it.toLowerCase())
                }catch (e: Exception){
                    Log.e("ayham",e.message.toString())
                }

                Log.e("ayham",it)


            })



            LaunchedEffect(key1 = scannedCode) {
                Log.e("AyhamCode", scannedCode)
            }
            LaunchedEffect(key1 = scannedText) {
                Log.e("AyhamText", scannedText)
            }

            Text("Scanned Code: $scannedCode", fontSize = 20.sp)
        }

        Column(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp))
                .background(Color.White)
        ) {

            Spacer(Modifier.size(20.dp))
            Text(
                "Scanned SKU:",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp),
                color = Color.Black
            )

            Text(
                scannedText,
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp),
                color = Color.Red
            )

            Spacer(Modifier.size(20.dp))
            Text(
                "Scanned Barcode:",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp),
                color = Color.Black
            )
            Text(
                scannedCode,
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp),
                color = Color.Red
            )

            Spacer(Modifier.size(20.dp))



            Button(
                onClick = {
                    if (scannedCode.isNotEmpty()) {
                        navController.popBackStack(INVENTORY_SCREEN, inclusive = true)
                        navController.navigate(INVENTORY_SCREEN + "/${scannedCode}"+"/${scannedText}")
                    } else {
                        // show error Toast
                        Toast.makeText(context, "Please scan a barcode first", Toast.LENGTH_SHORT).show()

                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isButtonEnabled) Color.Green else Color.Red
                )
            ) {
                Text("Confirm", color = Color.White)
            }

            Spacer(Modifier.size(80.dp))

        }
    }

}

@Composable
fun BarcodeScannerScreen(onBarcodeScanned: (String) -> Unit, onTextFound: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(factory = { previewView }) { view ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview =
                Preview.Builder().build().also { it.setSurfaceProvider(view.surfaceProvider) }
            val barcodeScanner = BarcodeScanning.getClient()
            val textRecognizerOptions = TextRecognizerOptions.Builder().build()
            val textRecognizer = TextRecognition.getClient(textRecognizerOptions)
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(
                            barcodeScanner,
                            textRecognizer,
                            imageProxy,
                            onBarcodeScanned,
                            onTextFound
                        )
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e("BarcodeScanner", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}


@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    barcodeScanner: BarcodeScanner,
    textRecognizer: TextRecognizer,
    imageProxy: ImageProxy,
    onBarcodeScanned: (String) -> Unit,
    onTextFound: (String) -> Unit
) {
    // Convert ImageProxy to InputImage
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        // Process barcode scanning
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.displayValue?.let { onBarcodeScanned(it) }
                }
            }
            .addOnFailureListener { e ->
                Log.e("BarcodeScanner", "Barcode processing failed", e)
            }

        // Process text recognition
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                for (block in visionText.textBlocks) {
                    for (line in block.lines) {
                        line.text?.let { text ->
                            if (text.startsWith("S", ignoreCase = true)) {
                                onTextFound(text)
                                return@addOnSuccessListener // Exit after finding the first match
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("BarcodeScanner", "Text recognition failed", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}