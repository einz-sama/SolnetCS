package com.einz.solnetcs.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetcs.data.Repository
import com.einz.solnetcs.data.model.Customer
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: Repository): ViewModel() {

    private val _userLiveData = repository.registerSuccessLiveData
    val userLiveData = _userLiveData

    fun register(user: Customer, textPassword: String){
        viewModelScope.launch {
            repository.register(user,textPassword)
        }
    }



}