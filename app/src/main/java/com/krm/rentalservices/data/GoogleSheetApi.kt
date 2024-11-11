package com.krm.rentalservices.data

import com.krm.rentalservices.InventoryItem
import com.krm.rentalservices.domain.SheetResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleSheetApi {
    @GET("{spreadsheetId}/values/{sheetName}")
    suspend fun getInventoryItems(
        @Path("spreadsheetId") spreadsheetId: String,
        @Path("sheetName") sheetName: String,
        @Query("key") apiKey: String
    ): SheetResponse

    @POST("path-to-google-sheet-endpoint")
    suspend fun addInvItem(@Body item: InventoryItem)

    @POST("{spreadsheetId}/values/{sheetName}:append")
    suspend fun addItem(
        @Path("spreadsheetId") spreadsheetId: String,
        @Path("sheetName") sheetName: String,
        @Query("key") apiKey: String,
        @Query("insertDataOption") insertDataOption: String = "INSERT_ROWS",
        @Query("valueInputOption") valueInputOption: String = "USER_ENTERED",
        @Body requestBody: ItemRequestBody
    ): Response<Unit>

}