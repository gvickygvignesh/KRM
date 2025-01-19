package com.krm.rentalservices.repository

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.krm.rentalservices.Resource
import com.krm.rentalservices.model.Customer
import com.krm.rentalservices.model.RentalOrder
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RentalOrdersRepository @Inject constructor(private val firebaseFireStore: FirebaseFirestore) {
    private val fBRentalOrderRef = "rentalOrders"
    private val TAG: String = "KRM tag"
    private val fbFTDB = firebaseFireStore.collection(fBRentalOrderRef)


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


    fun addCustomer(customer: Customer): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())
        val db = Firebase.firestore
        val newDocRef = db.collection(fBRentalOrderRef).document()
        customer.id = newDocRef.id

        newDocRef.set(customer).addOnSuccessListener {
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

    fun deleteCustomer(id: String): Flow<Resource<String>> = callbackFlow {
        val db = Firebase.firestore
        db.collection("items").document(id).delete().addOnSuccessListener {
            trySend(Resource.Success(id))
            Log.d(TAG, "delete: success ${id}")
        }.addOnFailureListener {
            trySend(Resource.Error(it.localizedMessage ?: "Unknown Error"))
//                _inventoryState.value = Resource.Error(it.localizedMessage ?: "Unknown Error")
        }
        awaitClose()
    }

    fun fetchRentalOrdersFlow(): Flow<Resource<List<RentalOrder>>> = flow {
        emit(Resource.Loading())

        try {
            val snapshot = fbFTDB.firestore.collection(fBRentalOrderRef).get().await()
            val rentalOrders = snapshot.toObjects(RentalOrder::class.java)
            emit(Resource.Success(rentalOrders))

        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error fetching orders"))
        }
    }
}