package com.krm.rentalservices

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object OrderList : BottomNavItem("order_list", "Orders", Icons.Default.ExitToApp)
    object CustomerDirectory : BottomNavItem("cust_dir", "Customers", Icons.Default.AccountCircle)
    object Inventory : BottomNavItem("inventory", "Inventory", Icons.Default.Home)
    object ProdList : BottomNavItem("prod_list", "Products", Icons.Default.List)
    object MoreOptions : BottomNavItem("more_opt", "More", Icons.Default.MoreVert)

}
