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
    var titleName by remember { mutableStateOf("") }
    var isTopBarVisible by remember { mutableStateOf(true) }

    val navController = rememberNavController()
    var rentalOrderViewModel: RentalOrderViewModel = hiltViewModel()

    // List of routes where the bottom bar should be hidden
    val hideBottomBarRoutes = listOf(
        "${Constants.ORDER_ROUTE}?orderJson={rentalOrderJson}",
//        Constants.ADD_ITEM_RENTAL_ORDER_ROUTE,
        Constants.ADD_CHARGES_ROUTE,
        Constants.ADD_PAYMENT_ROUTE,
        Constants.ADD_PRODUCT,
        Constants.MANAGE_INV_ROUTE,
        Constants.ADD_CUSTOMER_ROUTE,
        Constants.PREVIEW_PDF
    )

    // Determine the current route
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val isBottomBarVisible = currentRoute !in hideBottomBarRoutes
    val appBarTitle = when (currentRoute) {
        Constants.ORDER_ROUTE -> "Order Details"
//        Constants.ADD_ITEM_RENTAL_ORDER_ROUTE -> "Add Rental Item"
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

                /* var rentalOrder: RentalOrder? = null
                 // Decode and parse JSON
                 val json = backStackEntry.arguments?.getString("rentalOrderJson")
                     ?.let { URLDecoder.decode(it, "UTF-8") }
                 if (!json.isNullOrEmpty()) {
                     rentalOrder = Gson().fromJson(json, RentalOrder::class.java)
                 }*/

//                rentalOrderViewModel.setDataFetched(false)
                rentalOrderViewModel = hiltViewModel()
                RentalOrder(
                    navController = navController,
                    rentalOrderViewModel = rentalOrderViewModel,
                    rentalOrder = rentalOrder
                )
            }
            /*composable(Constants.ORDER_ROUTE,
                enterTransition = { slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 2 }) + fadeOut() }) {
                isTopBarVisible = false
                RentalOrder(
                    navController = navController, rentalOrderViewModel = rentalOrderViewModel
                )
            }*/
            /* composable(Constants.ADD_ITEM_RENTAL_ORDER_ROUTE) {
                 isTopBarVisible = false
                 AddItemRentalOrderOverlay(
                     navController = navController, onDismiss = {
                         navController.popBackStack()
                     }, rentalOrderViewModel = rentalOrderViewModel
                 )
             }*/

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
           /* composable(Constants.CHOOSE_CUSTOMER_ROUTE) {
                ChooseCustomerOrderOverlay(navController = navController, onDismiss = {
                    navController.popBackStack()
                }, rentalOrderViewModel = rentalOrderViewModel)
            }*/

            composable(BottomNavItem.MoreOptions.route) {
                MoreOptions(navController = navController)
            }


//            }
            /* // Spacer to prevent sudden UI shift
             Spacer(modifier = Modifier.height(animatedHeight))*/
        }
    }
}

/*import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson*/

/*val rentalOrderNavType = object : NavType<RentalOrder>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): RentalOrder? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): RentalOrder {
        return Gson().fromJson(value, RentalOrder::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: RentalOrder) {
        bundle.putParcelable(key, value)
    }
}*/
