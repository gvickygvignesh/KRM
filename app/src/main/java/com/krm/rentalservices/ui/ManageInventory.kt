// ItemListScreen.kt
package com.krm.rentalservices.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.Timestamp
import com.krm.rentalservices.model.Product
import com.krm.rentalservices.viewmodel.ERROR_INTERNET
import com.krm.rentalservices.viewmodel.InventoryViewModel
import com.krm.rentalservices.viewmodel.SUCCESS

@Composable
fun ManageInventory(
    viewModel: InventoryViewModel = hiltViewModel(),
    navController: NavHostController
) {

    var showDialog by remember { mutableStateOf(false) }
    val itemNameFocusRequester = remember { FocusRequester() }

    var prodID by remember { mutableStateOf("") }
    var prodName by remember { mutableStateOf("") }
    var totCount by remember { mutableStateOf("") }
    var rentedCount by remember { mutableStateOf("") }
    var avlCount by remember { mutableStateOf("") }
    var damagedCount by remember { mutableStateOf("") }

    //    val items = viewModel.itemsFlow.collectAsState(initial = emptyList()).value // Collect items
//    val productState by viewModel.productState.collectAsState()

    // Validation state
    var formIsValid by remember { mutableStateOf(true) }
    var validationMessage by remember { mutableStateOf("") }

    val prodState = viewModel.prodState.collectAsState()
//    val state = viewModel.addProdState.value
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val state = viewModel.addItemState.value

    when (state.success) {
        SUCCESS, ERROR_INTERNET -> {
            Toast.makeText(
                LocalContext.current, state.data + " Success", Toast.LENGTH_LONG
            ).show()
        }
    }

//    navController.popBackStack()
    // Scrollable view
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),// Corrected scrollable modifier
        verticalArrangement = Arrangement.spacedBy(8.dp)

    ) {

        val ind = CircularProgressIndicator(LocalContext.current)

        when (prodState.value.success) {
            SUCCESS, ERROR_INTERNET -> {
                // Spinner Composable
                ProductSpinner(
                    products = prodState.value.data,
                    selectedProduct = selectedProduct,
                    onProductSelected = { product ->
                        selectedProduct = product
                        prodID = selectedProduct?.id.toString()
                        prodName = selectedProduct?.name.toString()
                        println("Selected Product: ${product.name}")
                    },
                    modifier = Modifier // Ensures dropdown aligns correctly
                        .wrapContentSize()
                )

            }
        }

        when (prodState.value.isLoading) {
            true -> ind.show()
            false -> ind.hide()
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
            onValueChange = { rentedCount = it },
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
                        viewModel.addInventoryItem(
                            id = prodID,
                            name = prodName,
                            totCount = totCount.toInt(),
                            rentedCount = rentedCount.toInt(),
                            avlCount = avlCount.toInt(),
                            damagedCount = damagedCount.toInt(),
                            Timestamp.now()
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
            Text("Submit")
        }
    }
}


/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSpinner(
    list: List<Product>,
    preselected: Product,
    onSelectionChanged: (product: Product) -> Unit,
    modifier: Modifier = Modifier
) {

    var selected by remember { mutableStateOf(preselected) }
    var expanded by remember { mutableStateOf(false) } // initial value

    OutlinedCard(
        modifier = modifier.clickable {
            expanded = !expanded
        }
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {

            Text(
                text = selected.name,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Icon(Icons.Outlined.ArrowDropDown, null, modifier = Modifier.padding(8.dp))

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.wrapContentWidth()   // delete this modifier and use .wrapContentWidth() if you would like to wrap the dropdown menu around the content
            ) {
                list.forEach { listEntry ->

                    DropdownMenuItem(
                        onClick = {
                            selected = listEntry
                            expanded = false
                            onSelectionChanged(selected)
                        },
                        text = {
                            Text(
                                text = listEntry.name,
                                modifier = Modifier
                                    //.wrapContentWidth()  //optional instad of fillMaxWidth
                                    .fillMaxWidth()
                                    .align(Alignment.Start)
                            )
                        },
                    )
                }
            }

        }
    }
}*/


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSpinner(
    products: List<Product>,
    selectedProduct: Product?, // Pass the default product if available
    onProductSelected: (Product) -> Unit,
    modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) } // Controls dropdown visibility
    var selectedItem by remember { mutableStateOf(selectedProduct ?: products.firstOrNull()) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded } // Toggle dropdown visibility
    ) {
        OutlinedTextField(
            value = selectedItem?.name ?: "Select", // Show default text
            onValueChange = {},
            readOnly = true, // Makes the field uneditable
            label = { Text("Select Product") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = modifier
//            /*modifier = Modifier
                .menuAnchor() // Ensures dropdown aligns correctly
                .wrapContentSize()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier
        ) {
            products.forEach { product ->
                DropdownMenuItem(
                    text = { Text(product.name) },
                    onClick = {
                        selectedItem = product // Update selected item
                        expanded = false // Close dropdown
                        onProductSelected(product) // Notify parent about the selection
                    }
                )
            }
        }
    }
}




