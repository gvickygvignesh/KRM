// ItemListScreen.kt
package com.krm.rentalservices.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.krm.rentalservices.Constants
import com.krm.rentalservices.RentalOrderState
import com.krm.rentalservices.model.RentalOrder
import com.krm.rentalservices.viewmodel.ERROR_INTERNET
import com.krm.rentalservices.viewmodel.RentalOrderViewModel
import com.krm.rentalservices.viewmodel.SUCCESS

@Composable
fun RentalOrderList(
    viewModel: RentalOrderViewModel = hiltViewModel(),
    navController: NavHostController
) {
    //    val items = viewModel.itemsFlow.collectAsState(initial = emptyList()).value // Collect items
    val state = viewModel.rentalOrderState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    when (state.value.success) {
        SUCCESS, ERROR_INTERNET -> {
            RentalOrderList(state, viewModel, navController)
        }
    }

    RentalOrderList(state, viewModel, navController)
    /*  when (state.isLoading) {
          true -> {
              CircularProgressIndicator()
          }

          false -> TODO()
      }*/


}

@Composable
fun RentalOrderList(
    state: State<RentalOrderState>,
    viewModel: RentalOrderViewModel,
    navController: NavHostController
) {


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Constants.ORDER_ROUTE)
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Add"
                )
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier.padding(innerPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.value.data) { rentalOrder ->
                    RentalOrderCard(rentalOrder, {}, {})
                }
            }
        }
    }
}


@Composable
fun RentalOrderCard(rentalOrder: RentalOrder, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                text = "Order date: ${rentalOrder.orderDate}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Total amount: ${rentalOrder.totalAmt}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Balance amount: ${rentalOrder.balanceAmt}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Edit and Delete Icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun CustomerCardListPreview() {
    val sampleCustomers = listOf(
        Customer(
            "John Doe",
            "Regular",
            "123 Street, City",
            "9876543210",
            "1234",
            "Passport",
            "photo1.jpg",
            "",
            null
        ),
        Customer(
            "Jane Smith",
            "VIP",
            "456 Avenue, Town",
            "8765432109",
            "5678",
            "Driving License",
            "photo2.jpg",
            "",
            null
        )
    )


}*/




