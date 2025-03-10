// InventoryList.kt
package com.krm.rentalservices.ui

import android.util.Log
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
import androidx.navigation.NavController
import com.google.gson.Gson
import com.krm.rentalservices.Constants
import com.krm.rentalservices.InventoryState
import com.krm.rentalservices.model.InventoryItem
import com.krm.rentalservices.viewmodel.ERROR_INTERNET
import com.krm.rentalservices.viewmodel.InventoryViewModel
import com.krm.rentalservices.viewmodel.SUCCESS
import java.net.URLEncoder

val TAG: String = "Sothanai"

@Composable
fun InventoryList(
    viewModel: InventoryViewModel = hiltViewModel(),
    navController: NavController
) {

    val state = viewModel.invState.collectAsState()

    when (state.value.success) {
        SUCCESS, ERROR_INTERNET -> {
            InventoryCardList(state, viewModel, navController)
        }
    }

    InventoryCardList(state, viewModel, navController)
}

@Composable
fun InventoryCardList(
    state: State<InventoryState>,
    viewModel: InventoryViewModel,
    navController: NavController
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("manage_inv")
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Manage Inventory"
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
                Log.d(TAG, "InventoryCardList: size" + state.value.data.size)
                items(state.value.data) { invItem ->
                    InventoryCardRow(invItem, viewModel, {
                        val route = run {
                            val prodId = invItem.prodId
                            "${Constants.MANAGE_INV_ROUTE}?prodId=$prodId"
                        }
                        navController.navigate(route)
                    })
                }
            }
        }
    }
}

@Composable
fun InventoryCardRow(
    inventoryItem: InventoryItem,
    viewModel: InventoryViewModel,
    onEdit: () -> Unit,
//    onDelete: () -> Unit
) {

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        YesNoDialog(
            onDismissRequest = { showDialog = false },
            onConfirm = {
                // Handle Yes action
                viewModel.deleteInventoryItem(inventoryItem)
                showDialog = false
//                Toast.makeText(LocalContext.current, "Confirmed", Toast.LENGTH_SHORT).show()
            },
            onCancel = {
                // Handle No action
                showDialog = false
//                Toast.makeText(LocalContext.current, "Canceled", Toast.LENGTH_SHORT).show()
            }
        )
    }

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
            Log.d(TAG, "InventoryCard: $inventoryItem")
            Text(
                text = "Name: ${inventoryItem.prodName}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Total Count: ${inventoryItem.totCount}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Rented Count: ${inventoryItem.rentedCount}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Available Count: ${inventoryItem.avlCount}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Damaged Count: ${inventoryItem.damagedCount}",
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
                IconButton(onClick = { showDialog = true }) {
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


