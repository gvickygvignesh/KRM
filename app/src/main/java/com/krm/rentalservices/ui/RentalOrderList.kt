// ItemListScreen.kt
package com.krm.rentalservices.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.krm.rentalservices.Constants
import com.krm.rentalservices.R
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
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        onClick = {
            if (rentalOrder.orderStatus == Constants.RETURNED_ORDER) {
                navigateToOrder(rentalOrder, viewModel, navController, false)
            }
        }) {

        Row {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .weight(0.85f),
            ) {
                // Customer Details
                Text(
                    text = rentalOrder.customerName,
                    style = typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
//                horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = rentalOrder.orderStatus + " order",
                        style = typography.bodyMedium
                    )

                    Text(
                        text = "  ●  ",
                        style = typography.bodyMedium,
                        fontSize = 10.sp // Adjust size as needed
                    )


                    Text(
                        text = "${rentalOrder.getOrderDateAsDate()}",
                        style = typography.bodyMedium
                    )
                }

                Text(
                    text = "Total ₹ ${rentalOrder.totalAmt}",
                    style = typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = "Balance ₹ ${rentalOrder.balanceAmt}",
                    style = typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(0.15f)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically),
            ) {
                if (rentalOrder.orderStatus != Constants.RETURNED_ORDER) {
                    IconButton(onClick = {
                        navigateToOrder(rentalOrder, viewModel, navController, false)
                    }, modifier = Modifier.clip(RoundedCornerShape(0.dp))) {
                        Icon(
                            painter = painterResource(R.drawable.edit),
//                            imageVector = R.drawable.edit,
                            contentDescription = "Edit",
//                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = {
                        navigateToOrder(rentalOrder, viewModel, navController, true)
                    }, modifier = Modifier.clip(RoundedCornerShape(0.dp))) {
                        Icon(
                            painter = painterResource(R.drawable.returnorder),
                            contentDescription = "Return",
//                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    Icon(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                        painter = painterResource(R.drawable.closed),
                        contentDescription = "closed"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun prevBtn() {
    val sampleOrder = RentalOrder(
        customerName = "John Doe",
        orderStatus = "Pending",
        orderDate = 1740943287381,
        totalAmt = 2500,
        balanceAmt = 500
    )
//    ShuffleButton(sampleOrder)
    /*val mockViewModel: RentalOrderViewModel = hiltViewModel()
    val mockNavController = NavHostController(LocalContext.current)

    RentalOrderCard(
        rentalOrder = sampleOrder,
        viewModel = mockViewModel,
        navController = mockNavController
    )*/
}

//@Preview(showBackground = true)
@Composable
fun PreviewRentalOrderCard() {
    MaterialTheme {
        val sampleOrder = RentalOrder(
            customerName = "John Doe",
            orderStatus = "Pending",
            orderDate = 1740943287381,
            totalAmt = 2500,
            balanceAmt = 500
        )

        val mockViewModel: RentalOrderViewModel = hiltViewModel()
        val mockNavController = NavHostController(LocalContext.current)

        /*RentalOrderCard(
            rentalOrder = sampleOrder,
            viewModel = mockViewModel,
            navController = mockNavController
        )*/
    }
}







