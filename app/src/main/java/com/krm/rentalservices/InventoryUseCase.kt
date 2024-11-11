package com.krm.rentalservices

import com.krm.rentalservices.data.IGoogleSheetRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InventoryUseCase @Inject constructor(private val googleSheetRepo: IGoogleSheetRepo) {
    private val TAG: String = "KRM tag"
    private val _inventoryItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventoryItems: StateFlow<List<InventoryItem>> = _inventoryItems


    fun getItems(): Flow<Resource<List<InventoryItem>>> =
        flow {
            emit(Resource.Loading())
            val response = googleSheetRepo.getInvInfo()
            val headers = response.values.firstOrNull() ?: emptyList()

            val dataRows = response.values.drop(1)

            // Find indices for the keys we need
            val idIndex = headers.indexOf("id")
            val nameIndex = headers.indexOf("name")
            val quantityIndex = headers.indexOf("quantity")

            // Map each row to InventoryItem, only if it has the expected fields
            val inventoryItems = dataRows.mapNotNull { row ->
                val id = row.getOrNull(idIndex)
                val name = row.getOrNull(nameIndex)
                val quantity = row.getOrNull(quantityIndex)?.toIntOrNull()

                if (id != null && name != null && quantity != null) {
                    InventoryItem(id = id, name = name, quantity = quantity)
                } else {
                    null // Skip row if any field is missing
                }
            }

            emit(Resource.Success(inventoryItems))
        }

    fun addItem(item: InventoryItem): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            val response = googleSheetRepo.addInvItem(item.id, item.name, item.quantity)
            if (response.isSuccessful) {
                emit(Resource.Success("Success"))
            }else {
                emit(Resource.Error(response.message()))
            }
        }


    suspend fun updateItemQuantity(id: String, quanity: Int) {
//        fbftDB.
//        fbftDB.child(id).child("quantity").setValue(quanity)
    }

}