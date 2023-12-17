package com.einz.solnetcs.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.einz.solnetcs.data.remote.responses.DataCustomer
import com.einz.solnetcs.data.remote.responses.DataTicket
import com.einz.solnetcs.data.remote.responses.Login
import com.einz.solnetcs.data.remote.retrofit.ApiConfig
import com.einz.solnetcs.data.remote.retrofit.ApiService

class Repository(private val apiService: ApiService, private val context: Context) {

    private val _responseLogin = MutableLiveData<Result<Login>>()
    val responseLogin: LiveData<Result<Login>> = _responseLogin

    private val _responseGetCustomer = MutableLiveData<Result<DataCustomer>>()
    val responseGetCustomer: LiveData<Result<DataCustomer>> = _responseGetCustomer

    private val _responseGetTicket = MutableLiveData<Result<DataTicket>>()
    val responseGetTicket: LiveData<Result<DataTicket>> = _responseGetTicket

    suspend fun login(username : String, password : String){
        try {
            val response = ApiConfig.getApiService().login(username, password)
            if (response.isSuccessful) {
                val result = response.body()
                result?.data?.let{
                    _responseLogin.value = Result.Success(it)
                }
            } else {
                _responseLogin.value = Result.Error("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            _responseLogin.value = Result.Error(e.message.toString())
        }
    }

    suspend fun getCustomer(idCustomer : String){
        try {
            val response = ApiConfig.getApiService().getCustomer(idCustomer)
            if (response.isSuccessful) {
                val result = response.body()
                result?.data?.let{
                    _responseGetCustomer.value = Result.Success(it)
                    val result = _responseGetCustomer.value
                }
            } else {
                _responseGetCustomer.value = Result.Error("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            _responseGetCustomer.value = Result.Error(e.message.toString())
            val errorcode = _responseGetCustomer.value
        }

    }

    suspend fun getTicket(idCustomer : String){
        Log.d("REPOSITORY TICKET INIT", "idCustomer: $idCustomer")
        try {
            val response = ApiConfig.getApiService().getTicket(idCustomer)
            if (response.isSuccessful) {
                val result = response.body()
                result?.data?.let{
                    _responseGetTicket.value = Result.Success(it)
                    val result = _responseGetTicket.value
                    Log.d("REPOSITORY TICKET SUCCESS", "idCustomer: $result")
                }
            } else {
                _responseGetTicket.value = Result.Error("Error: ${response.code()}")
                Log.d("REPOSITORY TICKET ERROR", "idCustomer: $_responseGetTicket.value ")
            }
        } catch (e: Exception) {
            _responseGetTicket.value = Result.Error(e.message.toString())
            val errorcode = _responseGetTicket.value
            Log.d("REPOSITORY TICKET EXCEPT", "idCustomer: $errorcode")
        }

    }



}