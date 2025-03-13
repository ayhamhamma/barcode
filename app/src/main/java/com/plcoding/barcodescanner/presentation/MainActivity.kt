package com.plcoding.barcodescanner.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.plcoding.barcodescanner.presentation.barcodeScanner.BarcodeScannerDemo
import com.plcoding.barcodescanner.presentation.barcodeScanner.BarcodeScanningViewModel
import com.plcoding.barcodescanner.presentation.inventoryScreen.InventoryTransferScreen
import com.plcoding.barcodescanner.presentation.inventoryScreen.InventoryViewModel
import com.plcoding.barcodescanner.ui.theme.BarcodeScannerTheme
import com.plcoding.barcodescanner.utils.Constants.BARCODE_SCREEN
import com.plcoding.barcodescanner.utils.Constants.INVENTORY_SCREEN

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            BarcodeScannerTheme {

                LaunchedEffect(true) {

                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.CAMERA),
                            1000
                        )
                    }
                }

                // Setup Navigation
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = BARCODE_SCREEN
                ) {

                    composable(
                        route = INVENTORY_SCREEN + "/{ScannedCode}" + "/{ScannedText}",
                        arguments = listOf(
                            navArgument("ScannedCode") {
                                type = NavType.StringType
                                defaultValue = ""
                                nullable = true
                            },
                            navArgument("ScannedText") {
                                type = NavType.StringType
                                defaultValue = ""
                                nullable = true
                            }
                        )
                    ) {
                        val scannedCode = it.arguments?.getString("ScannedCode") ?: ""
                        val scannedSKU = it.arguments?.getString("ScannedText") ?: ""
                        val viewModel: InventoryViewModel = viewModel()

                        LaunchedEffect(true) {
                            viewModel.updateItemSKUAndBarcode(scannedSKU, scannedCode)
                        }



                        InventoryTransferScreen(
                            navController = navController,
                            oldBoxNumber = viewModel.oldBoxNumber,
                            skuValue = viewModel.skuValue,
                            itemName = viewModel.itemName,
                            categoryName = viewModel.categoryName,
                            newBoxNumber = viewModel.newBoxNumber,
                            isSkuFound = viewModel.isSkuFound,
                            isSkuExpanded = viewModel.isSkuDropdownExpanded,
                            skusList = viewModel.skuList,
                            onSkuExpandChange = viewModel::changeSkuDropDownExpand,
                            onSkuChange = viewModel::updateSKU,
                            onOldBoxNumberChange = viewModel::updateOldBoxNumber,
                            onMarkBoxAsDone = viewModel::markBoxAsDone,
                            onCategorySelected = viewModel::selectCategory,
                            onNewBoxNumberChange = viewModel::updateNewBoxNumber,
                            onIssueButtonClick = viewModel::onButtonClick,
                            isCategoriesExpanded = viewModel.isCategoryDropdownExpanded,
                            onCategoriesExpandChange = viewModel::changeCategoryDropDownExpand,
                            categoriesList = viewModel.categoryList,
                            showError = viewModel.showError,
                            error = viewModel.errorMessage,
                            isLoading = viewModel.isLoading,
                            image = viewModel.itemImage,
                            itemDamaged = viewModel.itemDamaged,
                            onDamagedCheckedChange = viewModel::updateItemDamaged,
                            showDialog = viewModel.showDialog,
                            onShowDialogChange = viewModel::onShowDialogChange,
                            quantityOne = viewModel.quantityOne,
                            quantityTwo = viewModel.quantityTwo,
                            itemStatus = viewModel.itemStatus,
                            updateItemStatus = viewModel::updateItemStatus
                        )

                    }

                    composable(route = BARCODE_SCREEN) {
                        val viewModel: BarcodeScanningViewModel = viewModel()
                        BarcodeScannerDemo(
                            navController = navController,
                            scannedCode = viewModel.scannedCode,
                            scannedText = viewModel.scannedText,
//                            isButtonEnabled = viewModel.isButtonEnabled,
                            onScannedCodeUpdate = viewModel::updateScannedCode,
                            onScannedTextUpdate = viewModel::updateScannedText
                        )
                    }


                }


            }

        }
    }
}