package com.krm.rentalservices.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.krm.rentalservices.Constants
import com.krm.rentalservices.model.Customer
import com.krm.rentalservices.viewmodel.CustomersViewModel
import com.krm.rentalservices.viewmodel.ERROR_INTERNET
import com.krm.rentalservices.viewmodel.InventoryViewModel
import com.krm.rentalservices.viewmodel.RentalOrderViewModel
import com.krm.rentalservices.viewmodel.SUCCESS


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalOrder(
    inventoryViewModel: InventoryViewModel = hiltViewModel(),
    customersViewModel: CustomersViewModel = hiltViewModel(),
    rentalOrderViewModel: RentalOrderViewModel,
    navController: NavController
) {
    // Collect state values using collectAsState to observe the data in UI
    val customersState by rentalOrderViewModel.customerState.collectAsState()
    val inventoryState by rentalOrderViewModel.inventoryState.collectAsState()
    val productState by rentalOrderViewModel.prodState.collectAsState()
    val orderItems by rentalOrderViewModel.orderItems.collectAsState()
    val totalAmount by rentalOrderViewModel.totalAmount.collectAsState()
    val chargesAmt by rentalOrderViewModel.otherChargesTotalAmount.collectAsState()
    val paidAmount by rentalOrderViewModel.paidAmount.collectAsState()
//    val balanceAmount by rentalOrderViewModel.paidAmount.collectAsState()

    val selectedCustomer = rentalOrderViewModel.selectedCustomer.collectAsState().value
    val selectedProduct = rentalOrderViewModel.selectedProduct.collectAsState().value

    val state = rentalOrderViewModel.addRentalOrderState.value

    LaunchedEffect(Unit) {
        rentalOrderViewModel.fetchCustomers()
        rentalOrderViewModel.fetchInventory()
        rentalOrderViewModel.fetchProducts()
    }

    if (!state.isEventHandled) {
        when (state.success) {
            SUCCESS -> { //, ERROR_INTERNET
                Toast.makeText(
                    LocalContext.current,
                    state.data + " Order placed successfully",
                    Toast.LENGTH_LONG
                ).show()

                rentalOrderViewModel.clearData()
                rentalOrderViewModel.markEventHandled()
                rentalOrderViewModel.fetchInventory()
            }

            ERROR_INTERNET -> {
                Toast.makeText(
                    LocalContext.current,
                    state.data + " No internet connection",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    Scaffold(
        /*floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    rentalOrderViewModel.saveRentalOrder()
                },
                modifier = Modifier.padding(1.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Add"
                )
            }
        }*/
    ) { innerPadding ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {

            val (firstRow, listRow, balanceInfo, totalInfo, paidInfo, chargesRow, paymentRow) = createRefs()

            Column(
                modifier = Modifier
                    .constrainAs(firstRow) {
                        top.linkTo(parent.top, margin = 10.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(chargesRow.top)
                        height = Dimension.fillToConstraints
                    }
                    .fillMaxWidth(),
//                    .align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Spinners for customer and product

                Row(modifier = Modifier.fillMaxWidth()) {
                    CustomerSpinner(
                        customersState.data,
                        selectedCustomer = selectedCustomer,
                        onCustomerSelected = { rentalOrderViewModel.selectCustomer(it) },
                        modifier = Modifier.weight(0.8f)
                    )

                    IconButton(
                        modifier = Modifier
                            .weight(0.1f)
                            .align(Alignment.CenterVertically)
                            .fillMaxWidth(),
                        onClick = {
                            navController.navigate(Constants.ADD_ITEM_RENTAL_ORDER_ROUTE)
                        }) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Add Item",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(vertical = 8.dp)
                ) {
                    TableHeaderCell("Item", Modifier.weight(2f))
                    TableHeaderCell("Qty", Modifier.weight(0.8f))
                    TableHeaderCell("Days", Modifier.weight(0.8f))
                    TableHeadAmtCell("Price", Modifier.weight(1f))
                    TableHeadAmtCell("Amount", Modifier.weight(1.5f))
                }

                // LazyColumn for Order Items
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
//                        .heightIn(max = 200.dp) // Limit height for scrollable list
//                        .heightIn(max = 200.dp) // Limit height for scrollable list
                        .padding(vertical = 4.dp)
                ) {
                    items(orderItems) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableCell(item.productName, Modifier.weight(2f))
                            TableCell(item.quantity.toString(), Modifier.weight(0.8f))
                            TableCell(item.days.toString(), Modifier.weight(0.8f))
                            TableAmtCell("₹${item.rentalPrice}", Modifier.weight(1f))
                            TableAmtCell(
                                "₹${item.rentalPrice * item.days * item.quantity}",
                                Modifier.weight(1.5f)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.constrainAs(chargesRow) {
                    bottom.linkTo(totalInfo.top)
                }
            ) {
                Button(
                    onClick = {
                        navController.navigate(Constants.ADD_CHARGES_ROUTE)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text("Add charges")
                }

                Text(
                    "Charges: ₹$chargesAmt",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                        .wrapContentSize(Alignment.CenterEnd)
                        .align(Alignment.CenterVertically)
                )
            }

            // Total Amount
            Text(
                "Total: ₹$totalAmount",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .constrainAs(totalInfo) {
                        bottom.linkTo(paymentRow.top, margin = 5.dp)
                        end.linkTo(parent.end)
                    }
                    .padding(horizontal = 10.dp)
                    .wrapContentSize(Alignment.CenterEnd)

            )

            Row(
                modifier = Modifier.constrainAs(paymentRow) {
                    bottom.linkTo(balanceInfo.top)
                }
            ) {
                Button(
                    onClick = {
                        navController.navigate(Constants.ADD_PAYMENT_ROUTE)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text("Add payment")
                }

                Text(
                    "Paid Amount: ₹$paidAmount",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                        .wrapContentSize(Alignment.CenterEnd)
                        .align(Alignment.CenterVertically)
                )
            }

            Row(
                modifier = Modifier.constrainAs(balanceInfo) {
                    bottom.linkTo(parent.bottom)
//                    start.linkTo(parent.start)
                }
            ) {
                Button(
                    onClick = {
//                        navController.navigate(Constants.ADD_PAYMENT_ROUTE)
                        rentalOrderViewModel.saveRentalOrder("open", false)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .weight(1f)
                ) {
                    Text("Save Order")
                }

                Text(
                    text = "Balance : " + (totalAmount - paidAmount),
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                        .wrapContentSize(Alignment.CenterEnd)
                        .align(Alignment.CenterVertically)
                )
            }


            /* // Fixed Paid Amount Field
            OutlinedTextField(
                value = paidAmount.toString(),
                onValueChange = { rentalOrderViewModel.updatePaidAmount(it.toLongOrNull() ?: 0) },
                label = { Text("Paid Amount") },
                enabled = false,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .constrainAs(paidInfo) {
                        bottom.linkTo(balanceInfo.top)
                    }
                    .wrapContentSize()
            )*/

            /* Text(
                 text = "Balance amount: " + (totalAmount - paidAmount),
                 color = Color.Red,
                 modifier = Modifier
                     .constrainAs(balanceInfo) {
                         bottom.linkTo(parent.bottom)
                         start.linkTo(parent.start)
                     }
                     .fillMaxWidth()
             )*/

            /*// Fixed Save Order Button
            Button(
                onClick = { rentalOrderViewModel.saveRentalOrder() },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Text("Save Order")
            }*/
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

@Composable
fun TableHeaderCell(text: String, modifier: Modifier) {
    Text(
        text = text,
        modifier = modifier.padding(3.dp),
        color = Color.White,
        fontSize = 16.sp,
        textAlign = TextAlign.Left
    )
}

@Composable
fun TableCell(text: String, modifier: Modifier) {
    Text(
        text = text,
        modifier = modifier.padding(3.dp),
        fontSize = 14.sp,
        textAlign = TextAlign.Left
    )
}

@Composable
fun TableHeadAmtCell(text: String, modifier: Modifier) {
    Text(
        text = text,
        modifier = modifier.padding(3.dp),
        color = Color.White,
        fontSize = 16.sp,
        textAlign = TextAlign.Right
    )
}

@Composable
fun TableAmtCell(text: String, modifier: Modifier) {
    Text(
        text = text,
        modifier = modifier.padding(3.dp),
        fontSize = 14.sp,
        textAlign = TextAlign.Right
    )
}

fun clearStates() {

}






