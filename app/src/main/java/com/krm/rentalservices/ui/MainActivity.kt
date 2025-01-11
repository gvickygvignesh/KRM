package com.krm.rentalservices.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.krm.rentalservices.BottomNavItem
import com.krm.rentalservices.BottomNavigationBar
import com.krm.rentalservices.Constants
import com.krm.rentalservices.viewmodel.InventoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var inventoryViewModel: InventoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inventoryViewModel = ViewModelProvider(this)[InventoryViewModel::class.java]

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Column to hold both the Add Item UI and the List UI
                    /*Column {
                        AddItemScreen(viewModel = inventoryHiltViewModel)
                        ItemListScreen(viewModel = inventoryHiltViewModel)
                    }*/
                }
                MainScreen(inventoryViewModel)
            }
        }
    }
}

@Composable
fun MainScreen(inventoryViewModel: InventoryViewModel) {
    val navController = rememberNavController()

    Scaffold(bottomBar = { BottomNavigationBar(navController) }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Order.route,
            Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Order.route) {
                Order(inventoryViewModel, navController)
            }

            composable(BottomNavItem.ProdList.route) {
                ProductList(viewModel = inventoryViewModel, navController = navController)
            }
            composable(Constants.ADD_PRODUCT) {
                AddProduct(viewModel = inventoryViewModel, navController = navController)
            }

            composable(BottomNavItem.Inventory.route) {
                InventoryScreen(navController = navController)
            }
            composable(Constants.MANAGE_INV_ROUTE) {
                ManageInventory(navController = navController)
            }

            composable(BottomNavItem.CustomerDirectory.route) {
                CustomersList(navController = navController)
            }
            composable(Constants.ADD_CUSTOMER_ROUTE) {
                AddCustomer(navController = navController)
            }

            composable(BottomNavItem.MoreOptions.route) {
                MoreOptions(navController = navController)
            }


        }
    }
}