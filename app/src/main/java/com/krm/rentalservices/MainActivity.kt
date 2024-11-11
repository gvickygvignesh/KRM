package com.krm.rentalservices

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.krm.rentalservices.ui.theme.KRMRentalServicesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var inventoryViewModel: InventoryViewModel
    private lateinit var itemViewModel: ItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inventoryViewModel = ViewModelProvider(this)[InventoryViewModel::class.java]
        itemViewModel = ViewModelProvider(this)[ItemViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            KRMRentalServicesTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(viewModel = inventoryViewModel, itemViewModel = itemViewModel)
                    // Column to hold both the Add Item UI and the List UI
                    /* Column {
                         AddItemScreen(viewModel = inventoryViewModel)
                         ItemListScreen(viewModel = inventoryViewModel)
                     }*/
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KRMRentalServicesTheme {
        Greeting("Android")
    }
}