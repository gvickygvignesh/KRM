package com.krm.rentalservices.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.krm.rentalservices.AddFireStoreState
import com.krm.rentalservices.CustomerState
import com.krm.rentalservices.InventoryState
import com.krm.rentalservices.ProdState
import com.krm.rentalservices.RentalOrderState
import com.krm.rentalservices.Resource
import com.krm.rentalservices.model.Customer
import com.krm.rentalservices.model.OrderItem
import com.krm.rentalservices.model.OtherCharges
import com.krm.rentalservices.model.Payment
import com.krm.rentalservices.model.Product
import com.krm.rentalservices.model.RentalOrder
import com.krm.rentalservices.repository.CustomersRepository
import com.krm.rentalservices.repository.InventoryRepository
import com.krm.rentalservices.repository.RentalOrdersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date
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
    private var job: Job? = null

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

    private val _otherChargesTotalAmount = MutableStateFlow(0L)
    val otherChargesTotalAmount: StateFlow<Long> = _otherChargesTotalAmount

    private val _paidAmount = MutableStateFlow(0L)
    val paidAmount: StateFlow<Long> = _paidAmount

    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer: StateFlow<Customer?> = _selectedCustomer

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct

    private val _paymentItems = MutableStateFlow<List<Payment>>(emptyList())
    val paymentItems: StateFlow<List<Payment>> = _paymentItems

    private val _otherChargeItems = MutableStateFlow<List<OtherCharges>>(emptyList())
    val otherChargeItems: StateFlow<List<OtherCharges>> = _otherChargeItems

    private val _addRentalOrderState = mutableStateOf(AddFireStoreState())
    val addRentalOrderState: State<AddFireStoreState> = _addRentalOrderState

    // Fetch data for customers and inventory
    /*init {
        fetchCustomers()
        fetchInventory()
        fetchProducts()
    }*/

    fun fetchCustomers() {
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

    fun fetchInventory() {
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

    fun fetchProducts() {
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

    fun selectCustomer(customer: Customer) {
        _selectedCustomer.value = customer
    }

    fun selectProduct(product: Product) {
        _selectedProduct.value = product
    }

    // Add product to order items
    fun addOrderItem(qty: Int, days: Int, rentalPrice: Long, amount: Long) {
        val product = _selectedProduct.value ?: return
        /* val quantity = qty // Replace with quantity input field value
         val days = days // Replace with days input field value
         val price = rentalPrice//_selectedProduct.value?.rentalPrice ?: 0*/

        // Check if enough stock is available
        val inventoryItem = _inventoryState.value.data.find { it.prodId == product.id }
        if (inventoryItem != null && inventoryItem.avlCount >= qty) {
            val newItem = OrderItem(
                product.id,
                product.name,
                qty,
                days,
                rentalPrice = rentalPrice,
                amount = amount
            )//todo
            _orderItems.value += newItem

            // Update total amount
            val newTotalAmount = _orderItems.value.sumOf { it.quantity * it.days * it.rentalPrice }
            _totalAmount.value = newTotalAmount + _otherChargeItems.value.sumOf { it.amount }
        }
    }

    fun addPaymentItem(dateTime: String, payMode: String, remarks: String, amount: Long) {
        val newPayment = Payment(dateTime, payMode, amount, remarks)
        _paymentItems.value += newPayment
        _paidAmount.value = _paymentItems.value.sumOf { it.amount }
    }

    fun addOtherChargesItem(otherChargeType: String, remarks: String, amount: Long) {
        val newCharge = OtherCharges(otherChargeType, amount, remarks)
        _otherChargeItems.value += newCharge

        val otherChargesTotal = _otherChargeItems.value.sumOf { it.amount }
        _otherChargesTotalAmount.value = otherChargesTotal

        val currOrderValueTotal = _orderItems.value.sumOf { it.quantity * it.days * it.rentalPrice }
        _totalAmount.value = otherChargesTotal + currOrderValueTotal
    }

    // Save rental order
    fun saveRentalOrder(orderStatus: String, isReturnOrder: Boolean) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            val newOrder = RentalOrder(
                customerId = _selectedCustomer.value?.id ?: "",
                customerName = _selectedCustomer.value?.name ?: "",
                orderDate = Date(),
                orderStatus = orderStatus,
                orderItemList = orderItems.value,
                paymentList = paymentItems.value,
                otherChargesList = otherChargeItems.value,
                totalAmt = totalAmount.value,
                paidAmt = paidAmount.value,
                balanceAmt = totalAmount.value - paidAmount.value,
                returnOrderDate = if (isReturnOrder) {
                    Date()
                } else null,
                timestamp = Timestamp.now(),
            )

//            rentalOrdersRepository.createRentalOrderWithInventoryUpdate(newOrder).collectLatest { result -> }
            /* rentalOrdersRepository.addRentalOrder(rentalOrder = newOrder).collectLatest { result -> */
            rentalOrdersRepository.createRentalOrderWithInventoryUpdate(newOrder)
                .collectLatest { result ->
                    when (result) {
                        is Resource.Error ->
                            _addRentalOrderState.value = AddFireStoreState(
                                isLoading = false,
                                internet = false,
                                success = ERROR_HTTP,
                                isEventHandled = false
                            )

                        is Resource.Internet -> {
                            _addRentalOrderState.value = AddFireStoreState(
                                isLoading = false,
                                internet = true,
                                success = ERROR_INTERNET,
                                isEventHandled = false
                            )
                        }

                        is Resource.Loading -> {
                            _addRentalOrderState.value = AddFireStoreState(
                                isLoading = true,
                                internet = false
                            )
                        }

                        is Resource.Success -> {
                            _addRentalOrderState.value = AddFireStoreState(
                                isLoading = false,
                                internet = false,
                                success = SUCCESS,
                                data = result.data!!,
                                isEventHandled = false
                            )
                        }

                    }
                }
        }
    }

    fun markEventHandled() {
        _addRentalOrderState.value = _addRentalOrderState.value.copy(isEventHandled = true)
    }


    fun clearData() {
        // Reset state flows to their default values
//        _customerState.value = CustomerState()
        _inventoryState.value = InventoryState()
//        _productState.value = ProdState()
        _orderItems.value = emptyList()
        _rentalOrderState.value = RentalOrderState()
        _totalAmount.value = 0L
        _otherChargesTotalAmount.value = 0L
        _paidAmount.value = 0L
        _selectedCustomer.value = null
        _selectedProduct.value = null
        _paymentItems.value = emptyList()
        _otherChargeItems.value = emptyList()

        // Reset AddFireStoreState
        _addRentalOrderState.value = AddFireStoreState()
    }


    // Flow to collect Rental order items
    fun fetchRentalOrders() {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
//            val itemsFlow: Flow<Resource<List<InventoryItem>>> =
            rentalOrdersRepository.fetchRentalOrders().onEach { result ->
                when (result) {
                    is Resource.Error ->
                        _rentalOrderState.value = RentalOrderState(
                            isLoading = false,
                            internet = false,
                            success = ERROR_HTTP
                        )

                    is Resource.Internet -> {
                        _rentalOrderState.value = RentalOrderState(
                            isLoading = false,
                            internet = true,
                            success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _rentalOrderState.value = RentalOrderState(
                            isLoading = true,
                            internet = false
                        )
                    }

                    is Resource.Success -> {
                        _rentalOrderState.value = RentalOrderState(
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

}



