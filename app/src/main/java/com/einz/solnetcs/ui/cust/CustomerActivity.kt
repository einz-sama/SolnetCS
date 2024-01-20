package com.einz.solnetcs.ui.cust

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.einz.solnetcs.data.LoginPreferences
import com.einz.solnetcs.data.di.ViewModelFactory
import com.einz.solnetcs.databinding.ActivityCustomerBinding
import com.einz.solnetcs.data.Result
import com.einz.solnetcs.ui.auth.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class CustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: CustomerViewModel by viewModels{factory}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)

        viewModel.getCustomer()
        viewModel.customerLiveData.observe(this){
            result ->
            when(result){
                is Result.Success -> {
                    binding.apply{
                        var header = "SI-TP"
                        header = if(result.data?.daerahCustomer.equals("Tanjungpinang")){
                            "SI-TP"
                        } else{
                            "SI-BTN"
                        }

                        welcome.text = "Selamat Datang"
                        username.text = result.data?.namaCustomer
                        idPelanggan.text = "ID PELANGGAN: ${header}${result.data?.idCustomer}"
                        alamat.text = "ALAMAT: ${result.data?.alamatCustomer}"
                        daerah.text = "DAERAH: ${result.data?.daerahCustomer}"
                        telepon.text = "TELEPON: ${result.data?.noTelpCustomer}"
                        showLoading(false)
                    }
                }
                is Result.Error -> {
                    Log.d("CustomerActivity", "Error: ${result.errorMessage}")
                    showLoading(false)
                }
                is Result.Loading -> {
                    showLoading(true)
                }

                else -> {
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()}
            }
        }

        binding.logout.setOnClickListener(){
            viewModel.logout()
            viewModel.loggedOutLiveData.observe(this){
                    result ->
                when(result){
                    is Result.Success -> {
                        Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }
                    is Result.Error -> {
                        Log.d("CustomerActivity", "Error: ${result.errorMessage}")
                    }
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    else -> {
                        Toast.makeText(this, "Logout Failed", Toast.LENGTH_SHORT).show()}
                }
            }
        }






    }
    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

}