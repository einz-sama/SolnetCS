package com.einz.solnetcs.ui.cust.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetcs.data.Repository
import kotlinx.coroutines.launch

class SettingViewModel(private val repository: Repository): ViewModel() {

    private val _customerLiveData = repository.customerLiveData
    val customerLiveData = repository.customerLiveData

    private val _changePasswordLiveData = repository.changePasswordLiveData
    val changePasswordLiveData = repository.changePasswordLiveData

    private val _changeAlamatLiveData = repository.changeAlamatLiveData
    val changeAlamatLiveData = repository.changeAlamatLiveData

    private val _changePhoneLiveData = repository.changePhoneLiveData
    val changePhoneLiveData = repository.changePhoneLiveData

    private val _loggedOutLiveData = repository.loggedOutLiveData
    val loggedOutLiveData = _loggedOutLiveData

    fun getCustomer(){
        viewModelScope.launch {
            repository.getCustomerData()
        }
    }

    fun changePassword(password: String){
        viewModelScope.launch {
            repository.changePassword(password)
        }
    }

    fun changeAlamat(idCust:String, alamat:String){
        viewModelScope.launch {
            repository.changeAlamat(idCust, alamat)
        }
    }

    fun changePhone(idCust:String, phone:String){
        viewModelScope.launch {
            repository.changePhone(idCust, phone)
        }
    }

    fun logout(){
        viewModelScope.launch {
            repository.logout()
        }
    }

}