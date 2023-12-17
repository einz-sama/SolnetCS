package com.einz.solnetcs.ui.auth


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetcs.data.Repository
import com.einz.solnetcs.data.remote.responses.Login
import kotlinx.coroutines.launch
import com.einz.solnetcs.data.Result

class LoginViewModel(private val repository: Repository): ViewModel() {

    private val _responseLogin = repository.responseLogin
    val responseLogin: LiveData<Result<Login>> = _responseLogin


    fun login(username : String, password : String){
        viewModelScope.launch{
            repository.login(username, password)
        }

    }



}