package com.krm.rentalservices.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.krm.rentalservices.AddFireStoreState
import com.krm.rentalservices.Constants
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
import com.krm.rentalservices.ui.TAG
import com.krm.rentalservices.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class RentalOrderViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    private val customersRepository: CustomersRepository,
    private val rentalOrdersRepository: RentalOrdersRepository
) : ViewModel() {
    private var job: Job? = null

    private val _customerState = MutableStateFlow(CustomerState())
    val customerState: StateFlow<CustomerState> = _customerState.asStateFlow()

    private val _inventoryState = MutableStateFlow(InventoryState())
    val inventoryState: StateFlow<InventoryState> = _inventoryState.asStateFlow()

    private val _productState = MutableStateFlow(ProdState())
    val prodState: StateFlow<ProdState> = _productState.asStateFlow()

    private val _orderItemsDTO = MutableStateFlow<List<OrderItem>>(emptyList())
    val orderItemsDTO: StateFlow<List<OrderItem>> = _orderItemsDTO.asStateFlow()

    private val _rentalOrderState = MutableStateFlow(RentalOrderState())
    val rentalOrderState: StateFlow<RentalOrderState> = _rentalOrderState.asStateFlow()

    private val _discountAmount = MutableStateFlow(0L)
    val discountAmount: StateFlow<Long> = _discountAmount.asStateFlow()

    private val _isUpdateOrder = MutableStateFlow(false)
    val isUpdateOrder: StateFlow<Boolean> = _isUpdateOrder.asStateFlow()

    private val _selectedRentalOrder =
        MutableStateFlow<RentalOrder?>(null) //Rental Order selected from the list
    val selectedRentalOrder = _selectedRentalOrder.asStateFlow()

    private val _discountedTotalAmount = MutableStateFlow(0L)
    private val _totalAmount = MutableStateFlow(0L)

    private val _orderID = MutableStateFlow<String?>(null)
    val orderID = _orderID.asStateFlow()

    // Combine total and discount values to dynamically update totalAmount
    val totalAmount: StateFlow<Long> = combine(
        _totalAmount,
        _discountedTotalAmount
    ) { total, discounted ->
        if (discounted != 0L) discounted else total
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0L)

    private val _otherChargesTotalAmount = MutableStateFlow(0L)
    val otherChargesTotalAmount: StateFlow<Long> = _otherChargesTotalAmount.asStateFlow()

    private val _paidAmount = MutableStateFlow(0L)
    val paidAmount: StateFlow<Long> = _paidAmount.asStateFlow()

    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer: StateFlow<Customer?> = _selectedCustomer.asStateFlow()

    private var hasPreselected = false

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _paymentItems = MutableStateFlow<List<Payment>>(emptyList())
    val paymentItems: StateFlow<List<Payment>> = _paymentItems.asStateFlow()

    private val _otherChargeItems = MutableStateFlow<List<OtherCharges>>(emptyList())
    val otherChargeItems: StateFlow<List<OtherCharges>> = _otherChargeItems.asStateFlow()

    private val _addRentalOrderState = mutableStateOf(AddFireStoreState())
    val addRentalOrderState: State<AddFireStoreState> = _addRentalOrderState

    init {
        Log.d(TAG, "RentalOrder: viewmodel init called: ")
        clearData()
        fetchCustomers()
        fetchInventory()
        fetchProducts()
    }

    private fun fetchCustomers() {
        viewModelScope.launch {
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

                        // Preselect specific customer if not null
                        if (_isUpdateOrder.value && !hasPreselected) {
                            val preselectedCustomer =
                                result.data.find { it.id == _selectedRentalOrder.value?.customerId }
                            if (preselectedCustomer != null) {
                                selectCustomer(preselectedCustomer)
                                hasPreselected = true // Ensure it doesn't run again
                            }
                        }
                    }

                }
            }.launchIn(viewModelScope)
        }
    }

    fun fetchInventory() {
        viewModelScope.launch {
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
        }
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            // Simulate API call
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
        }
    }

    fun selectCustomer(customer: Customer) {
        _selectedCustomer.update { customer }
    }

    fun clearCustomer() {
        _selectedCustomer.update { null }
    }

    fun selectProduct(product: Product?) {
        _selectedProduct.update { product }
    }

    fun setOrderItemDTO(orderItemList: List<OrderItem>) {
        _orderItemsDTO.update { orderItemList }
        updateTotalAmt()
    }

    fun setPaymentsDTO(paymentList: List<Payment>) {
        _paymentItems.update { paymentList }
        updatePaidAmt()
    }

    fun setOtherChargesDTO(otherChargesList: List<OtherCharges>) {
        _otherChargeItems.update { otherChargesList }
        updateChargesAmt()
    }

    fun setDiscountAmt(discountAmt: String) {
        Log.d(TAG, "setDiscountAmt : $discountAmt")
        val discountValue = discountAmt.toLongOrNull() ?: 0L
        _discountAmount.update { discountValue }
        updateDiscountAmt()
    }

    fun setIsUpdateOrder(isUpdateOrder: Boolean) {
        _isUpdateOrder.update { isUpdateOrder }
    }

    fun setRentalOrder(rentalOrder: RentalOrder) {
        _selectedRentalOrder.update { rentalOrder }
    }

    private fun updateTotalAmt() {
        _totalAmount.update {
            _orderItemsDTO.value.sumOf { it.quantity * it.days * it.rentalPrice } +
                    _otherChargeItems.value.sumOf { it.amount }
        }
        updateDiscountAmt()
    }

    private fun updateDiscountAmt() {
        _discountedTotalAmount.update {
            _totalAmount.value - _discountAmount.value
        }

        Log.d(TAG, "updateDiscountAmt: discountedTotalAmount " + _discountedTotalAmount.value)
        Log.d(TAG, "updateDiscountAmt: totalAmount " + totalAmount.value)
    }

    private fun updatePaidAmt() {
        _paidAmount.update { _paymentItems.value.sumOf { it.amount } }
    }

    private fun updateChargesAmt() {
        _otherChargesTotalAmount.update { _otherChargeItems.value.sumOf { it.amount } }
        updateTotalAmt()
    }

    fun generateInvoiceNo(): String {
        val invoiceNo = Utils.generateInvoiceNumber()
        _orderID.update { invoiceNo }
        return invoiceNo
    }

    fun clearData() {
        Log.d(TAG, "RentalOrder: clearData: called")
        _productState.value = ProdState()
        _orderItemsDTO.value = emptyList()
        _rentalOrderState.value = RentalOrderState()
        _totalAmount.value = 0L
        _otherChargesTotalAmount.value = 0L
        _paidAmount.value = 0L
        _selectedCustomer.value = null
        _selectedProduct.value = null
        _paymentItems.value = emptyList()
        _otherChargeItems.value = emptyList()
        _customerState.value = CustomerState()
        _inventoryState.value = InventoryState()
        _addRentalOrderState.value = AddFireStoreState()
    }

    // Add product to order items
    fun addOrUpdateOrderItem(
        qty: Int,
        rtnQty: Int,
        days: Int,
        rentalPrice: Long,
        amount: Long,
        isProductPresent: Boolean,
        isDelete: Boolean
    ) {
        val product = _selectedProduct.value ?: return

        // Check if enough stock is available
//        val inventoryItem = _inventoryState.value.data.find { it.prodId == product.id }
        if (isDelete) {
            _orderItemsDTO.update { orderItemList -> orderItemList.filterNot { it.productId == product.id } }
        } else {
//            if (inventoryItem != null && inventoryItem.avlCount >= qty) { Removed As we already checked in UI
            val newItem = OrderItem(
                productId = product.id,
                productName = product.name,
                quantity = qty,
                days = days,
                rentalPrice = rentalPrice,
                amount = amount,
                rtnQty = rtnQty
            )

            if (isProductPresent) {
                _orderItemsDTO.update { orderItemList -> orderItemList.map { if (it.productId == product.id) newItem else it } }
            } else {
                _orderItemsDTO.value += newItem
            }
//            }
        }

        selectProduct(null) //reset selection

        // Update total amount
        updateTotalAmt()
    }

    fun addOtherChargesItem(otherChargeType: String, remarks: String, amount: Long) {
        _otherChargeItems.value += OtherCharges(otherChargeType, amount, remarks)

        updateChargesAmt()
        updateDiscountAmt()
    }

    fun addPaymentItem(dateTime: String, payMode: String, remarks: String, amount: Long) {
        _paymentItems.value += Payment(dateTime, payMode, amount, remarks)
        updatePaidAmt()
    }

    // Save rental order
    fun saveRentalOrder() {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {

            var isOrderUpdate = false
            var isOrderReturn = false

            if (_isUpdateOrder.value) {
                isOrderUpdate = true
                if (selectedRentalOrder.value!!.orderStatus == Constants.RETURNED_ORDER) {
                    isOrderReturn = true
                }
            }

            val updatedRentalOrder: RentalOrder = createOrderItem(isOrderUpdate, isOrderReturn)

            rentalOrdersRepository.createOrUpdateRentalOrderWithInventoryUpdate(
                updatedRentalOrder,
                selectedRentalOrder.value,
                isOrderUpdate
            ).collectLatest { result ->
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

    private fun createOrderItem(isEditOrder: Boolean, isReturnOrder: Boolean): RentalOrder {
        val rentalOrder = RentalOrder(
            customerId = _selectedCustomer.value?.id ?: "",
            customerName = _selectedCustomer.value?.name ?: "",
            orderDate = System.currentTimeMillis(),
            orderId = if (isEditOrder || isReturnOrder) {
                selectedRentalOrder.value!!.orderId
            } else _orderID.value!!,
            orderStatus = if (isReturnOrder) {
                Constants.RETURNED_ORDER
            } else Constants.OPEN_ORDER,
            orderItemList = orderItemsDTO.value,
            paymentList = paymentItems.value,
            otherChargesList = otherChargeItems.value,
            discountAmt = _discountAmount.value,
            totalAmt = totalAmount.value,
            paidAmt = paidAmount.value,
            balanceAmt = totalAmount.value - paidAmount.value,
            returnOrderDate = if (isReturnOrder) {
                System.currentTimeMillis()
            } else null,
            timestamp = Timestamp.now(),
        )

        return rentalOrder
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



