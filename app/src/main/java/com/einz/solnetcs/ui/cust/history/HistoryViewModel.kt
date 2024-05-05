package com.einz.solnetcs.ui.cust.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetcs.data.Repository
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: Repository): ViewModel() {

    private val _customerLiveData = repository.customerLiveData
    val customerLiveData = _customerLiveData


    private val _listFinishedLaporan = repository.laporanListLiveData
    val listFinishedLaporan = _listFinishedLaporan

    fun getCustomer(){
        viewModelScope.launch {
            repository.getCustomerData()
        }
    }

    fun getFinishedLaporan(idCust: String){
        viewModelScope.launch {
            repository.getFinishedLaporan(idCust)
        }
    }




}