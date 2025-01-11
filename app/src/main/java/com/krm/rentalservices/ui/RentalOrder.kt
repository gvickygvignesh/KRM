// ItemListScreen.kt
package com.krm.rentalservices.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.krm.rentalservices.viewmodel.InventoryViewModel

@Composable
fun Order(viewModel: InventoryViewModel, navController: NavHostController) {
//    val items = viewModel.itemsFlow.collectAsState(initial = emptyList()).value // Collect items
    val state = viewModel.prodState.value
    var showDialog by remember { mutableStateOf(false) }

    Text(text = "Coming soon")
}

