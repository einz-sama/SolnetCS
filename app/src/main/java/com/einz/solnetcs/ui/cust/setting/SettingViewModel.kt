package com.einz.solnetcs.ui.cust.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetcs.data.Repository
import kotlinx.coroutines.launch

class SettingViewModel(private val repository: Repository): ViewModel() {

    val customerLiveData = repository.customerLiveData

    val changePasswordLiveData = repository.changePasswordLiveData

    val changeAlamatLiveData = repository.changeAlamatLiveData

    val changePhoneLiveData = repository.changePhoneLiveData

    val loggedOutLiveData = repository.loggedOutLiveData

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