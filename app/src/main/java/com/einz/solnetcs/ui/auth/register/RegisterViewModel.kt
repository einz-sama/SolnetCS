package com.einz.solnetcs.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetcs.data.Repository
import com.einz.solnetcs.data.model.Customer
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: Repository): ViewModel() {

    private val _userLiveData = repository.registerSuccessLiveData
    val userLiveData = _userLiveData

    private val _verifyLiveData = repository.verifyCustomerLiveData
    val verifyLiveData = _verifyLiveData

    fun verifyCustomer(email: String, phone: String){
        viewModelScope.launch {
            repository.verifyCustomer(email, phone)
        }
    }

    fun newRegister(idCustomer: Int, email: String, password: String){
        viewModelScope.launch {
            repository.newregister(idCustomer, email, password)
        }
    }



    fun register(user: Customer, textPassword: String){
        viewModelScope.launch {
            repository.register(user,textPassword)
        }
    }



}