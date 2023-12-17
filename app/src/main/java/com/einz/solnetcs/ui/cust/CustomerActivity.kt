package com.einz.solnetcs.ui.cust

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.einz.solnetcs.R
import com.einz.solnetcs.data.LoginPreferences
import com.einz.solnetcs.data.di.ViewModelFactory
import com.einz.solnetcs.databinding.ActivityCustomerBinding
import com.einz.solnetcs.databinding.ActivityLoginBinding
import com.einz.solnetcs.ui.auth.LoginViewModel
import com.einz.solnetcs.data.Result

class CustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: CustomerViewModel by viewModels{factory}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)

        val preferences = LoginPreferences(this)
        val idCustomer = preferences.getIdCustomer()
        Log.d("TESTING CUSTOMER", "idCustomer: $idCustomer")

        if (idCustomer != null) {

            viewModel.getCustomer(idCustomer)

            viewModel.responseGetCustomer.observe(this){data -> when(data){
                is Result.Success -> {
                    Log.d("TESTING CUSTOMER SUCCESS", "data: ${data.data}")
                    binding.username.text = data.data.namaLengkap
                    binding.idPelanggan.text = data.data.idCustomer
                    binding.alamat.text = data.data.alamat
                    binding.daerah.text = data.data.daerah
                    binding.telepon.text = data.data.noHp
                }
                is Result.Error -> {
                    Log.d("TESTING CUSTOMER ERROR", "")
                }
                is Result.Loading -> {
                    Log.d("TESTING CUSTOMER LOADING", "")
                }
            } }
        }
        else{
            Log.d("TESTING CUSTOMER FAILED", "idCustomer: $idCustomer")
        }

    }
}