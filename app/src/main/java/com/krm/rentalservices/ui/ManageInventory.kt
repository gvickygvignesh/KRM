// ItemListScreen.kt
package com.krm.rentalservices.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import com.krm.rentalservices.model.InventoryItem
import com.krm.rentalservices.model.Product
import com.krm.rentalservices.viewmodel.ERROR_HTTP
import com.krm.rentalservices.viewmodel.ERROR_INTERNET
import com.krm.rentalservices.viewmodel.InventoryViewModel
import com.krm.rentalservices.viewmodel.SUCCESS

@Composable
fun ManageInventory(
    viewModel: InventoryViewModel = hiltViewModel(),
    navController: NavHostController,
    selectedProdId: String?
) {
    Log.d(TAG, "ManageInventory: init called")
    var docID by remember { mutableStateOf("") }
    var prodID by remember { mutableStateOf("") }
    var prodName by remember { mutableStateOf("") }
    var totCount by remember { mutableStateOf("") }
    var rentedCount by remember { mutableStateOf("") }
    var avlCount by remember { mutableStateOf("") }
    var damagedCount by remember { mutableStateOf("") }

    // Validation state
    var formIsValid by remember { mutableStateOf(true) }
    var validationMessage by remember { mutableStateOf("") }

    val prodState = viewModel.prodState.collectAsState()
    val invState = viewModel.invState.collectAsStateWithLifecycle()
    val selectedProduct = viewModel.selectedProductItem.collectAsState().value
    var isProductPresent by remember { mutableStateOf(false) }

    val state = viewModel.addItemState.value
    val context = LocalContext.current

    // Trigger ViewModel update when selectedProdId is not null
    LaunchedEffect(selectedProdId, prodState.value.success) {
        if (!selectedProdId.isNullOrEmpty() && prodState.value.success == SUCCESS) {
            val product = prodState.value.data.find { it.id == selectedProdId }
            product?.let { viewModel.setSelectProductItem(it) }
        }
    }

    // Sync UI state with ViewModel when selected product changes
    LaunchedEffect(selectedProduct, invState.value.success) {
        selectedProduct.let { product ->
            invState.value.data.find { it.prodId == product?.id }?.let { inventoryItem ->
                docID = inventoryItem.id
                prodID = inventoryItem.prodId
                prodName = inventoryItem.prodName
                totCount = inventoryItem.totCount.toString()
                avlCount = inventoryItem.avlCount.toString()
                rentedCount = inventoryItem.rentedCount.toString()
                damagedCount = inventoryItem.damagedCount.toString()
                isProductPresent = true
            } ?: run {
                docID = ""
                prodID = if (product?.id != null) product.id else ""
                prodName = if (product?.name != null) product.name else ""
                totCount = ""
                avlCount = ""
                rentedCount = ""
                damagedCount = ""
                isProductPresent = false
            }
        }
    }

    // Handle UI state updates for API response
    LaunchedEffect(state.success) {
        if (!state.isEventHandled) {
            when (state.success) {
                SUCCESS, ERROR_INTERNET -> {
                    Toast.makeText(context, "${state.data} Success", Toast.LENGTH_LONG).show()
                    viewModel.markEventHandled()
                    navController.popBackStack()
                }

                ERROR_HTTP -> {
                    Toast.makeText(context, "${state.data} Failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Scrollable view
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),// Corrected scrollable modifier
        verticalArrangement = Arrangement.spacedBy(8.dp)

    ) {
//        val progressBar = CircularProgressIndicator()
        /*when (prodState.value.success) {
            SUCCESS, ERROR_INTERNET -> {*/
        // Spinner Composable
        ProductSpinner(
            products = prodState.value.data,
            selectedProduct = selectedProduct,
            onProductSelected = { product ->
                if (product.id.isNotEmpty()) {
                    viewModel.setSelectProductItem(product)
                    val selectedInvItem: InventoryItem? = invState.value.data
                        .find { it.prodId == product.id }

                    if (selectedInvItem != null) {
                        Log.d(
                            TAG,
                            "ManageInventory: selectedInvItem not null n totCnt" + selectedInvItem.totCount
                        )
                        docID = selectedInvItem.id
                        prodID = selectedInvItem.prodId
                        prodName = selectedInvItem.prodName
                        totCount = selectedInvItem.totCount.toString()
                        avlCount = selectedInvItem.avlCount.toString()
                        rentedCount = selectedInvItem.rentedCount.toString()
                        damagedCount = selectedInvItem.damagedCount.toString()
                        isProductPresent = true
                    } else {
                        Log.d(
                            TAG,
                            "ManageInventory: selectedInvItem is null, prod id & name is " + product.id + " &" + product.name
                        )
                        docID = ""
                        prodID = product.id
                        prodName = product.name
                        totCount = ""
                        avlCount = ""
                        rentedCount = ""
                        damagedCount = ""
                        isProductPresent = false
                    }
                } else {
                    Log.d(TAG, "ManageInventory: all empty set called")
                    docID = ""
                    prodID = ""
                    prodName = ""
                    totCount = ""
                    avlCount = ""
                    rentedCount = ""
                    damagedCount = ""
                    isProductPresent = false
                    viewModel.setSelectProductItem(null)
                }
            },
            modifier = Modifier // Ensures dropdown aligns correctly
                .fillMaxWidth()
        )
//            }
//        }

        when (prodState.value.isLoading) {
            true -> {
                CircularProgressIndicator()
            }
            false ->  {}
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = totCount,
            onValueChange = { totCount = it },
            label = { Text("Total Count") },
            isError = totCount.isEmpty() && !formIsValid,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),

            )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = rentedCount,
            onValueChange = {
                Log.d(TAG, "ManageInventory: rent value change$it")
                rentedCount = it
            },
            label = { Text("Rented Count") },
            isError = rentedCount.isEmpty() && !formIsValid,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = avlCount,
            onValueChange = { avlCount = it },
            label = { Text("Available Count") },
            isError = avlCount.isEmpty() && !formIsValid,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = damagedCount,
            onValueChange = { damagedCount = it },
            label = { Text("Damaged Count") },
            isError = damagedCount.isEmpty() && !formIsValid,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Validation message
        if (!formIsValid) {
            Text(
                text = validationMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = {
                Log.d(TAG, "ManageInventory: selected prodID, name is$prodID &$prodName")
                // Validation logic
                when {
                    prodID.isEmpty() -> {
                        validationMessage = "Choose Product from the Dropdown"
                        formIsValid = false
                    }

                    prodName.isEmpty() -> {
                        validationMessage = "Choose Product from the Dropdown"
                        formIsValid = false
                    }

                    totCount.isEmpty() -> {
                        validationMessage = "Total count is required."
                        formIsValid = false
                    }

                    rentedCount.isEmpty() -> {
                        validationMessage = "Rented count is required."
                        formIsValid = false
                    }

                    avlCount.isEmpty() -> {
                        validationMessage = "Available count is required."
                        formIsValid = false
                    }

                    damagedCount.isEmpty() -> {
                        validationMessage = "Damaged count is required."
                        formIsValid = false
                    }

                    else -> {
                        formIsValid = true
                        validationMessage = ""
                        viewModel.addOrUpdateInventoryItem(
                            docId = docID,
                            prodId = prodID,
                            name = prodName,
                            totCount = totCount.toInt(),
                            rentedCount = rentedCount.toInt(),
                            avlCount = avlCount.toInt(),
                            damagedCount = damagedCount.toInt(),
                            Timestamp.now(),
                            isProductPresent,
                            false
                        )
                        prodName = ""
                        totCount = ""
                        rentedCount = ""
                        avlCount = ""
                        damagedCount = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
//            enabled = formIsValid,
        ) {
            Text(if (isProductPresent) "Update Inventory" else "Add to Inventory")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSpinner(
    products: List<Product>,
    selectedProduct: Product?, // Preselected product
    onProductSelected: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // **Fix: Ensure `selectedItem` updates when `selectedProduct` changes**
    val selectedItem = remember(selectedProduct) { selectedProduct }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedItem?.name ?: "Select Product",
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Product",style = MaterialTheme.typography.labelLarge) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Option to reset selection
            DropdownMenuItem(
                text = { Text("Select Product", style = MaterialTheme.typography.labelLarge) },
                onClick = {
                    expanded = false
                    onProductSelected(Product("", "", 0, "", null)) // Reset product
                }
            )

            products.forEach { product ->
                DropdownMenuItem(
                    text = { Text(product.name, style = MaterialTheme.typography.labelLarge) },
                    onClick = {
                        expanded = false // Close dropdown
                        onProductSelected(product) // Notify parent about selection
                    }
                )
            }
        }
    }
}






