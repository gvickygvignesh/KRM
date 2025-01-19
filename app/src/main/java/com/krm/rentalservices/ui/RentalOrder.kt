// ItemListScreen.kt
package com.krm.rentalservices.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.krm.rentalservices.model.Customer
import com.krm.rentalservices.viewmodel.CustomersViewModel
import com.krm.rentalservices.viewmodel.InventoryViewModel
import com.krm.rentalservices.viewmodel.RentalOrderViewModel

/*@Composable
fun Order(viewModel: InventoryViewModel, navController: NavHostController) {
//    val items = viewModel.itemsFlow.collectAsState(initial = emptyList()).value // Collect items
    val state = viewModel.prodState.value
    var showDialog by remember { mutableStateOf(false) }

    Text(text = "Coming soon")
}*/


/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalOrder(
    viewModel: InventoryViewModel = hiltViewModel(),
    customersViewModel: CustomersViewModel,
    rentalOrderViewModel: RentalOrderViewModel, navController: NavController
) {
}
*/


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalOrder(
    inventoryViewModel: InventoryViewModel = hiltViewModel(),
    customersViewModel: CustomersViewModel = hiltViewModel(),
    rentalOrderViewModel: RentalOrderViewModel = hiltViewModel(),
    navController: NavController
) {
    // Collect state values using collectAsState to observe the data in UI
    val customersState by rentalOrderViewModel.customerState.collectAsState()
    val inventoryState by rentalOrderViewModel.inventoryState.collectAsState()
    val productState by rentalOrderViewModel.prodState.collectAsState()
    val orderItems by rentalOrderViewModel.orderItems.collectAsState()
    val totalAmount by rentalOrderViewModel.totalAmount.collectAsState()
    val paidAmount by rentalOrderViewModel.paidAmount.collectAsState()
    val balanceAmount by rentalOrderViewModel.paidAmount.collectAsState()

    val selectedCustomer = rentalOrderViewModel.selectedCustomer.collectAsState().value
    val selectedProduct = rentalOrderViewModel.selectedProduct.collectAsState().value

    /*var price by remember { mutableLongStateOf(selectedProduct?.rentalPrice ?: 0) }
    var quantity by remember { mutableIntStateOf(0) }*/

    var price by remember { mutableStateOf(selectedProduct?.rentalPrice?.toString() ?: "0") }
    var quantity by remember { mutableStateOf("0") }
    var days by remember { mutableStateOf("0") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Spinners for customer and product
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CustomerSpinner(
                    customersState.data,
                    selectedCustomer = selectedCustomer,
                    onCustomerSelected = { rentalOrderViewModel.selectCustomer(it) },
                    modifier = Modifier.weight(1f)
                )

                ProductSpinner(
                    productState.data,
                    selectedProduct = selectedProduct,
                    onProductSelected = {
                        rentalOrderViewModel.selectProduct(it)
                        price = it.rentalPrice.toString()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Quantity and Rental Price Fields
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.4f)
                )

                OutlinedTextField(
                    value = days,
                    onValueChange = { days = it },
                    label = { Text("Days") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.4f)
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Rental Price") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.6f)
                )
            }

            // Add Product Button
            Button(
                onClick = {
                    rentalOrderViewModel.addOrderItem(
                        qty = quantity.toInt(),
                        days = quantity.toInt(),
                        price = price.toLong()
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Product")
            }

            // LazyColumn for Order Items
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp) // Limit height for scrollable list
                    .padding(vertical = 8.dp)
            ) {
                items(orderItems) { item ->
                    Text(
                        "${item.productName} - Qty: ${item.quantity} * Days: ${item.days}" +
                                " * ₹${item.price}"
                    )
                }
            }

            // Total Amount
            Text(
                "Total: ₹$totalAmount",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.End)
            )
        }

        // Fixed Paid Amount Field
        OutlinedTextField(
            value = paidAmount.toString(),
            onValueChange = { rentalOrderViewModel.updatePaidAmount(it.toLongOrNull() ?: 0) },
            label = { Text("Paid Amount") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )

        Text(
            text = "Balance amount: " + (totalAmount - paidAmount),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
        )

        // Fixed Save Order Button
        Button(
            onClick = { rentalOrderViewModel.saveRentalOrder() },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Text("Save Order")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownMenuBox(
    items: List<T>,
    selected: T?,
    onItemSelected: (T) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedItem = selected?.toString() ?: "Select $label"

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.menuAnchor(),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.toString()) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSpinner(
    customers: List<Customer>,
    selectedCustomer: Customer?, // Pass the default product if available
    onCustomerSelected: (Customer) -> Unit,
    modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) } // Controls dropdown visibility
    var selectedItem by remember { mutableStateOf(selectedCustomer ?: customers.firstOrNull()) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded } // Toggle dropdown visibility
    ) {
        OutlinedTextField(
            value = selectedItem?.name ?: "Select", // Show default text
            onValueChange = {},
            readOnly = true, // Makes the field uneditable
            label = { Text("Customer") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = modifier
                .menuAnchor()
                .wrapContentSize()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier
        ) {
            customers.forEach { customer ->
                DropdownMenuItem(
                    text = { Text(customer.name) },
                    onClick = {
                        selectedItem = customer // Update selected item
                        expanded = false // Close dropdown
                        onCustomerSelected(customer) // Notify parent about the selection
                    }
                )
            }
        }
    }
}





