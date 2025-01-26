package com.krm.rentalservices

object Constants {
    val BASEURL: String = "https://sheets.googleapis.com/v4/spreadsheets/"
    val apiKey = "AIzaSyBw5Uc67R6HPMqTrkz_FPeQwmCFnRJjVmE"
    val spreadSheetID = "18BmJq3d7Jr2eT1SGcBQFnJyiLQ1xCKpOoETxijoN5iA"
    val invSheetName = "Inventory"
    val eqSheetName = "Equipments"

    const val MANAGE_INV_ROUTE = "manage_inv"
    const val ADD_CUSTOMER_ROUTE = "add_customer"
    const val ADD_PRODUCT = "add_prod"
//    const val ORDER_LIST_ROUTE = "order_list"
    const val ORDER_ROUTE = "order"
    const val ADD_ITEM_RENTAL_ORDER_ROUTE = "add_item_rental_order"
    const val ADD_PAYMENT_ROUTE = "add_payment"
    const val ADD_CHARGES_ROUTE = "add_charges"
}