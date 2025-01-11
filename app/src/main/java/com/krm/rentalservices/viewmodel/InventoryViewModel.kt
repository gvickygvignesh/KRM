package com.krm.rentalservices.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.krm.rentalservices.AddFireStoreState
import com.krm.rentalservices.InventoryState
import com.krm.rentalservices.ProdState
import com.krm.rentalservices.Resource
import com.krm.rentalservices.model.InventoryItem
import com.krm.rentalservices.model.Product
import com.krm.rentalservices.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(private val inventoryRepository: InventoryRepository) :
    ViewModel() {

    private var job: Job? = null

    private val _prodState = mutableStateOf(ProdState())
    val prodState: State<ProdState> = _prodState

    private val _addItemState = mutableStateOf(AddFireStoreState())
    val addItemState: State<AddFireStoreState> = _addItemState

    private val _invState = mutableStateOf(InventoryState())
    val invState: State<InventoryState> = _invState

    private val _selectedItem = MutableStateFlow<Product?>(null)
    val selectedItem: StateFlow<Product?> = _selectedItem

    init {
        getProducts()
        getInvItems()
    }


    private fun getProducts() {
        job?.cancel()
        job = viewModelScope.launch {
            inventoryRepository.getProducts().onEach { result ->
                when (result) {
                    is Resource.Error ->
                        _prodState.value = ProdState(
                            isLoading = false,
                            internet = false,
                            success = ERROR_HTTP
                        )

                    is Resource.Internet -> {
                        _prodState.value = ProdState(
                            isLoading = false,
                            internet = true,
                            success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _prodState.value = ProdState(
                            isLoading = true,
                            internet = false
                        )
                    }

                    is Resource.Success -> {
                        _prodState.value = ProdState(
                            isLoading = false,
                            internet = false,
                            success = SUCCESS,
                            data = result.data!!
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun addProduct(name: String, desc: String, price: Double, timestamp: Timestamp) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            val newItem =
                Product(name = name, description = desc, rentalPrice = price, timestamp = timestamp)
            inventoryRepository.addProduct(newItem).collectLatest { result ->
                when (result) {
                    is Resource.Error ->
                        _addItemState.value = AddFireStoreState(
                            isLoading = false,
                            internet = false,
                            success = ERROR_HTTP
                        )

                    is Resource.Internet -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = false,
                            internet = true,
                            success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = true,
                            internet = false
                        )
                    }

                    is Resource.Success -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = false,
                            internet = false,
                            success = SUCCESS,
                            data = result.data!!
                        )
                    }
                }
            }
        }

    }

    fun updateProduct(item: Product) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            inventoryRepository.updateProduct(item).collectLatest { result ->
                when (result) {
                    is Resource.Error ->
                        _addItemState.value = AddFireStoreState(
                            isLoading = false,
                            internet = false,
                            success = ERROR_HTTP
                        )

                    is Resource.Internet -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = false,
                            internet = true,
                            success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = true,
                            internet = false
                        )
                    }

                    is Resource.Success -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = false,
                            internet = false,
                            success = SUCCESS,
                            data = result.data!!
                        )
                    }
                }
            }
        }
    }

    fun deleteProduct(item: Product) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            inventoryRepository.deleteProduct(item.id).collectLatest { result ->
                when (result) {
                    is Resource.Error ->
                        _addItemState.value = AddFireStoreState(
                            isLoading = false,
                            internet = false,
                            success = ERROR_HTTP
                        )

                    is Resource.Internet -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = false,
                            internet = true,
                            success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = true,
                            internet = false
                        )
                    }

                    is Resource.Success -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = false,
                            internet = false,
                            success = SUCCESS,
                            data = result.data!!
                        )
                    }
                }
            }
        }
    }

    fun selectItem(item: Product) {
        _selectedItem.value = item
    }

    fun clearSelectedItem() {
        _selectedItem.value = null
    }

    private fun getInvItems() {
        job?.cancel()
        job = viewModelScope.launch {
            inventoryRepository.getInvItems().onEach { result ->
                when (result) {
                    is Resource.Error ->
                        _invState.value = InventoryState(
                            isLoading = false,
                            internet = false,
                            success = ERROR_HTTP
                        )

                    is Resource.Internet -> {
                        _invState.value = InventoryState(
                            isLoading = false,
                            internet = true,
                            success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _invState.value = InventoryState(
                            isLoading = true,
                            internet = false
                        )
                    }

                    is Resource.Success -> {
                        _invState.value = InventoryState(
                            isLoading = false,
                            internet = false,
                            success = SUCCESS,
                            data = result.data!!
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun addInventoryItem(
        id: String,
        name: String,
        totCount: Int,
        rentedCount: Int,
        avlCount: Int,
        damagedCount: Int,
        timestamp: Timestamp
    ) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            val newItem =
                InventoryItem(id, name, totCount, rentedCount, avlCount, damagedCount, timestamp)
            inventoryRepository.addInventoryItem(newItem).collectLatest { result ->
                when (result) {
                    is Resource.Error ->
                        _addItemState.value = AddFireStoreState(
                            isLoading = false,
                            internet = false,
                            success = ERROR_HTTP
                        )

                    is Resource.Internet -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = false,
                            internet = true,
                            success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = true,
                            internet = false
                        )
                    }

                    is Resource.Success -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = false,
                            internet = false,
                            success = SUCCESS,
                            data = result.data!!
                        )
                    }
                }
            }
        }
    }
}


const val INITIAL = 0
const val SUCCESS = 1
const val ERROR_HTTP = -1
const val ERROR_INTERNET = -2
