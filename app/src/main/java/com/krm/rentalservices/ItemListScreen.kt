// ItemListScreen.kt
package com.krm.rentalservices

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val TAG: String = "Inspection"

@Composable
fun ItemListScreen(viewModel: InventoryViewModel) {
    Log.d(TAG, "ItemListScreen: called")
//    val items = viewModel.itemsFlow.collectAsState(initial = emptyList()).value // Collect items
    val state = viewModel.state.value

    when (state.success) {
        SUCCESS, ERROR_INTERNET -> {
            ShowResult(state)
        }
    }

    // LaunchedEffect with a key of Unit ensures this block only runs once on first composition
   /* LaunchedEffect(key1 = state.data.isEmpty()) {
        if (state.data.isEmpty()) {
            viewModel.getInventoryDetails()
        }
    }*/
}

@Composable
fun ShowResult(state: InventoryState) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(state.data) { item ->
            InventoryItemRow(item)
        }
    }
}

@Composable
fun InventoryItemRow(item: InventoryItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = item.name, style = MaterialTheme.typography.bodyLarge)
        Text(text = "Quantity: ${item.quantity}", style = MaterialTheme.typography.bodyLarge)
//        Text(text = "Price: $${item.rentalPrice}", style = MaterialTheme.typography.bodySmall)
        Divider(modifier = Modifier.padding(vertical = 4.dp))
    }
}
