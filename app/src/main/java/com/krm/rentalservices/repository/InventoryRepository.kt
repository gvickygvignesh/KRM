package com.krm.rentalservices.repository

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.krm.rentalservices.Resource
import com.krm.rentalservices.model.InventoryItem
import com.krm.rentalservices.model.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject


private const val FIREBASE_ITEMS_REF = "items"
private const val FIREBASE_INVENTORY_REF = "inventory"

class InventoryRepository @Inject constructor(private val firebaseFirestore: FirebaseFirestore) {
    private val TAG: String = "KRM tag"
    private val fbftDB = firebaseFirestore.collection(FIREBASE_ITEMS_REF)


    fun getProducts(): Flow<Resource<List<Product>>> = callbackFlow {
        trySend(Resource.Loading())

        val listenerRegistration: ListenerRegistration =
            fbftDB.firestore.collection(FIREBASE_ITEMS_REF).addSnapshotListener { value, error ->
                try {
                    if (value?.isEmpty == false) {
                        val itemsList = mutableStateListOf<Product>()
                        val list = value.documents
                        for (listItem in list) {
                            val item = Product(
                                listItem.get("id").toString(),
                                listItem.get("name").toString(),
                                listItem.get("rentalPrice") as Double,
                                listItem.get("description").toString(),
                                Timestamp.now()
                            )
                            itemsList.add(item)
                        }

                        trySend((Resource.Success(itemsList)))
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
    }


    fun addProduct(item: Product): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())
        val db = Firebase.firestore
        val newDocRef = db.collection("items").document()
        item.id = newDocRef.id

        newDocRef.set(item).addOnSuccessListener {
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

    fun updateProduct(item: Product): Flow<Resource<String>> = callbackFlow {
        val db = Firebase.firestore
        db.collection(FIREBASE_ITEMS_REF).document(item.id).set(item) //, SetOptions.merge()
            .addOnSuccessListener {
                trySend(Resource.Success(item.id))
                Log.d(TAG, "addItem: success ${item.id}")
            }.addOnFailureListener {
                trySend(Resource.Error(it.localizedMessage ?: "Unknown Error"))
            }
        awaitClose()
    }

    fun deleteProduct(id: String): Flow<Resource<String>> = callbackFlow {
        val db = Firebase.firestore
        db.collection(FIREBASE_ITEMS_REF).document(id).delete().addOnSuccessListener {
            trySend(Resource.Success(id))
            Log.d(TAG, "delete: success ${id}")
        }.addOnFailureListener {
            trySend(Resource.Error(it.localizedMessage ?: "Unknown Error"))
        }
        awaitClose()
    }

    fun getInvItems(): Flow<Resource<List<InventoryItem>>> = callbackFlow {
        trySend(Resource.Loading())

        val listenerRegistration: ListenerRegistration =
            fbftDB.firestore.collection(FIREBASE_INVENTORY_REF)
                .addSnapshotListener { value, error ->
                    try {
                        if (value?.isEmpty == false) {

                            val invItems = mutableStateListOf<InventoryItem>()
                            val documents = value.documents
                            for (document in documents) {
                                val invItem: InventoryItem =
                                    document.toObject(InventoryItem::class.java)!!
                                /* val item = InventoryItem(
                                     document.get("prodId").toString(),
                                     document.get("prodName").toString(),
                                     document.get("totCount") as Long,
                                     document.get("rentedCount") as Int,
                                     document.get("availCount") as Int,
                                     document.get("damagedCount") as Int,
                                     document.get("timeStamp") as Timestamp
                                 )*/
                                invItems.add(invItem)
                            }

                            trySend((Resource.Success(invItems)))
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
    }

    fun addInventoryItem(item: InventoryItem): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())
        val db = Firebase.firestore
        val newDocRef = db.collection(FIREBASE_INVENTORY_REF).document()

        newDocRef.set(item).addOnSuccessListener {
            trySend(Resource.Success(newDocRef.id))
            Log.d(TAG, "Inventory added: success ${newDocRef.id}")
        }.addOnFailureListener { e ->
            Log.d(TAG, "Error adding doc")
            if (e.message != null) {
                trySend(Resource.Error(e.message!!))
            }
        }

        awaitClose()
    }
}


