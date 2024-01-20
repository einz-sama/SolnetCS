package com.einz.solnetcs.ui.auth.login


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetcs.data.Repository
import kotlinx.coroutines.launch
import com.einz.solnetcs.data.Result
import com.google.firebase.auth.FirebaseUser

class LoginViewModel(private val repository: Repository): ViewModel() {

    private val _responseLogin = repository.userLiveData
    val responseLogin: MutableLiveData<Result<FirebaseUser?>> = _responseLogin


    fun login(username : String, password : String){
        viewModelScope.launch{
            repository.login(username, password)
        }

    }



}