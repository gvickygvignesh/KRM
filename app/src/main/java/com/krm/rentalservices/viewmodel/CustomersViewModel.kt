package com.krm.rentalservices.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.krm.rentalservices.AddFireStoreState
import com.krm.rentalservices.CustomerState
import com.krm.rentalservices.Resource
import com.krm.rentalservices.model.Customer
import com.krm.rentalservices.repository.CustomersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomersViewModel @Inject constructor(private val customersRepository: CustomersRepository) :
    ViewModel() {

    private var job: Job? = null

    //    private val _customerState = mutableStateOf(CustomerState())
    private val _customerState = MutableStateFlow(CustomerState())
    val customerState = _customerState.asStateFlow()

    private val _addCustomerState = mutableStateOf(AddFireStoreState())
    val addCustomerState: State<AddFireStoreState> = _addCustomerState

    private val _selectedItem = MutableStateFlow<Customer?>(null)
    val selectedItem: StateFlow<Customer?> = _selectedItem

    init {
        fetchCustomers()
    }

    // Flow to collect inventory items
    fun fetchCustomers() {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
//            val itemsFlow: Flow<Resource<List<InventoryItem>>> =
            customersRepository.fetchCustomers().onEach { result ->
                when (result) {
                    is Resource.Error ->
                        _customerState.value = CustomerState(
                            isLoading = false,
                            internet = false,
                            success = ERROR_HTTP
                        )

                    is Resource.Internet -> {
                        _customerState.value = CustomerState(
                            isLoading = false,
                            internet = true,
                            success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _customerState.value = CustomerState(
                            isLoading = true,
                            internet = false
                        )
                    }

                    is Resource.Success -> {
                        _customerState.value = CustomerState(
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


    fun addCustomer(
        name: String,
        type: String,
        address: String,
        mobNo: String,
        idNo: String,
        idType: String,
        idPhoto: String,
        timestamp: Timestamp
    ) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {

            val newCustomer = Customer(
                name = name,
                type = type,
                address = address,
                mobNo = mobNo,
                idNo = idNo,
                idType = idType,
                idPhoto = idPhoto,
                timeStamp = timestamp
            )
//            viewModelScope.launch {
            customersRepository.addCustomer(newCustomer).collectLatest { result ->
                when (result) {
                    is Resource.Error ->
                        _addCustomerState.value = AddFireStoreState(
                            isLoading = false,
                            internet = false,
                            success = ERROR_HTTP
                        )

                    is Resource.Internet -> {
                        _addCustomerState.value = AddFireStoreState(
                            isLoading = false,
                            internet = true,
                            success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _addCustomerState.value = AddFireStoreState(
                            isLoading = true,
                            internet = false
                        )
                    }

                    is Resource.Success -> {
                        _addCustomerState.value = AddFireStoreState(
                            isLoading = false,
                            internet = false,
                            success = SUCCESS,
                            data = result.data!!
                        )
                    }
                }

            }
//            }
        }
    }

    fun updateCustomer(item: Customer) {
        /*viewModelScope.launch {
            inventoryRepository.updateItemQuantity(item)
        }*/

        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
//            val updateItem = InventoryItem(name = name, quantity = quantity, rentalPrice = price)
//            viewModelScope.launch {
            customersRepository.updateCustomer(item).collectLatest { result ->
                when (result) {
                    is Resource.Error ->
                        _addCustomerState.value = AddFireStoreState(
                            isLoading = false,
                            internet = false,
                            success = ERROR_HTTP
                        )

                    is Resource.Internet -> {
                        _addCustomerState.value = AddFireStoreState(
                            isLoading = false,
                            internet = true,
                            success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _addCustomerState.value = AddFireStoreState(
                            isLoading = true,
                            internet = false
                        )
                    }

                    is Resource.Success -> {
                        _addCustomerState.value = AddFireStoreState(
                            isLoading = false,
                            internet = false,
                            success = SUCCESS,
                            data = result.data!!
                        )
                    }
                }

            }
//            }
        }
    }

    fun deleteCustomer(item: Customer) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            customersRepository.deleteCustomer(item.id).collectLatest { result ->
                when (result) {
                    is Resource.Error ->
                        _addCustomerState.value = AddFireStoreState(
                            isLoading = false,
                            internet = false,
                            success = ERROR_HTTP
                        )

                    is Resource.Internet -> {
                        _addCustomerState.value = AddFireStoreState(
                            isLoading = false,
                            internet = true,
                            success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _addCustomerState.value = AddFireStoreState(
                            isLoading = true,
                            internet = false
                        )
                    }

                    is Resource.Success -> {
                        _addCustomerState.value = AddFireStoreState(
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

    fun selectItem(item: Customer) {
        _selectedItem.value = item
    }

    fun clearSelectedItem() {
        _selectedItem.value = null
    }
}


/*const val INITIAL = 0
const val SUCCESS = 1
const val ERROR_HTTP = -1
const val ERROR_INTERNET = -2*/
