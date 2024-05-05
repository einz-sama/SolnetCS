package com.einz.solnetcs.ui.cust.active_ticket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetcs.data.Repository
import kotlinx.coroutines.launch

class ActiveTicketViewModel(private val repository: Repository): ViewModel() {

    private val _getLaporanLiveData = repository.getLaporanLiveData
    val getLaporanLiveData = _getLaporanLiveData


    private val _customerLiveData = repository.customerLiveData
    val customerLiveData = _customerLiveData

    private val _laporanDoneLiveData = repository.laporanDoneLiveData
    val laporanDoneLiveData = _laporanDoneLiveData

    fun resetLaporanDone(){
        repository.resetLaporanDone()
    }

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
            repository.updateLaporanStatus(idCust, idLaporan, 3)
        }
    }

    fun finishLaporan(idlaporan:String, newStatus: Int){
        viewModelScope.launch{
            repository.finishLaporan(idlaporan, newStatus)
        }
    }

}