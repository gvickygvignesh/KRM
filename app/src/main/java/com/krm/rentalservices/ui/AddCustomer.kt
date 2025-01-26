// ItemListScreen.kt
package com.krm.rentalservices.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import com.krm.rentalservices.viewmodel.CustomersViewModel
import com.krm.rentalservices.viewmodel.ERROR_INTERNET
import com.krm.rentalservices.viewmodel.SUCCESS

@Composable
fun AddCustomer(
    viewModel: CustomersViewModel = hiltViewModel(),
    navController: NavHostController
) {
//    val items = viewModel.itemsFlow.collectAsState(initial = emptyList()).value // Collect items
    var showDialog by remember { mutableStateOf(false) }
    val itemNameFocusRequester = remember { FocusRequester() }

    var custName by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var mobNo by remember { mutableStateOf("") }
    var idNo by remember { mutableStateOf("") }
    var idType by remember { mutableStateOf("") }
    var idPhoto by remember { mutableStateOf("") }
//    val productState by viewModel.productState.collectAsState()

    // Validation state
    var formIsValid by remember { mutableStateOf(true) }
    var validationMessage by remember { mutableStateOf("") }

    val state = viewModel.addCustomerState.value

    when (state.success) {
        SUCCESS, ERROR_INTERNET -> {
            Toast.makeText(
                LocalContext.current, state.data + " Customer added successfully", Toast.LENGTH_LONG
            ).show()
        }
    }

//    navController.popBackStack()
    // Scrollable view
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())  // Corrected scrollable modifier
    ) {
        OutlinedTextField(
            value = custName,
            onValueChange = { custName = it },
            label = { Text("Customer Name") },
            isError = custName.isEmpty() && !formIsValid,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = type,
            onValueChange = { type = it },
            label = { Text("Type") },
            isError = type.isEmpty() && !formIsValid,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            isError = address.isEmpty() && !formIsValid,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = mobNo,
            onValueChange = { mobNo = it },
            label = { Text("Mobile Number") },
            isError = mobNo.isEmpty() && !formIsValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = idNo,
            onValueChange = { idNo = it },
            label = { Text("ID Number") },
            isError = idNo.isEmpty() && !formIsValid,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = idType,
            onValueChange = { idType = it },
            label = { Text("ID Type") },
            isError = idType.isEmpty() && !formIsValid,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = idPhoto,
            onValueChange = { idPhoto = it },
            label = { Text("ID Photo URL") },
            isError = idPhoto.isEmpty() && !formIsValid,
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
                    custName.isEmpty() -> {
                        validationMessage = "Customer Name is required."
                        formIsValid = false
                    }

                    type.isEmpty() -> {
                        validationMessage = "Type is required."
                        formIsValid = false
                    }

                    address.isEmpty() -> {
                        validationMessage = "Address is required."
                        formIsValid = false
                    }

                    mobNo.isEmpty() -> {
                        validationMessage = "Mobile Number is required."
                        formIsValid = false
                    }

                    idNo.isEmpty() -> {
                        validationMessage = "ID Number is required."
                        formIsValid = false
                    }

                    idType.isEmpty() -> {
                        validationMessage = "ID Type is required."
                        formIsValid = false
                    }

                    idPhoto.isEmpty() -> {
                        validationMessage = "ID Photo URL is required."
                        formIsValid = false
                    }

                    else -> {
                        formIsValid = true
                        validationMessage = ""
                        viewModel.addCustomer(
                            name = custName, type = type, address = address,
                            mobNo = mobNo, idNo = idNo, idType = idType, idPhoto = idPhoto,
                            Timestamp.now()
                        )
                        custName = ""
                        type = ""
                        address = ""
                        mobNo = ""
                        idNo = ""
                        idType = ""
                        idPhoto = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = formIsValid,
        ) {
            Text("Add Customer")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerFormPreview() {
//    CustomerDirectory()
}

