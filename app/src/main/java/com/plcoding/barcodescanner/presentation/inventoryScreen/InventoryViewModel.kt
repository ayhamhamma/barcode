package com.plcoding.barcodescanner.presentation.inventoryScreen

import android.app.Application
import android.content.res.AssetManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.barcodescanner.model.CategoriesResponseItem
import com.plcoding.barcodescanner.model.SortRequest
import com.plcoding.barcodescanner.remote.Repository
import com.plcoding.barcodescanner.utils.Constants.BOX_NUMBER
import com.plcoding.barcodescanner.utils.Constants.USER_NAME
import com.plcoding.barcodescanner.utils.Constants.VERSION
import com.plcoding.barcodescanner.utils.Resource
import com.plcoding.barcodescanner.utils.findClosestStrings
import com.plcoding.barcodescanner.utils.getTeamNumberString
//import com.plcoding.barcodescanner.utils.searchTopSkus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val assetManager: AssetManager = application.assets
    val context = application.applicationContext

    private val _dataList = MutableStateFlow<List<String>>(emptyList())
    val dataList = _dataList.asStateFlow()

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
    var itemName by mutableStateOf<String?>("")
        private set
    var categoryName by mutableStateOf("")
        private set
    var isCategoryDropdownExpanded by mutableStateOf(false)
        private set
    var categoryList by mutableStateOf<List<String>>(emptyList())
        private set

    var categoryWithBoxList by mutableStateOf<List<CategoriesResponseItem>>(emptyList())
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


    var showError by mutableStateOf(false)

    var itemImage by mutableStateOf("")

    var itemDamaged by mutableStateOf(false)
        private set

    var showDialog by mutableStateOf(false)

    var skuJob: Job? = null

    var itemStatus by mutableStateOf<String?>(null)

    var size by mutableStateOf(
        listOf("")
    )

    var selectedSize by mutableStateOf<String?>(null)

    var price by mutableStateOf<String?>(null)

    fun updateSelectedSize(value: String?) {
        selectedSize = value
    }

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


    fun updateOldBoxNumber(value: String) {
        BOX_NUMBER = value
        oldBoxNumber = value
    }

    init {
        oldBoxNumber = BOX_NUMBER
        getAllCategories()
    }

    fun loadCsv(itemSKU: String, barcode: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true  // Start loading

            val items = mutableListOf<String>()

            try {
                val inputStream = assetManager.open("sku.csv")
                val reader = BufferedReader(InputStreamReader(inputStream))

                reader.forEachLine { line ->
                    if (line.isNotBlank()) {
                        items.add(line.trim()) // Trim spaces
                    }
                }
                reader.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            _dataList.value = items

            Log.e("dataList", _dataList.value.toString())
            updateItemSKUAndBarcode(itemSKU, barcode)
        }
    }

    private fun getAllCategories() {
        viewModelScope.launch {
            val teamNumber = getTeamNumberString(context)
            if (teamNumber.isNullOrEmpty()) {
                errorMessage = "team number is empty"
                showErrorMessage()
            } else
                Repository.getAllCategories(teamNumber).collect {
                    when (it) {
                        is Resource.Success -> {
                            categoryList = it.data.map { it.name }
                            categoryWithBoxList = it.data

                            newBoxNumber =
                                categoryWithBoxList.find { it.name == categoryName }?.proposed_box
                                    ?: ""
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
        skuJob?.cancel()
        skuJob = null


        skuValue = itemSKU
        Log.e("ayham", itemSKU)
        Log.e("ayham", skuValue)

        viewModelScope.launch {
            skuList = findClosestStrings(itemSKU, _dataList.value, 4)
        }

        skuJob = viewModelScope.launch {
            delay(2000)
            onSkuChoose(itemSKU)
        }
    }


    fun updateItemSKUAndBarcode(itemSKU: String, barcode: String? = null) {


        skuValue = itemSKU
        viewModelScope.launch {
            skuList = findClosestStrings(itemSKU, _dataList.value, 4)
        }


        if (barcode != null) {
            savedBarcode = barcode
        }

        onSkuChoose(itemSKU)

    }


    fun onSkuChoose(sku: String) {
        viewModelScope.launch {
            Repository.getItem(sku).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        try {
                            if (result.data.isNotEmpty()) {
                                isSkuFound = true
                                itemName = result.data[0].name ?: "No Name" // Add null check
                                categoryName = result.data[0].category ?: "" // Add null check
                                newBoxNumber = categoryWithBoxList.find { it.name == categoryName }?.proposed_box ?: ""
                                itemImage = result.data[0].image ?: "" // Add null check
                                size = result.data.map { it.size ?: "" }
                                price = result.data[0].price?.toString() ?: "" // Add null check
                            } else {
                                isSkuFound = false
                                errorMessage = "No item found for SKU: $sku"
                                showErrorMessage()

                                itemName = "" // Add null check
                                categoryName =  "" // Add null check
                                newBoxNumber =  ""
                                itemImage = "" // Add null check
                                size = listOf("")
                                price = "" // Add null check
                            }
                            getAllCategories()
                        } catch (e: Exception) {
                            Log.e("ayham", "Error processing item data", e)
                            isSkuFound = false
                            errorMessage = "Error processing item: ${e.message}"
                            showErrorMessage()
                            itemName = "" // Add null check
                            categoryName =  "" // Add null check
                            newBoxNumber =  ""
                            itemImage = "" // Add null check
                            size = listOf("")
                            price = "" // Add null check
                        }
                    }

                    is Resource.Error -> {
                        isSkuFound = false
                        errorMessage = (result.errorMessage)
                        showErrorMessage()
                    }

                    is Resource.Loading -> {
                        isLoading = (result.isLoading)
                    }
                }
            }
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
        newBoxNumber =
            categoryWithBoxList.find { it.name == category }?.proposed_box ?: newBoxNumber
    }

    fun updateNewBoxNumber(value: String) {
        newBoxNumber = value
    }

    fun onButtonClick() {

        try {
            if (validateData()) {

                val status =
                    if (!isSkuFound) {
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
                            status = status,
                            size = selectedSize,
                            name = USER_NAME!!,
                            teamNumber = getTeamNumberString(context)!!,
                            oldBoxNumber = oldBoxNumber,
                            appVersion = VERSION
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
        } catch (e: Exception) {

        }

    }

    private fun validateData(): Boolean {
        Log.e("username", USER_NAME.toString())

        if (USER_NAME.isNullOrEmpty()) {
            errorMessage = "Please login first"
            showErrorMessage()
            return false
        }
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

        if (size.isNotEmpty()) {
            if (selectedSize == null) {
                errorMessage = "Please select a size"
                showErrorMessage()
                return false
            }
        }

        if(getTeamNumberString(context).isNullOrEmpty()){
            errorMessage = "team number is empty"
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
        price = null
        isSkuFound = false
    }

    fun markBoxAsDone(confirmed: Boolean = true) {
        if (confirmed) {
            errorMessage = "Box marked as done"
            showErrorMessage()
            oldBoxNumber = ""
            clearData()
        }
        showDialog = !confirmed
    }


}
