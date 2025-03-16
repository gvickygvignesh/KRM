package com.krm.rentalservices.ui

import OrderPDFViewerScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.krm.rentalservices.ui.theme.KRMRentalServicesTheme
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
            KRMRentalServicesTheme {
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
//    var isTopBarVisible by remember { mutableStateOf(true) }

    val navController = rememberNavController()
    var rentalOrderViewModel: RentalOrderViewModel = hiltViewModel()
    var showDialog by remember { mutableStateOf(false) }
    var onConfirmExit: (() -> Unit)? by remember { mutableStateOf(null) }

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
        "${Constants.ORDER_ROUTE}?orderJson={rentalOrderJson}" -> "Rental Order"
        Constants.ADD_CHARGES_ROUTE -> "Add Charges"
        Constants.ADD_PAYMENT_ROUTE -> "Add Payment"
        Constants.ADD_PRODUCT -> "Add Product"
        Constants.PREVIEW_PDF -> "Preview Invoice"
        "${Constants.MANAGE_INV_ROUTE}?prodId={selectedProdId}" -> "Manage Inventory"
        "${Constants.ADD_CUSTOMER_ROUTE}?customerJson={customerJson}" -> "Add Customer"
        else -> "KRM"
    }

    // Define screens that should have a back button
    val screensWithBackButton = listOf(
        "${Constants.ORDER_ROUTE}?orderJson={rentalOrderJson}",
        Constants.ADD_CHARGES_ROUTE,
        Constants.ADD_PAYMENT_ROUTE,
        Constants.PREVIEW_PDF,
        "${Constants.MANAGE_INV_ROUTE}?prodId={selectedProdId}",
        "${Constants.ADD_CUSTOMER_ROUTE}?customerJson={customerJson}"
    )

    // Check if back button is needed
    val showBackButton = currentRoute in screensWithBackButton

    // Define custom back button behavior
    val onBackPressed: () -> Unit = when (currentRoute) {
        "${Constants.ORDER_ROUTE}?orderJson={rentalOrderJson}" -> {
            {
                showDialog = true
                onConfirmExit = {
                    rentalOrderViewModel.clearData()
                    navController.popBackStack()
                }
            }
        }

        "${Constants.ADD_CUSTOMER_ROUTE}?customerJson={customerJson}" -> {
            {
                // Custom behavior for Add Customer screen
                navController.popBackStack()
            }
        }

        "${Constants.MANAGE_INV_ROUTE}?prodId={selectedProdId}" -> {
            {
                navController.popBackStack()
            }
        }

        else -> {
            { navController.popBackStack() }
        }
    }

    if (currentRoute == "${Constants.ORDER_ROUTE}?orderJson={rentalOrderJson}") {
        BackHandler(enabled = true) {
            onBackPressed()
        }
    }

    if (showDialog) {
        ConfirmExitDialog(
            onConfirm = {
                onConfirmExit?.invoke()
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    val bottomBarHeight = 56.dp // Fixed height of BottomNavigationBar
    val animatedHeight by animateDpAsState(
        targetValue = if (isBottomBarVisible) bottomBarHeight else 0.dp, label = ""
    )

    Scaffold(topBar = {
        AppBar(
            title = appBarTitle,
            showBackButton = showBackButton,
            onBackPressed = onBackPressed
        )
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
//                isTopBarVisible = true

                RentalOrderList(navController = navController)
            }
            composable(
                "${Constants.ORDER_ROUTE}?orderJson={rentalOrderJson}",
                enterTransition = { slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 2 }) + fadeOut() }
            ) { backStackEntry ->
//                isTopBarVisible = false

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
//                isTopBarVisible = false
                AddCharges(
                    rentalOrderViewModel = rentalOrderViewModel, navController = navController
                )
            }

            composable(Constants.ADD_PAYMENT_ROUTE) {
//                isTopBarVisible = false
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(title: String, showBackButton: Boolean, onBackPressed: () -> Unit) {
    TopAppBar(
        title = { Text(text = title, color = Color.White) },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        },
        modifier = Modifier.heightIn(max = 56.dp),
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun ConfirmExitDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Exit") },
        text = { Text("Are you sure you want to go back? Unsaved changes will be lost.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}