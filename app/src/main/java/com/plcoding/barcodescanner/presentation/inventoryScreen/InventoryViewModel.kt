package com.plcoding.barcodescanner.presentation.inventoryScreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.barcodescanner.model.MarkBoxAsDoneRequest
import com.plcoding.barcodescanner.model.SortRequest
import com.plcoding.barcodescanner.remote.Repository
import com.plcoding.barcodescanner.utils.Constants.BOX_NUMBER
import com.plcoding.barcodescanner.utils.Constants.BOX_SKUS_LIST
import com.plcoding.barcodescanner.utils.Resource
import com.plcoding.barcodescanner.utils.findClosestStrings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class InventoryViewModel() : ViewModel() {


    // Old Box Number
    var oldBoxNumber by mutableStateOf("")
        private set

    // SKU related states
    var skuValue by mutableStateOf("")
        private set
    var isSkuDropdownExpanded by mutableStateOf(false)
        private set
    var skuList by mutableStateOf<List<String>>(emptyList())
        private set

    // Item Details
    var itemName by mutableStateOf("")
        private set
    var categoryName by mutableStateOf("")
        private set
    var isCategoryDropdownExpanded by mutableStateOf(false)
        private set
    var categoryList by mutableStateOf<List<String>>(emptyList())
        private set

    // New Box Number
    var newBoxNumber by mutableStateOf("1")
        private set

    // Button State
    var isSkuFound by mutableStateOf(false)
        private set

    // Screen state
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf("")
        private set

    var savedBarcode: String = ""

    var oldBoxJob: Job? = null

    var showError by mutableStateOf(false)

    var itemImage by mutableStateOf("")

    var itemDamaged by mutableStateOf(false)
        private set

    var showDialog by mutableStateOf(false)

    var quantityOne by mutableStateOf(0)

    var quantityTwo by mutableStateOf(0)

    var itemStatus by mutableStateOf<String?>(null)

    fun updateItemStatus(value: String?) {
        itemStatus = value
    }

    fun onShowDialogChange(value: Boolean) {


        if (value)
            markBoxAsDone(false)
        else {
            showDialog = false
        }
    }

    // Update functions for each state
    fun updateOldBoxNumber(value: String) {
        oldBoxNumber = value
        oldBoxJob?.cancel()
        oldBoxJob = null
        oldBoxJob = viewModelScope.launch {
            delay(1000)

            getBoxItemsByNumber(value)
        }
    }

    fun updateItemDamaged(value: Boolean) {
        itemDamaged = value
    }

    init {
        getBoxItemsByNumber(BOX_NUMBER)
        oldBoxNumber = BOX_NUMBER
        getAllCategories()
    }

    private fun getAllCategories() {
        viewModelScope.launch {
            Repository.getAllCategories().collect {
                when (it) {
                    is Resource.Success -> {
                        categoryList = it.data.map { it.name }
                    }

                    is Resource.Error -> {
                        errorMessage = it.errorMessage
                        showErrorMessage()

                    }

                    is Resource.Loading -> {
                        isLoading = it.isLoading

                    }
                }
            }
        }
    }

    fun updateSKU(itemSKU: String) {
        skuValue = itemSKU
        Log.e("ayham", itemSKU)
        Log.e("ayham", skuValue)
        if (!BOX_SKUS_LIST.contains(itemSKU)) {
            skuList = findClosestStrings(itemSKU, BOX_SKUS_LIST, 3)
        }
        onSkuChoose(itemSKU)
    }


    fun updateItemSKUAndBarcode(itemSKU: String, barcode: String? = null) {

        skuValue = itemSKU
        if (!BOX_SKUS_LIST.contains(itemSKU)) {
            skuList = findClosestStrings(itemSKU, BOX_SKUS_LIST, 3)
        }



        if (barcode != null) {
            savedBarcode = barcode
        }

        onSkuChoose(itemSKU)

    }


    private fun getBoxItemsByNumber(boxNumber: String) {
        viewModelScope.launch {
            Repository.getBoxItems(boxNumber).collect { result ->
                when (result) {
                    is Resource.Success -> {

                        BOX_SKUS_LIST = result.data
                        BOX_NUMBER = boxNumber

                    }


                    is Resource.Error -> {
                        errorMessage = result.errorMessage
                        showErrorMessage()
                    }

                    is Resource.Loading -> {
                        isLoading = result.isLoading
                    }
                }
            }
        }
    }


    fun onSkuChoose(sku: String) {
        if (BOX_SKUS_LIST.contains(sku)) {
            viewModelScope.launch {
                Repository.getItem(sku).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            itemName = result.data.name
                            categoryName = result.data.category
                            isSkuFound = true
                            newBoxNumber = result.data.box ?: ""
                            itemImage = result.data.image
                        }

                        is Resource.Error -> {
                            errorMessage = (result.errorMessage)
                            showErrorMessage()
                        }

                        is Resource.Loading -> {
                            isLoading = (result.isLoading)
                        }
                    }
                }
            }
        } else {
            isSkuFound = false
        }
    }

    fun changeSkuDropDownExpand(expand: Boolean) {
        isSkuDropdownExpanded = expand
    }

    fun changeCategoryDropDownExpand(expand: Boolean) {
        isCategoryDropdownExpanded = expand
    }


    fun selectCategory(category: String) {
        categoryName = category
    }

    fun updateNewBoxNumber(value: String) {
        newBoxNumber = value
    }

    fun onButtonClick() {
        if (validateData()) {

            val status = if (!BOX_SKUS_LIST.contains(skuValue)) {
                2
            } else {
                when (itemStatus) {
                    "damaged" -> 1
                    "gift" -> 4
                    "donation" -> 3
                    else -> 0
                }
            }

            viewModelScope.launch {
                Repository.sortItem(
                    SortRequest(
                        itemBarcode = savedBarcode,
                        itemCategory = categoryName,
                        itemSKU = skuValue,
                        sortedBox = newBoxNumber,
                        status = status
                    )
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            errorMessage = result.data.message
                            showErrorMessage()
                            clearData()
                        }

                        is Resource.Error -> {
                            errorMessage = result.errorMessage
                            showErrorMessage()

                        }

                        is Resource.Loading -> {
                            isLoading = result.isLoading
                        }
                    }

                }
            }
        }
    }

    private fun validateData(): Boolean {

        if (oldBoxNumber.isEmpty()) {
            errorMessage = "Please enter a box number"
            showErrorMessage()
            return false
        }
        if (skuValue.isEmpty()) {
            errorMessage = "Please enter a SKU"
            showErrorMessage()
            return false
        }
        if (categoryName.isEmpty()) {
            errorMessage = "Please select a category"
            showErrorMessage()
            return false
        }
        if (newBoxNumber.isEmpty()) {
            errorMessage = "Please enter a new box number"
            showErrorMessage()
            return false
        }

        if (savedBarcode.isEmpty()) {
            errorMessage = "barcode is Empty"
            showErrorMessage()
            return false

        }
        return true
    }

    private fun showErrorMessage() {
        viewModelScope.launch {
            showError = true
            delay(1000)
            showError = false
        }

    }

    fun clearData() {
        skuValue = ""
        isSkuDropdownExpanded = false
        skuList = emptyList()
        itemName = ""
        itemImage = ""
        categoryName = ""
        isCategoryDropdownExpanded = false
        categoryList = emptyList()
        newBoxNumber = ""
        isSkuFound = false
    }

    fun markBoxAsDone(confirmed: Boolean = true) {
        viewModelScope.launch {
            Repository.markBoxAsDone(
                MarkBoxAsDoneRequest(
                    oldBoxNumber,
                    confirmed = confirmed
                )
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        errorMessage = result.data.message ?: ""
                        showErrorMessage()
                        oldBoxNumber = ""
                        clearData()
                        showDialog = !confirmed
                        quantityOne = result.data.summary?.totalScanned ?: 0
                        quantityTwo = result.data.summary?.totalExpected ?: 0
                    }

                    is Resource.Error -> {
                        errorMessage = result.errorMessage
                        showErrorMessage()
//                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()


                    }

                    is Resource.Loading -> {
                        isLoading = result.isLoading
                    }
                }
            }
        }

    }
}
