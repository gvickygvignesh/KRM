package com.krm.rentalservices.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.krm.rentalservices.Constants
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
    private val TAG: String = "Sothanai"
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


    fun createOrUpdateRentalOrderWithInventoryUpdate(
        rentalOrder: RentalOrder,
        isOrderUpdate: Boolean
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        try {
            // Preload inventory data before transaction
            val inventoryRefsAndData = mutableListOf<Pair<DocumentReference, InventoryItem>>()

            for (orderItem in rentalOrder.orderItemList) {
                val querySnapshot = db.collection(FIREBASE_INVENTORY_REF)
                    .whereEqualTo("prodId", orderItem.productId)
                    .limit(1)
                    .get()
                    .await()

                if (querySnapshot.isEmpty) {
                    emit(Resource.Error("Inventory not found for productId: ${orderItem.productId}"))
                    return@flow
                }

                val document = querySnapshot.documents[0]
                val inventoryItem = document.toObject(InventoryItem::class.java)
                    ?: throw IllegalStateException("Invalid Inventory data for productId: ${orderItem.productId}")
                inventoryRefsAndData.add(Pair(document.reference, inventoryItem))
            }

            // Firestore transaction
            db.runTransaction { transaction ->
                val orderRef = db.collection(FB_RENTAL_ORDER_REF).document(rentalOrder.orderId)
                val snapshot = transaction.get(orderRef)
                val orderExists = snapshot.exists()

                // Update inventory
                rentalOrder.orderItemList.forEach { orderItem ->
                    val (inventoryRef, inventoryItem) = inventoryRefsAndData.first { it.second.prodId == orderItem.productId }

                    val updatedAvlCount: Int
                    val updatedRentedCount: Int

                    if (rentalOrder.orderStatus == Constants.RETURNED_ORDER) {
                        updatedAvlCount = inventoryItem.avlCount + orderItem.rtnQty
                        updatedRentedCount = inventoryItem.rentedCount - orderItem.rtnQty

                        if (updatedRentedCount < 0) {
                            throw IllegalStateException("Rented stock qty cannot be negative for productId: ${orderItem.productId}")
                        }
                    } else {
                        updatedAvlCount = inventoryItem.avlCount - orderItem.quantity
                        updatedRentedCount = inventoryItem.rentedCount + orderItem.quantity

                        if (updatedAvlCount < 0) {
                            throw IllegalStateException("Insufficient stock for productId: ${orderItem.productId}")
                        }
                    }

                    transaction.update(inventoryRef, "avlCount", updatedAvlCount, "rentedCount", updatedRentedCount)
                }

                // Update or create order
                if (isOrderUpdate) {
                    if (orderExists) {
                        transaction.update(orderRef, mapOf(
                            "customerId" to rentalOrder.customerId,
                            "customerName" to rentalOrder.customerName,
                            "orderStatus" to rentalOrder.orderStatus,
                            "orderItemList" to rentalOrder.orderItemList,
                            "paymentList" to rentalOrder.paymentList,
                            "otherChargesList" to rentalOrder.otherChargesList,
                            "totalAmt" to rentalOrder.totalAmt,
                            "discountAmt" to rentalOrder.discountAmt,
                            "paidAmt" to rentalOrder.paidAmt,
                            "balanceAmt" to rentalOrder.balanceAmt,
                            "returnOrderDate" to rentalOrder.returnOrderDate,
                            "timestamp" to Timestamp.now()
                        ))
                    } else {
                        throw IllegalStateException("Order does not exist")
                    }
                } else {
                    transaction.set(orderRef, rentalOrder)
                }
            }.await()  // ✅ Ensure transaction completes before emitting success

            emit(Resource.Success(rentalOrder.orderId))

        } catch (e: Exception) {
            emit(Resource.Error("Transaction failed: ${e.message}"))
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


    fun fetchRentalOrders(): Flow<Resource<List<RentalOrder>>> = flow {
        emit(Resource.Loading())

        try {
            val snapshot = fbFtDb.firestore.collection(FB_RENTAL_ORDER_REF)
                .orderBy("timestamp", Query.Direction.DESCENDING).get().await()
            val rentalOrdersDTO = snapshot.toObjects(RentalOrder::class.java)
            emit(Resource.Success(rentalOrdersDTO))

        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error fetching orders"))
        }
    }

    companion object {
        private const val FB_RENTAL_ORDER_REF = "rentalOrders"
//        const val FIREBASE_RENTAL_ORDERS_REF = "RentalOrders"
    }
}