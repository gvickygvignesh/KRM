package com.krm.rentalservices.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.krm.rentalservices.Resource
import com.krm.rentalservices.model.Customer
import com.krm.rentalservices.model.InventoryItem
import com.krm.rentalservices.model.RentalOrder
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RentalOrdersRepository @Inject constructor(firebaseFireStore: FirebaseFirestore) {
    private val TAG: String = "KRM tag"
    private val fbFtDb = firebaseFireStore.collection(FB_RENTAL_ORDER_REF)


    /*fun fetchRentalOrders(): Flow<Resource<List<RentalOrder>>> = callbackFlow {
        trySend(Resource.Loading())

        val listenerRegistration: ListenerRegistration =
            fbFTDB.firestore.collection(fBRentalOrderRef).addSnapshotListener { value, error ->
                try {
                    if (value?.isEmpty == false) {
                        val custList = mutableStateListOf<RentalOrder>()
                        val list = value.documents
                        for (customer in list) {
                            val item = Customer(
                                customer.get("id").toString(),
                                customer.get("name").toString(),
                                customer.get("type").toString(),
                                customer.get("address").toString(),
                                customer.get("mobNo").toString(),
                                customer.get("idNo").toString(),
                                customer.get("idType").toString(),
                                customer.get("idPhoto").toString(),
                                customer.get("timeStamp") as Timestamp
                            )
                            custList.add(item)
                        }

                        trySend((Resource.Success(custList)))
                    }

                } catch (e: Exception) {
                    trySend(Resource.Error(e.message.toString()))
                }

                if (error != null) {
                    val errMsg = error.message
                    if (errMsg != null) {
                        trySend(Resource.Error(errMsg))
                    }
                }
            }

        awaitClose {
            listenerRegistration.remove() // Remove the listener when the flow is closed
        }
    }*/


    fun addRentalOrder(rentalOrder: RentalOrder): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())

        val newDocRef = fbFtDb.document()
        rentalOrder.orderId = newDocRef.id

        newDocRef.set(rentalOrder).addOnSuccessListener {
            trySend(Resource.Success(newDocRef.id))
            Log.d(TAG, "addItem: success ${newDocRef.id}")
        }.addOnFailureListener { e ->
            Log.d(TAG, "Error adding doc")
            if (e.message != null) {
                trySend(Resource.Error(e.message!!))
            }
        }

        awaitClose()
    }


    suspend fun createRentalOrderWithInventoryUpdate(rentalOrder: RentalOrder) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        try {
            db.runTransaction { transaction ->
                rentalOrder.orderItemList.forEach { orderItem ->
                    // Query InventoryItems collection and fetch the document snapshot
                    val inventoryQuerySnapshot = db.collection("InventoryItems")
                        .whereEqualTo("prodId", orderItem.productId)
                        .limit(1)
                        .get() // Query is outside the transaction


                    // Assuming there's only one document, get the first document reference
                    val document = inventoryQuerySnapshot.addOnSuccessListener() {
                        // Check if the query returned any documents
                        if (it.isEmpty) {
                            throw IllegalStateException("Inventory not found for productId: ${orderItem.productId}")
                        }
//                        for (doc in it.documents) {

                        val inventoryRef = it.documents[0].reference
                        val inventoryItem: InventoryItem =
                            it.documents[0].toObject(InventoryItem::class.java)
                                ?: throw IllegalStateException("Invalid Inventory data for productId: ${orderItem.productId}")

                        // Calculate updated counts
                        val updatedAvlCount = inventoryItem.avlCount - orderItem.quantity
                        val updatedRentedCount = inventoryItem.rentedCount + orderItem.quantity

                        // Ensure no negative stock
                        if (updatedAvlCount < 0) {
                            throw IllegalStateException("Insufficient stock for productId: ${orderItem.productId}")
                        }

                        // Apply updates to inventory
                        transaction.update(
                            inventoryRef, mapOf(
                                "avlCount" to updatedAvlCount,
                                "rentedCount" to updatedRentedCount
                            )
                        )
                    }
//                    }

                }

                // Add the RentalOrder after updating inventory
                val newOrderRef = db.collection("RentalOrders").document(rentalOrder.orderId)
                transaction.set(newOrderRef, rentalOrder)

                // Success
                null
            }.await() // Await transaction completion

            // Log success
            Log.d("RentalOrder", "Transaction success: Order created and inventory updated.")
        } catch (e: Exception) {
            // Log failure
            Log.w("RentalOrder", "Transaction failure: ${e.message}", e)
        }
    }


    fun updateCustomer(item: Customer): Flow<Resource<String>> = callbackFlow {
//    fun updateItemQuantity(item: InventoryItem) {

        // Update existing product
        val db = Firebase.firestore
//        val newDocRef = db.collection("items").document()

        db.collection("items").document(item.id).set(item) //, SetOptions.merge()
            .addOnSuccessListener {
                trySend(Resource.Success(item.id))
                Log.d(TAG, "addItem: success ${item.id}")
            }.addOnFailureListener {
                trySend(Resource.Error(it.localizedMessage ?: "Unknown Error"))
//                _inventoryState.value = Resource.Error(it.localizedMessage ?: "Unknown Error")
            }
        awaitClose()

//        fbftDB.
//        fbftDB.child(id).child("quantity").setValue(quanity)
    }

    suspend fun updateItemPrice(id: String, price: Int) {
//        fbftDB.child(id).child("price").setValue(price)
    }


    fun fetchRentalOrdersFlow(): Flow<Resource<List<RentalOrder>>> = flow {
        emit(Resource.Loading())

        try {
            val snapshot = fbFtDb.firestore.collection(FB_RENTAL_ORDER_REF).get().await()
            val rentalOrders = snapshot.toObjects(RentalOrder::class.java)
            emit(Resource.Success(rentalOrders))

        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error fetching orders"))
        }
    }

    companion object {
        private const val FB_RENTAL_ORDER_REF = "rentalOrders"
    }
}