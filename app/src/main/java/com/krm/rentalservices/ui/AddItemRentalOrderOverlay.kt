package com.krm.rentalservices.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.krm.rentalservices.viewmodel.RentalOrderViewModel

@Composable
fun AddItemRentalOrderOverlay(
    navController: NavController, onDismiss: () -> Unit,
    rentalOrderViewModel: RentalOrderViewModel
) {

    val productState by rentalOrderViewModel.prodState.collectAsState()
    val selectedProduct = rentalOrderViewModel.selectedProduct.collectAsState().value

    var price by remember { mutableStateOf(selectedProduct?.rentalPrice?.toString() ?: "0") }
    var quantity by remember { mutableStateOf("0") }
    var days by remember { mutableStateOf("0") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)) // Semi-transparent background
            .clickable(
                onClick = onDismiss,
                indication = null,
                interactionSource = remember { MutableInteractionSource() })
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ProductSpinner(
                    productState.data,
                    selectedProduct = selectedProduct,
                    onProductSelected = {
                        rentalOrderViewModel.selectProduct(it)
                        price = it.rentalPrice.toString()
                    },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                    modifier = Modifier.weight(0.4f)
                )

                OutlinedTextField(
                    value = days,
                    onValueChange = { days = it },
                    label = { Text("Days") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                    modifier = Modifier.weight(0.4f)
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Rental Price") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                    modifier = Modifier.weight(0.6f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,

                ) {
                    Button(
                        onClick = {
                            rentalOrderViewModel.addOrderItem(
                                qty = quantity.toInt(),
                                days = quantity.toInt(),
                                price = price.toLong()
                            )
                            navController.popBackStack()
                        }

//                        , modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add")
                    }

                    Button(
                        onClick = {
                            navController.popBackStack()
                        }
//                        , modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel")
                    }
                }
                // Add Product Button

            }
        }
    }
}
