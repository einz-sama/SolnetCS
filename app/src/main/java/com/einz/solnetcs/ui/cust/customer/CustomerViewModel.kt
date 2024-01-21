package com.einz.solnetcs.ui.cust.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetcs.data.Repository
import kotlinx.coroutines.launch

class CustomerViewModel(private val repository: Repository): ViewModel() {

    val customerLiveData = repository.customerLiveData
    val loggedOutLiveData = repository.loggedOutLiveData

    val checkLaporanLiveData = repository.checkLaporanLiveData

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