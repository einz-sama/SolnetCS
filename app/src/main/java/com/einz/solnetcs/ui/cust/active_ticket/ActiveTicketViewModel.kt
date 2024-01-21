package com.einz.solnetcs.ui.cust.active_ticket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetcs.data.Repository
import kotlinx.coroutines.launch

class ActiveTicketViewModel(private val repository: Repository): ViewModel() {

    val getLaporanLiveData = repository.getLaporanLiveData

    val customerLiveData = repository.customerLiveData

    val laporanDoneLiveData = repository.laporanDoneLiveData

    fun getCustomer(){
        viewModelScope.launch {
            repository.getCustomerData()
        }
    }

    fun getLaporan(idCust: String){
        viewModelScope.launch {
            repository.getActiveLaporanByIdCust(idCust)
        }
    }

    fun laporanDone(idCust: String, idLaporan: String){
        viewModelScope.launch {
            repository.updateLaporanStatus(idCust, idLaporan, 4)
        }
    }

}