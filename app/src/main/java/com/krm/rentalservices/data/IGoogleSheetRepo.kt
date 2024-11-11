package com.krm.rentalservices.data

import com.krm.rentalservices.InventoryItem
import com.krm.rentalservices.domain.SheetResponse
import retrofit2.Response


interface IGoogleSheetRepo {
    suspend fun getInvInfo() : SheetResponse

    suspend fun addInvItem(
        id: String,
        name: String,
        qty: Int
    ) : Response<Unit>
}
