package com.einz.solnetcs.ui.auth.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetcs.data.Repository
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val repository: Repository): ViewModel() {

    private val _resetPassword = repository.resetPasswordLiveData
    val resetPasswordLiveData = _resetPassword

    private val _confirmedCustomer = repository.verifiedCustomerLiveData
    val confirmedCustomerLiveData = _confirmedCustomer

    fun resetPassword(email: String){
        viewModelScope.launch{
            repository.resetPassword(email)
        }
    }

    fun checkCustomer(email: String){
        viewModelScope.launch {
            repository.checkIfCustomer(email)
        }
    }
}