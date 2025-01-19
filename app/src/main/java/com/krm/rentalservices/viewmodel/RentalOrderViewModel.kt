package com.krm.rentalservices.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krm.rentalservices.CustomerState
import com.krm.rentalservices.InventoryState
import com.krm.rentalservices.ProdState
import com.krm.rentalservices.RentalOrderState
import com.krm.rentalservices.Resource
import com.krm.rentalservices.model.Customer
import com.krm.rentalservices.model.OrderItem
import com.krm.rentalservices.model.Product
import com.krm.rentalservices.repository.CustomersRepository
import com.krm.rentalservices.repository.InventoryRepository
import com.krm.rentalservices.repository.RentalOrdersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/*@HiltViewModel
class RentalOrderViewModel @Inject constructor(
    private val repository: InventoryRepository,
    private val customersRepository: CustomersRepository
) : ViewModel() {

}*/

@HiltViewModel
class RentalOrderViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    private val customersRepository: CustomersRepository,
    private val rentalOrdersRepository: RentalOrdersRepository
) : ViewModel() {

    private val _customerState = MutableStateFlow(CustomerState())
    val customerState: StateFlow<CustomerState> = _customerState

    private val _inventoryState = MutableStateFlow(InventoryState())
    val inventoryState: StateFlow<InventoryState> = _inventoryState

    private val _productState = MutableStateFlow(ProdState())
    val prodState: StateFlow<ProdState> = _productState

    private val _orderItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val orderItems: StateFlow<List<OrderItem>> = _orderItems

    private val _rentalOrderState = MutableStateFlow(RentalOrderState())
    val rentalOrderState: StateFlow<RentalOrderState> = _rentalOrderState

    private val _totalAmount = MutableStateFlow(0L)
    val totalAmount: StateFlow<Long> = _totalAmount

    private val _paidAmount = MutableStateFlow(0L)
    val paidAmount: StateFlow<Long> = _paidAmount

    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer: StateFlow<Customer?> = _selectedCustomer

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct

    // Fetch data for customers and inventory
    init {
        fetchCustomers()
        fetchInventory()
        fetchProducts()
    }

    private fun fetchCustomers() {
        viewModelScope.launch {
            // Simulate API call
            _customerState.value = CustomerState(isLoading = true)
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
//            _customerState.value = CustomerState(isLoading = false, data = customers)
        }
    }

    private fun fetchInventory() {
        viewModelScope.launch {
            // Simulate API call
//            _inventoryState.value = InventoryState(isLoading = true)
            inventoryRepository.getInvItems().onEach { result ->
                when (result) {
                    is Resource.Error ->
                        _inventoryState.value = InventoryState(
                            isLoading = false,
                            internet = false,
                            success = ERROR_HTTP
                        )

                    is Resource.Internet -> {
                        _inventoryState.value = InventoryState(
                            isLoading = false,
                            internet = true,
                            success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _inventoryState.value = InventoryState(
                            isLoading = true,
                            internet = false
                        )
                    }

                    is Resource.Success -> {
                        _inventoryState.value = InventoryState(
                            isLoading = false,
                            internet = false,
                            success = SUCCESS,
                            data = result.data!!
                        )
                    }
                }
            }.launchIn(viewModelScope)
//            _inventoryState.value = InventoryState(isLoading = false, data = inventory)
        }
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            // Simulate API call
//            _inventoryState.value = InventoryState(isLoading = true)
            inventoryRepository.getProducts().onEach { result ->
                when (result) {
                    is Resource.Error ->
                        _productState.value = ProdState(
                            isLoading = false,
                            internet = false,
                            success = ERROR_HTTP
                        )

                    is Resource.Internet -> {
                        _productState.value = ProdState(
                            isLoading = false,
                            internet = true,
                            success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _productState.value = ProdState(
                            isLoading = true,
                            internet = false
                        )
                    }

                    is Resource.Success -> {
                        _productState.value = ProdState(
                            isLoading = false,
                            internet = false,
                            success = SUCCESS,
                            data = result.data!!
                        )
                    }
                }
            }.launchIn(viewModelScope)
//            _inventoryState.value = InventoryState(isLoading = false, data = inventory)
        }
    }

    // Add product to order items
    fun addOrderItem(qty: Int, days: Int, price: Long) {
        val product = _selectedProduct.value ?: return
        val quantity = qty // Replace with quantity input field value
        val days = days // Replace with quantity input field value
        val price = price//_selectedProduct.value?.rentalPrice ?: 0

        // Check if enough stock is available
        val inventoryItem = _inventoryState.value.data.find { it.prodId == product.id }
        if (inventoryItem != null && inventoryItem.avlCount >= quantity) {
            val newItem = OrderItem(product.id, product.name, quantity, days, 0, price)//todo
            _orderItems.value += newItem

            // Update total amount
            val newTotalAmount = _orderItems.value.sumOf { it.quantity * it.days * it.price }
            _totalAmount.value = newTotalAmount
        }
    }

    // Save rental order
    fun saveRentalOrder() {

    }

    // Update paid amount
    fun updatePaidAmount(amount: Long) {
        _paidAmount.value = amount
    }

    fun selectCustomer(customer: Customer) {
        _selectedCustomer.value = customer
    }

    fun selectProduct(product: Product) {
        _selectedProduct.value = product
    }
}



