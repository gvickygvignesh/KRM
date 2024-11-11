package com.krm.rentalservices

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(private val inventoryUseCase: InventoryUseCase) :
    ViewModel() {

    private var job: Job? = null
    private val _state = mutableStateOf(InventoryState())
    val state: State<InventoryState> = _state

    private val _add_inv_state = mutableStateOf(AddInventoryState())
    val add_inv_state: State<AddInventoryState> = _add_inv_state


    fun addItem(name: String, quantity: Int, price: Double) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            val newItem = InventoryItem(name = name, quantity = quantity)
//            viewModelScope.launch {
            inventoryUseCase.addItem(newItem).collectLatest { result ->
                when (result) {
                    is Resource.Error ->
                        _add_inv_state.value = AddInventoryState(
                            isLoading = false,
                            internet = false,
                            success = ERROR_HTTP
                        )

                    is Resource.Internet -> {
                        _add_inv_state.value = AddInventoryState(
                            isLoading = false,
                            internet = true,
                            success = ERROR_INTERNET
                        )
                    }

                    is Resource.Loading -> {
                        _add_inv_state.value = AddInventoryState(
                            isLoading = true,
                            internet = false
                        )
                    }

                    is Resource.Success -> {
                        _add_inv_state.value = AddInventoryState(
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

    fun updateQuantity(id: String, newQuantity: Int) {
        viewModelScope.launch {
            inventoryUseCase.updateItemQuantity(id, newQuantity)
        }
    }
}



