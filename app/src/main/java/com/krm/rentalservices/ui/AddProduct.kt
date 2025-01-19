// AddItemScreen.kt
package com.krm.rentalservices.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.krm.rentalservices.model.Product
import com.krm.rentalservices.viewmodel.ERROR_INTERNET
import com.krm.rentalservices.viewmodel.InventoryViewModel
import com.krm.rentalservices.viewmodel.SUCCESS

@Composable
fun AddProduct(viewModel: InventoryViewModel, navController: NavController) {
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

    /* when (state.isLoading) {
         true -> {
             CircularProgressIndicator()
         }

         false -> TODO()
     }*/

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

@Preview(showBackground = true)
@Composable
fun PreviewAddItemScreen() {
//    AddProduct()
}
