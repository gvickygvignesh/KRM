package com.krm.rentalservices.data

import com.krm.rentalservices.domain.SheetResponse
import retrofit2.Response
import kotlin.random.Random


class GoogleSheetRepo(private val api: GoogleSheetApi) : IGoogleSheetRepo {

    private val apiKey = "AIzaSyBw5Uc67R6HPMqTrkz_FPeQwmCFnRJjVmE"
    private val spreadSheetID = "18BmJq3d7Jr2eT1SGcBQFnJyiLQ1xCKpOoETxijoN5iA"
    private val invSheetName = "Inventory"
    private val eqSheetName = "Equipments"

    override suspend fun getInvInfo(): SheetResponse {
        return api.getInventoryItems(spreadSheetID, invSheetName, apiKey)
    }


    override suspend fun addInvItem(id: String, name: String, qty: Int): Response<Unit> {
        val requestBody = ItemRequestBody(
            range = eqSheetName,
            values = listOf(id + Random.nextInt(), name, qty.toString())
        )

        return api.addItem(
            spreadSheetID, eqSheetName, apiKey, "INSERT_ROWS",
            "USER_ENTERED", requestBody
        )
    }
}