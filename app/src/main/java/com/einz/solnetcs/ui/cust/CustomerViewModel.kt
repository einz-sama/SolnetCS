package com.einz.solnetcs.ui.cust

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetcs.data.Repository
import com.einz.solnetcs.data.Result
import com.einz.solnetcs.data.remote.responses.DataCustomer
import kotlinx.coroutines.launch

class CustomerViewModel(private val repository: Repository): ViewModel() {

//    private val _responseGetCustomer = repository.responseGetCustomer
//    val responseGetCustomer: LiveData<Result<DataCustomer>> = _responseGetCustomer

//    fun getCustomer(idCustomer : String){
//        viewModelScope.launch{
//            repository.getCustomer(idCustomer)
//            Log.d("VIEWMODEL CUSTOMER", "idCustomer: $idCustomer")
//            val currentGet = responseGetCustomer.value
//            Log.d("VIEWMODEL CUSTOMER", "responseGetCustomer: $currentGet")
//        }
//    }
}