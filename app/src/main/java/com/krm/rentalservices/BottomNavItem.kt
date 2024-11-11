package com.krm.rentalservices

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Order : BottomNavItem("order", "Order", Icons.Default.ShoppingCart)
    object ListItems : BottomNavItem("list_items", "List Items", Icons.Default.List)
    object AddItem : BottomNavItem("add_item", "Add Item", Icons.Default.Add)
}
