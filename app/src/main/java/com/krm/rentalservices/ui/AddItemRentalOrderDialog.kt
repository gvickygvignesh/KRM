package com.krm.rentalservices.ui

import android.content.Context
import android.util.Log
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
import androidx.compose.ui.graphics.RectangleShape
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
    context: Context, onDismiss: () -> Unit,
    rentalOrderViewModel: RentalOrderViewModel
) {
    val productState by rentalOrderViewModel.prodState.collectAsState()
    val selectedProduct = rentalOrderViewModel.selectedProduct.collectAsState().value
    val rentalOrder by rentalOrderViewModel.selectedRentalOrder.collectAsStateWithLifecycle()
    val orderItems by rentalOrderViewModel.orderItemsDTO.collectAsStateWithLifecycle()
    val inventoryState by rentalOrderViewModel.inventoryState.collectAsStateWithLifecycle()

    var rentalPrice by remember { mutableStateOf(selectedProduct?.rentalPrice?.toString() ?: "") }
    var quantity by remember { mutableStateOf("") }
    var rtnQuantity by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("") }
    var avlCount by remember { mutableStateOf("") }
    var isProductPresent by remember { mutableStateOf(false) }

    //If clicked from order list
    val orderItem: OrderItem? = orderItems
        .find { it.productId == selectedProduct?.id }
    if (orderItem != null) {
        Log.d(TAG, "AddItemRentalOrderDialog: orderItemk not null called")
        quantity = orderItem.quantity.toString()
        rtnQuantity = if (orderItem.rtnQty.toString() == "0") "" else orderItem.rtnQty.toString()
        days = orderItem.days.toString()
        rentalPrice = orderItem.rentalPrice.toString()
        isProductPresent = true

        val inventoryItem = inventoryState.data.find { it.prodId == orderItem.productId }
        avlCount = inventoryItem?.avlCount.toString()
    } else {
        Log.d(TAG, "AddItemRentalOrderDialog: orderItemk else called")
        days = ""
        quantity = ""
        rtnQuantity = ""
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
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ProductSpinner(
                        productState.data,
                        selectedProduct = selectedProduct,
                        onProductSelected = { product ->
                            if (product.id.isNotEmpty()) {
                                rentalOrderViewModel.selectProduct(product)

                                val selectedOrderItem: OrderItem? = orderItems
                                    .find { it.productId == product.id }
                                if (selectedOrderItem != null) {
                                    quantity = selectedOrderItem.quantity.toString()
                                    rtnQuantity =
                                        if (selectedOrderItem.rtnQty.toString() == "0") "" else selectedOrderItem.rtnQty.toString()
                                    days = selectedOrderItem.days.toString()
                                    rentalPrice = selectedOrderItem.rentalPrice.toString()
                                    isProductPresent = true
                                } else {
                                    days = ""
                                    quantity = ""
                                    rtnQuantity = ""
                                    rentalPrice = product.rentalPrice.toString()
                                    isProductPresent = false
                                }
                                val inventoryItem =
                                    inventoryState.data.find { it.prodId == product.id }
                                avlCount = inventoryItem?.avlCount.toString()
                            } else {
                                days = ""
                                quantity = ""
                                rtnQuantity = ""
                                rentalPrice = ""
                                isProductPresent = false
                                avlCount = ""
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity", style = MaterialTheme.typography.labelLarge) },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                    modifier = Modifier.weight(0.4f)
                    )

                    if (rentalOrder?.orderStatus == Constants.RETURNED_ORDER) {
                        OutlinedTextField(
                            value = rtnQuantity,
                            onValueChange = { rtnQuantity = it },
                            label = { Text("Rtn Qty", style = MaterialTheme.typography.labelLarge) },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                    modifier = Modifier.weight(0.4f)
                        )
                    }

                    OutlinedTextField(
                        value = days,
                        onValueChange = { days = it },
                        label = { Text("Days", style = MaterialTheme.typography.labelLarge) },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                    modifier = Modifier.weight(0.4f)
                    )

                    OutlinedTextField(
                        value = rentalPrice,
                        onValueChange = { rentalPrice = it },
                        label = { Text("Rental Price", style = MaterialTheme.typography.labelLarge) },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                    modifier = Modifier.weight(0.6f)
                    )


                    if (avlCount.isNotEmpty()) {
                        Text("Stock available :$avlCount")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
//                        .weight(1f)
                    horizontalArrangement = Arrangement.SpaceEvenly,

                    ) {
                    Button(
                        onClick = {
                            if (validateInput(
                                    rtnQuantity = rtnQuantity,
                                    quantity = quantity,
                                    days = days,
                                    rentalPrice = rentalPrice,
                                    isReturnOrder = rentalOrder?.orderStatus == Constants.RETURNED_ORDER
                                )
                            ) {
                                val inventoryItem =
                                    rentalOrderViewModel.inventoryState.value.data.find { it.prodId == selectedProduct?.id }

                                var currItemAvlCount = 0
                                val thisOrderItem: OrderItem? =
                                    rentalOrder?.orderItemList?.find { it.productId == selectedProduct?.id }
                                if (inventoryItem != null) {
                                    currItemAvlCount =
                                        inventoryItem.avlCount //When order edit - adding existing order count to avail count stock to update
                                    if (thisOrderItem != null) {
                                        currItemAvlCount += thisOrderItem.quantity
                                    }
                                }

                                if (inventoryItem == null || currItemAvlCount < quantity.toInt()) {
                                    Toast.makeText(
                                        context,
                                        "$quantity pc(s) of stock not available in the inventory",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    rentalOrderViewModel.addOrUpdateOrderItem(
                                        qty = quantity.toInt(),
                                        rtnQty = if (rentalOrder?.orderStatus == Constants.RETURNED_ORDER) rtnQuantity.toInt() else 0,
                                        days = days.toInt(),
                                        rentalPrice = rentalPrice.toLong(),
                                        amount = quantity.toInt() * days.toInt() * rentalPrice.toLong(),
                                        isProductPresent = isProductPresent,
                                        isDelete = false
                                    )

                                    rentalPrice = ""
                                    quantity = ""
                                    rtnQuantity = ""
                                    days = ""
                                    isProductPresent = false

                                    onDismiss()
                                }
                            } else {
                                Toast.makeText(context, "Provide all data", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        modifier = Modifier
//                            .weight(1f)
                            .padding(2.dp),
                        shape = RectangleShape
                    ) {
                        Text(if (isProductPresent) "Update" else "Add")
                    }

                    if (isProductPresent)
                        Button(
                            onClick = {
                                rentalOrderViewModel.addOrUpdateOrderItem(
                                    qty = 0,
                                    rtnQty = 0,
                                    days = 0,
                                    rentalPrice = 0L,
                                    amount = 0,
                                    isProductPresent = isProductPresent,
                                    isDelete = true
                                )

                                onDismiss()
                            }, modifier = Modifier
//                                .weight(1f)
                                .padding(2.dp), shape = RectangleShape
                        ) {
                            Text("Delete")
                        }

                    Button(
                        onClick = {
                            rentalPrice = ""
                            quantity = ""
                            rtnQuantity = ""
                            days = ""
                            isProductPresent = false
                            rentalOrderViewModel.selectProduct(null)
                            onDismiss()
                        },
                        modifier = Modifier
//                            .weight(1f)
                            .padding(2.dp), shape = RectangleShape
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

fun validateInput(
    quantity: String, rtnQuantity: String, days: String, rentalPrice: String, isReturnOrder: Boolean
): Boolean {
    return !(quantity.isEmpty() || quantity == "0" || days.isEmpty() || days == "0" ||
            rentalPrice.isEmpty() || rentalPrice == "0" || (rtnQuantity.isEmpty() && isReturnOrder))
}