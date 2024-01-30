package com.einz.solnetcs.ui.cust.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetcs.data.Repository
import kotlinx.coroutines.launch

class CustomerViewModel(private val repository: Repository): ViewModel() {

    private val _customerLiveData = repository.customerLiveData
    val customerLiveData = repository.customerLiveData

    private val _loggedOutLiveData = repository.loggedOutLiveData
    val loggedOutLiveData = _loggedOutLiveData

    private val _checkLaporanLiveData = repository.checkLaporanLiveData
    val checkLaporanLiveData = _checkLaporanLiveData

    fun getCustomer(){
        viewModelScope.launch {
            repository.getCustomerData()
        }
    }

    fun logout(){
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun checkLaporan(idCust:String){
        viewModelScope.launch {
            repository.checkActiveLaporanByIdCust(idCust)
        }
    }


}