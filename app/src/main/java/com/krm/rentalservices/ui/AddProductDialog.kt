// AddItemScreen.kt
package com.krm.rentalservices.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.google.firebase.Timestamp
import com.krm.rentalservices.model.Product
import com.krm.rentalservices.viewmodel.ERROR_HTTP
import com.krm.rentalservices.viewmodel.ERROR_INTERNET
import com.krm.rentalservices.viewmodel.InventoryViewModel
import com.krm.rentalservices.viewmodel.SUCCESS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductDialog(
    viewModel: InventoryViewModel,
    context: Context,
    onDismiss: () -> Unit
) {
    val selectedItem by viewModel.selectedProductItem.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val itemNameFocusRequester = remember { FocusRequester() }

    var productForm by remember {
        mutableStateOf(ProductForm())
    }

    /* productForm = if (selectedItem != null) {
         ProductForm(
             name = selectedItem!!.name,
             description = selectedItem!!.description,
             price = selectedItem!!.rentalPrice.toString() ?: ""
         )
     } else {
         ProductForm()
     }*/

    LaunchedEffect(selectedItem) {
        productForm = if (selectedItem != null) {
            ProductForm(
                name = selectedItem!!.name,
                description = selectedItem!!.description,
                price = selectedItem!!.rentalPrice.toString() ?: ""
            )
        } else {
            ProductForm()
        }
    }

    val buttonText by remember(selectedItem) {
        derivedStateOf {
            if (selectedItem == null) "Add Item" else "Update Item"
        }
    }

    val state = viewModel.addItemState.value

    if (!state.isEventHandled) {
//    LaunchedEffect(state.success) {
        when (state.success) {
            SUCCESS, ERROR_INTERNET -> {
                Toast.makeText(
                    context, " Success", Toast.LENGTH_LONG
                ).show()
                viewModel.markEventHandled()
                viewModel.clearSelectedProductItem()

                onDismiss() // Close the dialog after success
            }

            ERROR_HTTP -> {
                Toast.makeText(
                    context, state.data + " Failed", Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    BasicAlertDialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .focusRequester(itemNameFocusRequester),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = productForm.name,
                    onValueChange = { productForm = productForm.copy(name = it) },
                    label = { Text("Item Name", style = MaterialTheme.typography.labelLarge) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = productForm.description,
                    onValueChange = { productForm = productForm.copy(description = it) },
                    label = { Text("Description", style = MaterialTheme.typography.labelLarge) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = productForm.price,
                    onValueChange = { productForm = productForm.copy(price = it) },
                    label = { Text("Price", style = MaterialTheme.typography.labelLarge) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val price = productForm.price.toLongOrNull() ?: 0
                        if (selectedItem?.id?.isNotEmpty() == true) {
                            viewModel.updateProduct(
                                item = Product(
                                    selectedItem!!.id,
                                    productForm.name,
                                    price,
                                    productForm.description,
                                    Timestamp.now()
                                )
                            )
                        } else {
                            viewModel.addProduct(
                                productForm.name,
                                productForm.description,
                                price,
                                Timestamp.now()
                            )
                        }
                        keyboardController?.hide()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(buttonText)
                }
            }
        }
    }
}

data class ProductForm(
    val name: String = "",
    val description: String = "",
    val price: String = ""
)


/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductDialog(
    viewModel: InventoryViewModel, //navController: NavController,
    context: Context, onDismiss: () -> Unit
) {
    val selectedItem by viewModel.selectedItem.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val itemNameFocusRequester = remember { FocusRequester() }

    var itemName by remember { mutableStateOf("") }
    var itemDesc by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") } // State for price
    var buttonText = if (selectedItem == null) "Add Item" else "Update Item"

    // Pre-fill fields when an item is selected for editing
    LaunchedEffect(selectedItem) {
        itemName = selectedItem?.name ?: ""
        itemPrice = selectedItem?.rentalPrice?.toString() ?: ""
        itemDesc = selectedItem?.description ?: ""
        buttonText = if (selectedItem == null) "Add Item" else "Update item"
    }

    val state = viewModel.addItemState.value

    when (state.success) {
        SUCCESS, ERROR_INTERNET -> {
            Toast.makeText(
                LocalContext.current, state.data + " Success", Toast.LENGTH_LONG
            ).show()
        }
    }


    BasicAlertDialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
//                    .padding(16.dp),
                shape = MaterialTheme.shapes.large

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .focusRequester(itemNameFocusRequester),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Item Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = itemDesc,
                        onValueChange = { itemDesc = it },
                        label = { Text("Description") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = itemPrice,
                        onValueChange = { itemPrice = it },
                        label = { Text("Price") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val quantity = itemDesc
                            val price = itemPrice.toLongOrNull() ?: 0
                            val name = itemName
                            if (selectedItem?.id?.isEmpty() == false) {
                                viewModel.updateProduct(
                                    item = Product(
                                        selectedItem!!.id, name, price, quantity, Timestamp.now()
                                    )
                                )
                            } else {
                                viewModel.addProduct(itemName, quantity, price, Timestamp.now())
                            }
                            // Clear the fields after adding
                            itemName = ""
                            itemDesc = ""
                            itemPrice = ""

                            buttonText = "Add Item"
                            viewModel.clearSelectedItem()
                            keyboardController?.hide()
//                itemNameFocusRequester.requestFocus()
                        }, modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(buttonText)
                    }

                }
            }

            *//* when (state.isLoading) {
             true -> {
                 CircularProgressIndicator()
             }

             false -> TODO()
         }*//*
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddItemScreen() {
//    AddProduct()
}*/
