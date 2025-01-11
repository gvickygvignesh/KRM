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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.krm.rentalservices.CustomerState
import com.krm.rentalservices.model.Customer
import com.krm.rentalservices.viewmodel.CustomersViewModel
import com.krm.rentalservices.viewmodel.ERROR_INTERNET
import com.krm.rentalservices.viewmodel.SUCCESS

@Composable
fun CustomersList(viewModel: CustomersViewModel = hiltViewModel(), navController: NavHostController) {
    //    val items = viewModel.itemsFlow.collectAsState(initial = emptyList()).value // Collect items
    val state = viewModel.state.value
    var showDialog by remember { mutableStateOf(false) }

    when (state.success) {
        SUCCESS, ERROR_INTERNET -> {
            CustomerCardList(state, viewModel, navController)
        }
    }

    CustomerCardList(state, viewModel, navController)
    /*  when (state.isLoading) {
          true -> {
              CircularProgressIndicator()
          }

          false -> TODO()
      }*/


}

@Composable
fun CustomerCardList(
    state: CustomerState,
    viewModel: CustomersViewModel,
    navController: NavHostController
) {


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("add_customer")
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
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
                items(state.data) { customer ->
                    CustomerCard(customer, {}, {})
                }
            }
        }
    }
}



@Composable
fun CustomerCard(customer: Customer, onEdit: () -> Unit, onDelete: () -> Unit) {
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
                text = "Name: ${customer.name}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Type: ${customer.type}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Address: ${customer.address}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Mobile: ${customer.mobNo}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "ID Type: ${customer.idType}, ID No: ${customer.idNo}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Photo: ${customer.idPhoto}",
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

@Preview(showBackground = true)
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


}




