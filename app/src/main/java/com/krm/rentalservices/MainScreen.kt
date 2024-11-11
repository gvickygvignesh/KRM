package com.krm.rentalservices

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(viewModel: InventoryViewModel, itemViewModel: ItemViewModel) {
    Log.d(TAG, "MainScreen: Called")
    val navController = rememberNavController()

//    ItemListScreen(viewModel)
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.ListItems.route,
            Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.ListItems.route) {
                ItemListScreen(viewModel)
            }
            composable(BottomNavItem.AddItem.route) {
                AddItemScreen(itemViewModel)
            }
            composable(BottomNavItem.Order.route) {
                SaleOrderScreen(viewModel)
            }
        }
    }
}
