package com.krm.rentalservices.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.krm.rentalservices.BottomNavItem
import com.krm.rentalservices.BottomNavigationBar
import com.krm.rentalservices.Constants
import com.krm.rentalservices.viewmodel.InventoryViewModel
import com.krm.rentalservices.viewmodel.RentalOrderViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(inventoryViewModel: InventoryViewModel) {
    val navController = rememberNavController()
    val rentalOrderViewModel: RentalOrderViewModel = hiltViewModel()

    // List of routes where the bottom bar should be hidden
    val hideBottomBarRoutes = listOf(
        Constants.ORDER_ROUTE,
        Constants.ADD_ITEM_RENTAL_ORDER_ROUTE,
        Constants.ADD_CHARGES_ROUTE,
        Constants.ADD_PAYMENT_ROUTE,
        Constants.ADD_PRODUCT,
        Constants.MANAGE_INV_ROUTE,
        Constants.ADD_CUSTOMER_ROUTE
    )

    // Determine the current route
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val isBottomBarVisible = currentRoute !in hideBottomBarRoutes
    val appBarTitle = when (currentRoute) {
        Constants.ORDER_ROUTE -> "Order Details"
        Constants.ADD_ITEM_RENTAL_ORDER_ROUTE -> "Add Rental Item"
        Constants.ADD_CHARGES_ROUTE -> "Add Charges"
        Constants.ADD_PAYMENT_ROUTE -> "Add Payment"
        Constants.ADD_PRODUCT -> "Add Product"
        Constants.MANAGE_INV_ROUTE -> "Manage Inventory"
        Constants.ADD_CUSTOMER_ROUTE -> "Add Customer"
        else -> null
    }

    Scaffold(
        topBar = {
            if (!isBottomBarVisible && appBarTitle != null) {
                TopAppBar(
                    title = { Text(text = appBarTitle) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        bottomBar = {
            if (isBottomBarVisible) {
                BottomNavigationBar(navController)
            }
        }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.OrderList.route,
            Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.OrderList.route) {
                RentalOrderList(navController = navController)
            }
            composable(Constants.ORDER_ROUTE) {
                RentalOrder(
                    navController = navController, rentalOrderViewModel = rentalOrderViewModel
                )
            }
            composable(Constants.ADD_ITEM_RENTAL_ORDER_ROUTE) {
                AddItemRentalOrderOverlay(
                    navController = navController, onDismiss = {
                        navController.popBackStack()
                    }, rentalOrderViewModel = rentalOrderViewModel
                )
            }

            composable(Constants.ADD_CHARGES_ROUTE) {
                AddCharges(
                    rentalOrderViewModel = rentalOrderViewModel, navController = navController
                )
            }

            composable(Constants.ADD_PAYMENT_ROUTE) {
                AddPayment(
                    rentalOrderViewModel = rentalOrderViewModel, navController = navController
                )
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