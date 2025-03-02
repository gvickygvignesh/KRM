package com.krm.rentalservices.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krm.rentalservices.Constants
import com.krm.rentalservices.model.OrderItem
import com.krm.rentalservices.viewmodel.RentalOrderViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemRentalOrderDialog(
//    navController: NavController,
//    isEditOrderItem: Boolean,
    context: Context, onDismiss: () -> Unit,
    rentalOrderViewModel: RentalOrderViewModel
) {

    val productState by rentalOrderViewModel.prodState.collectAsState()
    val selectedProduct = rentalOrderViewModel.selectedProduct.collectAsState().value
    val rentalOrder by rentalOrderViewModel.rentalOrder.collectAsStateWithLifecycle()
    val orderItems by rentalOrderViewModel.orderItemsDTO.collectAsStateWithLifecycle()

    var rentalPrice by remember { mutableStateOf(selectedProduct?.rentalPrice?.toString() ?: "") }
    var quantity by remember { mutableStateOf("") }
    var rtnQuantity by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("") }
    var isProductPresent by remember { mutableStateOf(false) }

    //If clicked from order list
    val orderItem: OrderItem? = orderItems
        .find { it.productId == selectedProduct?.id }
    if (orderItem != null) {
        quantity = orderItem.quantity.toString()
        rtnQuantity = if (orderItem.rtnQty.toString() == "0") "" else orderItem.rtnQty.toString()
        days = orderItem.days.toString()
        rentalPrice = orderItem.rentalPrice.toString()
        isProductPresent = true
    } else {
        days = ""
        quantity = ""
        rtnQuantity = ""
    }

    BasicAlertDialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnClickOutside = false)
//            title = { Text("Enter Details") }
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
//                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium

            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ProductSpinner(
                        productState.data,
                        selectedProduct = selectedProduct,
                        onProductSelected = { product ->
                            rentalOrderViewModel.selectProduct(product)

                            val orderItem: OrderItem? = orderItems
                                .find { it.productId == product.id }
                            if (orderItem != null) {
                                quantity = orderItem.quantity.toString()
                                rtnQuantity =
                                    if (orderItem.rtnQty.toString() == "0") "" else orderItem.rtnQty.toString()
                                days = orderItem.days.toString()
                                rentalPrice = orderItem.rentalPrice.toString()
                                isProductPresent = true
                            } else {
                                days = ""
                                quantity = ""
                                rtnQuantity = ""
                                rentalPrice = product.rentalPrice.toString()
                                isProductPresent = false

                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    /*if (isEditOrderItem){

                    }*/

                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                    modifier = Modifier.weight(0.4f)
                    )

                    if (rentalOrder?.orderStatus == Constants.RETURNED_ORDER) {
                        OutlinedTextField(
                            value = rtnQuantity,
                            onValueChange = { rtnQuantity = it },
                            label = { Text("Rtn Qty") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                    modifier = Modifier.weight(0.4f)
                        )
                    }

                    OutlinedTextField(
                        value = days,
                        onValueChange = { days = it },
                        label = { Text("Days") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                    modifier = Modifier.weight(0.4f)
                    )

                    OutlinedTextField(
                        value = rentalPrice,
                        onValueChange = { rentalPrice = it },
                        label = { Text("Rental Price") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                    modifier = Modifier.weight(0.6f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,

                    ) {
                    Button(onClick = {
                        if (validateInput(
                                rtnQuantity = rtnQuantity,
                                quantity = quantity,
                                days = days,
                                rentalPrice = rentalPrice,
                                isReturnOrder = rentalOrder?.orderStatus == Constants.RETURNED_ORDER
                            )
                        ) {
                            rentalOrderViewModel.addOrUpdateOrderItem(
                                qty = quantity.toInt(),
                                rtnQty = if (rentalOrder?.orderStatus == Constants.RETURNED_ORDER) rtnQuantity.toInt() else 0,
                                days = days.toInt(),
                                rentalPrice = rentalPrice.toLong(),
                                amount = quantity.toInt() * days.toInt() * rentalPrice.toLong(),
                                isProductPresent = isProductPresent
                            )

                            rentalPrice = ""
                            quantity = ""
                            rtnQuantity = ""
                            days = ""
                            isProductPresent = false

                            onDismiss()
                        } else {
                            Toast.makeText(context, "Provide all data", Toast.LENGTH_SHORT).show()
                        }
                    }
                    ) {
                        Text(if (isProductPresent) "Update" else "Add")
                    }

                    Button(onClick = {
                        rentalPrice = ""
                        quantity = ""
                        rtnQuantity = ""
                        days = ""
                        isProductPresent = false
                        rentalOrderViewModel.selectProduct(null)
                        onDismiss()
                    }
                        //                        , modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel")
                    }
                }/*,
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            })*/
            }
        }
    }
}

fun clearData() {

}

fun validateInput(
    quantity: String, rtnQuantity: String, days: String, rentalPrice: String, isReturnOrder: Boolean
): Boolean {
    return !(quantity.isEmpty() || days.isEmpty() || rentalPrice.isEmpty() || (rtnQuantity.isEmpty() && isReturnOrder))
}