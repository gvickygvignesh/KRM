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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(private val inventoryRepository: InventoryRepository) :
    ViewModel() {

    private var job: Job? = null

    private val _prodState = MutableStateFlow(ProdState())
    val prodState = _prodState

    private val _addItemState = mutableStateOf(AddFireStoreState())
    val addItemState: State<AddFireStoreState> = _addItemState

    private val _delItemState = mutableStateOf(AddFireStoreState())
    val delItemState: State<AddFireStoreState> = _delItemState

    private val _invState = MutableStateFlow(InventoryState())
    val invState = _invState.asStateFlow()

    private val _selectedProductItem = MutableStateFlow<Product?>(null)
    val selectedProductItem: StateFlow<Product?> = _selectedProductItem

    private val _selectedInvItem = MutableStateFlow<InventoryItem?>(null)
    val selectedInvItem: StateFlow<InventoryItem?> = _selectedInvItem

    private val _inventoryItemDTO = MutableStateFlow(InventoryItem())
    val inventoryItemDTO: StateFlow<InventoryItem> = _inventoryItemDTO.asStateFlow()

    init {
        getProducts()
        getInvItems()
    }


    private fun getProducts() {
        job?.cancel()
        job = viewModelScope.launch {
            inventoryRepository.getProducts().onEach { result ->
                when (result) {
                    is Resource.Error -> _prodState.value = ProdState(
                        isLoading = false, internet = false, success = ERROR_HTTP
                    )

                    is Resource.Internet -> {
                        _prodState.value = ProdState(
                            isLoading = false, internet = true, success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _prodState.value = ProdState(
                            isLoading = true, internet = false
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

    fun addProduct(name: String, desc: String, price: Long, timestamp: Timestamp) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            val newItem =
                Product(name = name, description = desc, rentalPrice = price, timestamp = timestamp)
            inventoryRepository.addProduct(newItem).collectLatest { result ->
                when (result) {
                    is Resource.Error -> _addItemState.value = AddFireStoreState(
                        isLoading = false, internet = false, success = ERROR_HTTP
                    )

                    is Resource.Internet -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = false, internet = true, success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = true, internet = false
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
                    is Resource.Error -> _addItemState.value = AddFireStoreState(
                        isLoading = false, internet = false, success = ERROR_HTTP
                    )

                    is Resource.Internet -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = false, internet = true, success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = true, internet = false
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
                    is Resource.Error -> _delItemState.value = AddFireStoreState(
                        isLoading = false, internet = false, success = ERROR_HTTP
                    )

                    is Resource.Internet -> {
                        _delItemState.value = AddFireStoreState(
                            isLoading = false, internet = true, success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _delItemState.value = AddFireStoreState(
                            isLoading = true, internet = false
                        )
                    }

                    is Resource.Success -> {
                        _delItemState.value = AddFireStoreState(
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

    fun setSelectProductItem(item: Product?) {
        _selectedProductItem.value =  item
    }

    fun clearSelectedProductItem() {
        _selectedProductItem.value = null
    }

    fun setSelectInvItem(item: InventoryItem) {
        _selectedInvItem.update { item }
    }

    fun clearSelectedInvItem() {
        _selectedInvItem.value = null
    }

    fun markEventHandled() {
        _addItemState.value = _addItemState.value.copy(isEventHandled = true)
    }

    fun markDelEventHandled() {
        _delItemState.value = _delItemState.value.copy(isEventHandled = true)
    }

    private fun getInvItems() {
        job?.cancel()
        job = viewModelScope.launch {
            inventoryRepository.getInvItems().onEach { result ->
                when (result) {
                    is Resource.Error -> _invState.value = InventoryState(
                        isLoading = false, internet = false, success = ERROR_HTTP
                    )

                    is Resource.Internet -> {
                        _invState.value = InventoryState(
                            isLoading = false, internet = true, success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _invState.value = InventoryState(
                            isLoading = true, internet = false
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

    fun addOrUpdateInventoryItem(
        docId: String,
        prodId: String,
        name: String,
        totCount: Int,
        rentedCount: Int,
        avlCount: Int,
        damagedCount: Int,
        timestamp: Timestamp,
        isInvUpdate: Boolean,
        isDelete: Boolean
    ) {

        if (isDelete) {

        } else {

        }

        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            val newItem = InventoryItem(
                id = docId,
                prodId = prodId,
                prodName = name,
                totCount = totCount,
                rentedCount = rentedCount,
                avlCount = avlCount,
                damagedCount = damagedCount,
                timestamp = timestamp
            )
            inventoryRepository.addOrUpdateInventoryItem(newItem, isInvUpdate)
                .collectLatest { result ->
                    when (result) {
                        is Resource.Error -> _addItemState.value = AddFireStoreState(
                            isLoading = false, internet = false, success = ERROR_HTTP
                        )

                        is Resource.Internet -> {
                            _addItemState.value = AddFireStoreState(
                                isLoading = false, internet = true, success = ERROR_INTERNET
                            )
                        }

                        is Resource.Loading -> {
                            _addItemState.value = AddFireStoreState(
                                isLoading = true, internet = false
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

    fun deleteInventoryItem(
        inventoryItem: InventoryItem
    ) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {

            inventoryRepository.deleteInventoryItem(inventoryItem).collectLatest { result ->
                when (result) {
                    is Resource.Error -> _addItemState.value = AddFireStoreState(
                        isLoading = false, internet = false, success = ERROR_HTTP
                    )

                    is Resource.Internet -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = false, internet = true, success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _addItemState.value = AddFireStoreState(
                            isLoading = true, internet = false
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
