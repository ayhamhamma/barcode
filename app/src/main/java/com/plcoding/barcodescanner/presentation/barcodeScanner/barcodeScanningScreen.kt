package com.plcoding.barcodescanner.presentation.barcodeScanner

import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
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
import java.util.Date
import java.util.Locale
import android.content.Context
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.text.Text
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

@Composable
fun BarcodeScannerDemo(
    navController: NavController,
    scannedCode: String,
    scannedText: String,
    onScannedCodeUpdate: (String) -> Unit,
    onScannedTextUpdate: (String) -> Unit
) {
    Box {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HybridBarcodeScannerScreen(
                onBarcodeScanned = { barcode ->
                    // Save the barcode value
                    onScannedCodeUpdate(barcode)
                },
                onTextScanned = { text ->
                    // Save the text value
                    onScannedTextUpdate(text.toLowerCase())
                }
            )



            LaunchedEffect(key1 = scannedCode) {
                Log.e("AyhamCode", scannedCode)
            }
            LaunchedEffect(key1 = scannedText) {
                Log.e("AyhamText", scannedText)
            }

            Text("Scanned Code: $scannedCode", fontSize = 20.sp)
        }

        if(scannedText.isNotEmpty())
            Box (Modifier.fillMaxWidth().align(Alignment.BottomCenter)){

                Column(
                    Modifier
                        .fillMaxWidth()
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
                                navController.popBackStack(INVENTORY_SCREEN, inclusive = true)
                                navController.navigate(INVENTORY_SCREEN + "/${scannedCode}"+"/${scannedText}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Green
                        )
                    ) {
                        Text("Confirm", color = Color.White)
                    }

                    Spacer(Modifier.size(80.dp))

                }
                // Close button
                IconButton(
                    onClick = {
                        onScannedTextUpdate("")
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Scanner",
                        tint = Color.White
                    )
                }
            }

    }

}

@Composable
fun HybridBarcodeScannerScreen(
    onBarcodeScanned: (String) -> Unit,
    onTextScanned: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Camera and analysis components
    val imageCapture = remember { ImageCapture.Builder().build() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var scannedTexts by remember { mutableStateOf<List<String>>(emptyList()) }
    var showTextSelection by remember { mutableStateOf(false) }
    var processingImage by remember { mutableStateOf(false) }

    // Disposable effect to clean up resources
    DisposableEffect(lifecycleOwner) {
        onDispose {
            cameraExecutor.shutdown()
            barcodeScanner.close()
        }
    }

    // Create a temporary file for the image
    val createTempFile = {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            absolutePath
        }
    }

    Box(Modifier.fillMaxSize()) {
        // Camera Preview with live barcode detection
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        ) { view ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Preview use case
                val preview = Preview.Builder()
                    .build()
                    .also { it.setSurfaceProvider(view.surfaceProvider) }

                // Live barcode scanning analyzer
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor) { imageProxy ->
                            processImageProxyForBarcode(
                                barcodeScanner,
                                imageProxy,
                                onBarcodeScanned
                            )
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind previous use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera lifecycle
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer,
                        imageCapture
                    )

                    Log.d("HybridScanner", "Camera set up successfully")
                } catch (exc: Exception) {
                    Log.e("HybridScanner", "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(context))
        }

        // Capture button and controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (processingImage) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Processing image...", color = Color.White)
            } else {
                IconButton(
                    onClick = {
                        processingImage = true
                        Log.d("HybridScanner", "Taking picture for text recognition")

                        try {
                            val photoFile = createTempFile()
                            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                            imageCapture.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                                        capturedImageUri = savedUri

                                        // Process the captured image for text only
                                        processImageForText(
                                            context,
                                            savedUri,
                                            onTextResult = { textBlocks ->
                                                Log.d("HybridScanner", "Text processing complete: ${textBlocks.size} text blocks found")
                                                scannedTexts = textBlocks.flatMap { block ->
                                                    block.lines.map { it.text }
                                                }
                                                showTextSelection = true
                                                processingImage = false
                                            },
                                            onError = {
                                                Log.e("HybridScanner", "Text processing failed")
                                                Toast.makeText(context, "Error processing text", Toast.LENGTH_SHORT).show()
                                                processingImage = false
                                            }
                                        )
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        Log.e("HybridScanner", "Image capture failed", exception)
                                        Toast.makeText(context, "Failed to capture image: ${exception.message}", Toast.LENGTH_SHORT).show()
                                        processingImage = false
                                    }
                                }
                            )
                        } catch (e: Exception) {
                            Log.e("HybridScanner", "Exception during image capture", e)
                            Toast.makeText(context, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
                            processingImage = false
                        }
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color.White.copy(alpha = 0.5f), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Capture Image for Text",
                        modifier = Modifier.size(36.dp),
                        tint = Color.Black
                    )
                }
            }
        }

        // Text selection dialog
        if (showTextSelection) {
            TextSelectionDialog(
                texts = scannedTexts,
                onTextSelected = { selectedText ->
                    onTextScanned(selectedText)
                    Toast.makeText(context, "Text selected: $selectedText", Toast.LENGTH_SHORT).show()
                    showTextSelection = false
                },
                onDismiss = {
                    showTextSelection = false
                }
            )
        }
    }
}

@Composable
fun TextSelectionDialog(
    texts: List<String>,
    onTextSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Text to Save") },
        text = {
            LazyColumn {
                items(texts) { text ->
                    if (text.isNotBlank()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onTextSelected(text) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = text,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                // Show message if no text found
                if (texts.isEmpty()) {
                    item {
                        Text(
                            text = "No text detected in image",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Process image proxy for barcode detection (live scanning)
@OptIn(ExperimentalGetImage::class)
private fun processImageProxyForBarcode(
    barcodeScanner: BarcodeScanner,
    imageProxy: ImageProxy,
    onBarcodeScanned: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        // Process barcode scanning
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.displayValue?.let {
                        onBarcodeScanned(it)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("HybridScanner", "Barcode processing failed", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}

// Process captured image for text recognition only
private fun processImageForText(
    context: Context,
    imageUri: Uri,
    onTextResult: (List<Text.TextBlock>) -> Unit,
    onError: () -> Unit
) {
    try {
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        }

        val image = InputImage.fromBitmap(bitmap, 0)

        // Process text recognition
        val textRecognizerOptions = TextRecognizerOptions.Builder().build()
        val textRecognizer = TextRecognition.getClient(textRecognizerOptions)
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                onTextResult(visionText.textBlocks)
            }
            .addOnFailureListener {
                Log.e("HybridScanner", "Text recognition failed", it)
                onError()
            }
    } catch (e: Exception) {
        Log.e("HybridScanner", "Image processing failed", e)
        onError()
    }
}