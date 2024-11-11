// AddItemScreen.kt
package com.krm.rentalservices

import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun AddItemScreen(viewModel: ItemViewModel) {
    Log.d(TAG, "AddItemScreen: called")
    val itemNameFocusRequester = remember { FocusRequester() }

    var itemName by remember { mutableStateOf(TextFieldValue("")) }
    var itemQuantity by remember { mutableStateOf(TextFieldValue("")) }
    var itemPrice by remember { mutableStateOf(TextFieldValue("")) } // State for price

    val state = viewModel.add_inv_state.value

    when (state.success) {
        SUCCESS, ERROR_INTERNET, ERROR_HTTP  -> {
            Toast.makeText(LocalContext.current, "Added item " + state.data + " Successfully", Toast.LENGTH_LONG).show()
        }

    }



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
            value = itemQuantity,
            onValueChange = { itemQuantity = it },
            label = { Text("Quantity") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                val quantity = itemQuantity.text.toIntOrNull() ?: 0
                val price = itemPrice.text.toDoubleOrNull() ?: 0.0
                viewModel.addItem(itemName.text, quantity, price)
                // Clear the fields after adding
                itemName = TextFieldValue("")
                itemQuantity = TextFieldValue("")
                itemPrice = TextFieldValue("")
                itemNameFocusRequester.requestFocus()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Item")
        }
    }
}
