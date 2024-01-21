package com.einz.solnetcs.ui.cust.new_ticket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetcs.data.Repository
import com.einz.solnetcs.data.model.Laporan
import kotlinx.coroutines.launch

class NewTicketViewModel(private val repository: Repository): ViewModel() {

    val createLaporanLiveData = repository.createLaporanLiveData
    val customerLiveData = repository.customerLiveData

    fun createLaporan(idCust: String, laporan: Laporan){
        viewModelScope.launch {
            repository.createLaporan(idCust, laporan)
        }
    }

    fun getCustomer(){
        viewModelScope.launch {
            repository.getCustomerData()
        }
    }
}