// ItemListScreen.kt
package com.krm.rentalservices.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.krm.rentalservices.Constants
import com.krm.rentalservices.RentalOrderState
import com.krm.rentalservices.model.RentalOrder
import com.krm.rentalservices.viewmodel.ERROR_INTERNET
import com.krm.rentalservices.viewmodel.RentalOrderViewModel
import com.krm.rentalservices.viewmodel.SUCCESS
import java.net.URLEncoder

@Composable
fun RentalOrderList(
    viewModel: RentalOrderViewModel = hiltViewModel(), navController: NavHostController
) {
    val state = viewModel.rentalOrderState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchRentalOrders()
    }

    when {
        state.value.isLoading -> {
            // Show ProgressBar in the center of the screen
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Log.e(TAG, "Sothanai: CircularProgressIndicator called")
                CircularProgressIndicator()
            }
        }

        state.value.success in listOf(SUCCESS, ERROR_INTERNET) -> {
            Log.e(TAG, "Sothanai: RentalOrderList called")
            RentalOrderList(state, viewModel, navController)
        }
    }
}

@Composable
fun RentalOrderList(
    state: State<RentalOrderState>,
    viewModel: RentalOrderViewModel,
    navController: NavHostController
) {
    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = {


                navController.navigate(Constants.ORDER_ROUTE)

//                navController.navigate("${Constants.ORDER_ROUTE}/{rentalOrderJson}")
            }, modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart, contentDescription = "Add"
            )
        }
    }) { innerPadding ->

        if (state.value.data.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center // Centers content inside the Box
            ) {
                Text(
                    text = "No orders available\nPlace new order by clicking Cart button",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Row(
                modifier = Modifier.padding(innerPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.value.data) { rentalOrder ->
                        RentalOrderCard(rentalOrder, viewModel, navController)
                    }
                }
            }
        }
    }
}


fun navigateToOrder(
    rentalOrder: RentalOrder,
    viewModel: RentalOrderViewModel,
    navController: NavHostController,
    isReturn: Boolean
) {
    if (isReturn) {
        rentalOrder.orderStatus = Constants.RETURNED_ORDER
    }
    viewModel.clearData()
    val route = run {
        val rentalOrderJson = URLEncoder.encode(Gson().toJson(rentalOrder), "UTF-8")
            .replace("+", "%20") // Fix space encoding
        "order?orderJson=$rentalOrderJson"
    }

    navController.navigate(route)
}

@Composable
fun RentalOrderCard(
    rentalOrder: RentalOrder,
    viewModel: RentalOrderViewModel,
    navController: NavHostController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        onClick = {

            /*viewModel.setOrderItemDTO(rentalOrder.orderItemList)
            viewModel.setPaymentsDTO(rentalOrder.paymentList)
            viewModel.setOtherChargesDTO(rentalOrder.otherChargesList)

            val customer = Customer(
                id = rentalOrder.customerId,
                name = rentalOrder.customerName,
                type = "",
                address = "",
                mobNo = "",
                idNo = "",
                idType = "",
                idPhoto = "",
                timeStamp = null
            )

            viewModel.selectCustomer(customer)*/

//            navigateToOrder(rentalOrder, viewModel, navController)

            /* val rentalOrderJson = URLEncoder.encode(Gson().toJson(rentalOrder), "UTF-8")
             "order?orderJson=$rentalOrderJson"*/

//            val rentalOrderJson = URLEncoder.encode(Gson().toJson(rentalOrder), "UTF-8")


//            navController.navigate(Constants.ORDER_ROUTE)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            // Customer Details
            Text(
                text = "Customer Name: ${rentalOrder.customerName}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Order status: ${rentalOrder.orderStatus}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Order date : ${rentalOrder.orderDate}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Total amount: ₹ ${rentalOrder.totalAmt}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Balance amount: ₹ ${rentalOrder.balanceAmt}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Edit and Delete Icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {
                    navigateToOrder(rentalOrder, viewModel, navController, false)
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = {
                    navigateToOrder(rentalOrder, viewModel, navController, true)
                }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}




