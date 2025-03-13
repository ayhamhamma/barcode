package com.plcoding.barcodescanner.presentation.inventoryScreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.plcoding.barcodescanner.utils.Constants.BARCODE_SCREEN

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Preview(showBackground = true)
@Composable
fun InventoryTransferScreen(
    navController: NavController? = null,
    oldBoxNumber: String = "",
    skuValue: String = "",
    itemName: String = "",
    categoryName: String = "",
    newBoxNumber: String = "",
    isSkuFound: Boolean = false,
    isSkuExpanded: Boolean = true,
    skusList: List<String> = listOf(""),
    onSkuExpandChange: (Boolean) -> Unit = {

    },
    onSkuChange: (String) -> Unit = {

    },
    onOldBoxNumberChange: (String) -> Unit = {

    },
    onMarkBoxAsDone: () -> Unit = {

    },
    onCategorySelected: (String) -> Unit = {

    },
    onNewBoxNumberChange: (String) -> Unit = {

    },
    onIssueButtonClick: () -> Unit = {

    },

    isCategoriesExpanded: Boolean = false,
    onCategoriesExpandChange: (Boolean) -> Unit = {

    },
    categoriesList: List<String> = listOf(""),
    showError: Boolean = false,
    error: String = "",
    image: String = "",
    isLoading: Boolean = false,
    itemDamaged: Boolean = false,
    onDamagedCheckedChange: (Boolean) -> Unit = {},
    showDialog: Boolean = false,
    onShowDialogChange: (Boolean) -> Unit = {

    },
    quantityOne: Int = 0,
    quantityTwo: Int = 0,
    itemStatus: String? = "",
    updateItemStatus: (String?) -> Unit = {

    },
    sizeList: List<String> = listOf(),
    onSizeSelected: (String?) -> Unit = {},
    selectedSize : String? = null
) {

    val context = LocalContext.current


    LaunchedEffect(showError) {
        Log.e("ayham", "error: $error")
        if (showError) {
            try {
                Log.e("ayham", "error: $error")
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {

            }

        }
    }

    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {

                Spacer(Modifier.size(30.dp))
                // Old Box Number section
                Text(
                    text = "Old Box number",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = oldBoxNumber,
                    onValueChange = onOldBoxNumberChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .height(56.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                TextButton(
                    onClick = { onShowDialogChange(true) },
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "mark box as done",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scan SKU Button
                Button(
                    onClick = {
                        navController?.popBackStack(BARCODE_SCREEN, inclusive = true)
                        navController?.navigate(BARCODE_SCREEN)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color.Gray),
                ) {
                    Text(
                        text = "Scan SKU",
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SKU Dropdown
                ExposedDropdownMenuBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    expanded = isSkuExpanded,
                    onExpandedChange = onSkuExpandChange
                ) {

                    // Dropdown button
                    Button(
                        onClick = { onSkuExpandChange(true) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.Gray),
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Log.e("ayham", skuValue)
                            BasicTextField(
                                value = skuValue,
                                onValueChange = onSkuChange,
                                textStyle = TextStyle(
                                    color = Color.Black,
                                    fontSize = 16.sp
                                ),

                                )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color.Black
                            )
                        }

                        ExposedDropdownMenu(
                            expanded = isSkuExpanded,
                            onDismissRequest = { onSkuExpandChange(false) }) {
                            if (skusList.isNotEmpty()) {
                                skusList.forEach {
                                    DropdownMenuItem(text = { Text(text = it) }, onClick = {
                                        onSkuChange(it)
                                    })
                                }
                            } else {
                                DropdownMenuItem(text = { Text(text = skuValue) }, onClick = {

                                })
                            }


                        }


                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                // Item details card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Item image and name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                // Placeholder for item image
                                AsyncImage(
                                    model = image,
                                    contentDescription = "Item image",
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = itemName,
                                fontSize = 20.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Category row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Category:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(100.dp)
                            )

                            // SKU Dropdown
                            ExposedDropdownMenuBox(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                expanded = isCategoriesExpanded,
                                onExpandedChange = onCategoriesExpandChange
                            ) {
                                Button(
                                    onClick = { onCategoriesExpandChange(!isCategoriesExpanded) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.LightGray
                                    ),
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp,
                                        vertical = 0.dp
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = categoryName,
                                            color = Color.Black,
                                            fontSize = 16.sp,
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Category dropdown",
                                            tint = Color.Black
                                        )
                                    }
                                }

                                ExposedDropdownMenu(
                                    expanded = isCategoriesExpanded,
                                    onDismissRequest = { onCategoriesExpandChange(false) }) {
                                    if (categoriesList.isNotEmpty()) {
                                        categoriesList.sortedBy { it }.forEach {
                                            DropdownMenuItem(text = { Text(text = it) }, onClick = {
                                                onCategorySelected(it)
                                            })
                                        }
                                    } else {
                                        DropdownMenuItem(
                                            text = { Text(text = categoryName) },
                                            onClick = {

                                            })
                                    }

                                }

                            }
                        }

                        if (sizeList.isNotEmpty()) {
                            Text(
                                text = "Size",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )

                            sizeList.forEach {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = selectedSize == it ,
                                        onClick = {
                                            if (itemStatus == it) {
                                                onSizeSelected(null)
                                            } else {
                                                onSizeSelected(it)
                                            }
                                        }
                                    )
                                    Text(
                                        text = it,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )

                                }
                            }
                        }

                        Text(
                            text = "Item Status",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        Column {

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = itemStatus == "damaged",
                                    onClick = {
                                        if (itemStatus == "damaged") {
                                            updateItemStatus(null)
                                        } else {
                                            updateItemStatus("damaged")
                                        }
                                    }
                                )
                                Text(
                                    text = "Item Damaged",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )

                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = itemStatus == "gift",
                                    onClick = {
                                        if (itemStatus == "gift") {
                                            updateItemStatus(null)
                                        } else {
                                            updateItemStatus("gift")
                                        }
                                    }
                                )
                                Text(
                                    text = "gift",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )

                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = itemStatus == "donation",
                                    onClick = {
                                        if (itemStatus == "donation") {
                                            updateItemStatus(null)
                                        } else {
                                            updateItemStatus("donation")
                                        }
                                    }
                                )
                                Text(
                                    text = "donation",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )

                            }
                        }


                        // New box number row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "New box #",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(100.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .height(48.dp)
                                    .width(100.dp)
                                    .background(Color.LightGray, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                BasicTextField(
                                    value = newBoxNumber,
                                    onValueChange = onNewBoxNumberChange,
                                    textStyle = TextStyle(
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                )
                            }

                            Spacer(modifier = Modifier.size(10.dp))

                            Button(
                                onClick = onIssueButtonClick,
                                modifier = Modifier
                                    .height(48.dp)
                                    .width(120.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSkuFound) Color(0xFFFFD700) else Color.Red
                                )
                            ) {
                                Text(
                                    text = if (isSkuFound) "Sort" else "issue",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
        if (isLoading) {
            Dialog(
                onDismissRequest = {
                    // nothing
                },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(White, shape = RoundedCornerShape(8.dp))
                ) {
                    CircularProgressIndicator()
                }
            }
        }


        ConfirmationDialog(
            showDialog = showDialog,
            message = "Are you sure you want to mark this box as done?",
            confirmText = "Mark as Done",
            quantityOne = quantityOne.toString(),
            quantityTwo = quantityTwo.toString(),
            cancelText = "Cancel",
            onConfirm = onMarkBoxAsDone,
            onDismiss = { onShowDialogChange(false) }
        )
    }
}

@Composable
fun ConfirmationDialog(
    showDialog: Boolean,
    message: String,
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    quantityOne: String = "",
    quantityTwo: String = "",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = quantityOne + " out of " + quantityTwo + "Sorted",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    }
                ) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(cancelText)
                }
            }
        )
    }
}