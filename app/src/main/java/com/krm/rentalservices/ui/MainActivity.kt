package com.krm.rentalservices.ui

import OrderPDFViewerScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.krm.rentalservices.BottomNavItem
import com.krm.rentalservices.BottomNavigationBar
import com.krm.rentalservices.Constants
import com.krm.rentalservices.model.Customer
import com.krm.rentalservices.model.RentalOrder
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
                }
                MainScreen(inventoryViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(inventoryViewModel: InventoryViewModel) {
    var titleName by remember { mutableStateOf("") }
    var isTopBarVisible by remember { mutableStateOf(true) }

    val navController = rememberNavController()
    var rentalOrderViewModel: RentalOrderViewModel = hiltViewModel()

    // List of routes where the bottom bar should be hidden
    val hideBottomBarRoutes = listOf(
        "${Constants.ORDER_ROUTE}?orderJson={rentalOrderJson}",
        Constants.ADD_CHARGES_ROUTE,
        Constants.ADD_PAYMENT_ROUTE,
        Constants.ADD_PRODUCT,
        "${Constants.MANAGE_INV_ROUTE}?prodId={selectedProdId}",
        "${Constants.ADD_CUSTOMER_ROUTE}?customerJson={customerJson}",
        Constants.PREVIEW_PDF
    )

    // Determine the current route
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val isBottomBarVisible = currentRoute !in hideBottomBarRoutes
    val appBarTitle = when (currentRoute) {
        Constants.ORDER_ROUTE -> "Order Details"
        Constants.ADD_CHARGES_ROUTE -> "Add Charges"
        Constants.ADD_PAYMENT_ROUTE -> "Add Payment"
        Constants.ADD_PRODUCT -> "Add Product"
        Constants.MANAGE_INV_ROUTE -> "Manage Inventory"
        Constants.ADD_CUSTOMER_ROUTE -> "Add Customer"
        else -> null
    }

    val bottomBarHeight = 56.dp // Fixed height of BottomNavigationBar
    val animatedHeight by animateDpAsState(
        targetValue = if (isBottomBarVisible) bottomBarHeight else 0.dp, label = ""
    )

    Scaffold(topBar = {
        AnimatedVisibility(
            visible = isTopBarVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = titleName.ifEmpty { "KRM" },
                        color = Color.White
                    )
                },
                modifier = Modifier.heightIn(max = 56.dp),
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
            )
        }
    },
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                BottomNavigationBar(navController)
            }
        }) { innerPadding ->
//        Column(modifier = Modifier.padding(innerPadding)) {
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.OrderList.route,
            Modifier.padding(innerPadding)
        ) {

            composable(BottomNavItem.OrderList.route) {
                isTopBarVisible = true

                RentalOrderList(navController = navController)
            }
            composable(
                "${Constants.ORDER_ROUTE}?orderJson={rentalOrderJson}",
                enterTransition = { slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 2 }) + fadeOut() }
            ) { backStackEntry ->
                isTopBarVisible = false

                val rentalOrderJson = backStackEntry.arguments?.getString("rentalOrderJson")
                val rentalOrder =
                    rentalOrderJson?.let { Gson().fromJson(it, RentalOrder::class.java) }

                rentalOrderViewModel = hiltViewModel()
                RentalOrder(
                    navController = navController,
                    rentalOrderViewModel = rentalOrderViewModel,
                    rentalOrder = rentalOrder
                )
            }

            composable(Constants.ADD_CHARGES_ROUTE) {
                isTopBarVisible = false
                AddCharges(
                    rentalOrderViewModel = rentalOrderViewModel, navController = navController
                )
            }

            composable(Constants.ADD_PAYMENT_ROUTE) {
                isTopBarVisible = false
                AddPayment(
                    rentalOrderViewModel = rentalOrderViewModel, navController = navController
                )
            }

            composable(Constants.PREVIEW_PDF) {
                OrderPDFViewerScreen(
                    rentalOrderViewModel = rentalOrderViewModel,
                    navController = navController
                )
            }

            composable(BottomNavItem.ProdList.route) {
                ProductList(invViewModel = inventoryViewModel, navController = navController)
            }
            /*composable(Constants.ADD_PRODUCT) {
                AddProductDialog(viewModel = inventoryViewModel) //, navController = navController)
            }*/

            composable(BottomNavItem.Inventory.route) {
                InventoryList(navController = navController)
            }
            composable(
                "${Constants.MANAGE_INV_ROUTE}?prodId={selectedProdId}",
            ) { backStackEntry ->
                val selectedProdId = backStackEntry.arguments?.getString("selectedProdId")
                ManageInventory(navController = navController, selectedProdId = selectedProdId)
            }

            composable(BottomNavItem.CustomerDirectory.route) {
                CustomersList(navController = navController)
            }
            composable(
                "${Constants.ADD_CUSTOMER_ROUTE}?customerJson={customerJson}"
            ) { it ->
                val customerJson = it.arguments?.getString("customerJson")
                val customer =
                    customerJson?.let { Gson().fromJson(it, Customer::class.java) }

                AddCustomer(navController = navController, selectedCustomer = customer)
            }

            composable(BottomNavItem.MoreOptions.route) {
                MoreOptions(navController = navController)
            }
        }
    }
}
