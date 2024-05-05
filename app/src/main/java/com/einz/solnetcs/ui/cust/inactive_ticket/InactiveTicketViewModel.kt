package com.einz.solnetcs.ui.cust.inactive_ticket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetcs.data.Repository
import kotlinx.coroutines.launch

class InactiveTicketViewModel(private val repository: Repository): ViewModel() {

    private val _laporanLiveData = repository.laporanLiveData
    val laporanLiveData = _laporanLiveData

    fun getLaporanById(idLaporan: String){
        viewModelScope.launch {
            repository.getLaporanById(idLaporan)
        }
    }

}