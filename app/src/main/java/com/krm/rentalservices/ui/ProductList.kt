// ItemListScreen.kt
package com.krm.rentalservices.ui

import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.krm.rentalservices.ProdState
import com.krm.rentalservices.model.Product
import com.krm.rentalservices.viewmodel.ERROR_INTERNET
import com.krm.rentalservices.viewmodel.InventoryViewModel
import com.krm.rentalservices.viewmodel.SUCCESS

@Composable
fun ProductList(invViewModel: InventoryViewModel, navController: NavHostController) {
//    val items = viewModel.itemsFlow.collectAsState(initial = emptyList()).value // Collect items
    val state = invViewModel.prodState.collectAsState()
    var showDialog by remember { mutableStateOf(false) } // 🔹 Manage dialog state

    val delItemState = invViewModel.delItemState.value

    if (!delItemState.isEventHandled) {
        when (delItemState.success) {
            SUCCESS -> {
                Toast.makeText(
                    LocalContext.current, "Deleted Success", Toast.LENGTH_LONG
                ).show()
            }

            ERROR_INTERNET -> {
                Toast.makeText(
                    LocalContext.current, "Error occurred", Toast.LENGTH_LONG
                ).show()
            }
        }

        invViewModel.markDelEventHandled()
    }

    when (state.value.success) {
        SUCCESS, ERROR_INTERNET -> {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            invViewModel.clearSelectedProductItem()
                            showDialog = true
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        /*Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add"
                        )*/
                        Text("+ Add Item", modifier = Modifier.padding(16.dp))
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier.padding(innerPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.value.data) { item ->
                            ProductItemRow(item, invViewModel, navController, { showDialog = true })
                        }
                    }
                }

                if (showDialog) {
                    AddProductDialog(
                        viewModel = invViewModel,
                        context = LocalContext.current,
                        onDismiss = {
                            showDialog = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun ProdCardList(
    state: State<ProdState>,
    viewModel: InventoryViewModel,
    navController: NavHostController,
    showDialog: Boolean
) {

}

@Composable
fun ProductItemRow(
    product: Product,
    viewModel: InventoryViewModel,
    navController: NavHostController,
    showDialog: () -> Unit
) {

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        YesNoDialog(
            onDismissRequest = { showDeleteDialog = false },
            onConfirm = {
                // Handle Yes action
                viewModel.deleteProduct(product)
                showDeleteDialog = false
//                Toast.makeText(LocalContext.current, "Confirmed", Toast.LENGTH_SHORT).show()
            },
            onCancel = {
                // Handle No action
                showDeleteDialog = false
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Name: ${product.name}")
                Text("Price: ${product.rentalPrice}")
                Text("Description: ${product.description}")
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {
                    viewModel.setSelectProductItem(product)
                    showDialog()
//                    navController.navigate("add_prod")
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = {
                    showDeleteDialog = true
                }) {
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


@Composable
fun YesNoDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Are you sure?") },
        text = { Text("Do you want to proceed?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("No")
            }
        }
    )
}

