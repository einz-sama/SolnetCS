package com.einz.solnetcs.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetcs.data.Repository
import com.einz.solnetcs.data.Result
import com.einz.solnetcs.data.model.Customer
import com.einz.solnetcs.data.remote.responses.Login
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: Repository): ViewModel() {

    val userLiveData = repository.registerSuccessLiveData

    fun register(user: Customer, textPassword: String){
        viewModelScope.launch {
            repository.registerFirebase(user,textPassword)
        }
    }



}