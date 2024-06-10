package com.einz.solnetcs.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.einz.solnetcs.R
import com.einz.solnetcs.data.State
import com.einz.solnetcs.data.di.ViewModelFactory
import com.einz.solnetcs.databinding.ActivityRegisterBinding
import com.einz.solnetcs.databinding.ActivityVerifyCustomerBinding
import com.einz.solnetcs.util.ErrorDialog
import com.einz.solnetcs.util.phoneValidator

class VerifyCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyCustomerBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: RegisterViewModel by viewModels{factory}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)

        binding.btnRegister.isEnabled = false

        checkInput()

        binding.btnRegister.setOnClickListener {
            val idPelanggan = binding.tfEditIdPelanggan.text?.trim().toString()
            val textPhone = binding.tfEditPhone.text?.trim().toString()

            viewModel.verifyCustomer(idPelanggan, textPhone)

            binding.progressBar.visibility = View.VISIBLE
        }

        viewModel.verifyLiveData.observe(this){result->
            when(result) {
                is State.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val intent = Intent(this, NewRegisterActivity::class.java)
                    startActivity(intent)

                }

                is State.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is State.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showError(result.errorMessage)
                }
            }
        }


    }

    private fun checkValid(){
        val idPelanggan = binding.tfEditIdPelanggan.text?.trim().toString()
        val textPhone = binding.tfEditPhone.text?.trim().toString()

        var isValid = true

        if (textPhone.isEmpty()) {
            binding.tfLayoutPhone.error = "Isi Nomor Telpon"
            isValid = false
        } else {
            binding.tfLayoutPhone.error = null
            if( phoneValidator(this@VerifyCustomerActivity, textPhone).isEmpty() ){
                binding.tfLayoutPhone.error = "Nomor Telepon tidak valid"
                isValid = false
            }
            else{
                binding.tfLayoutPhone.error = null
            }
        }

        if (idPelanggan.length <6) {
            binding.tfLayoutIdPelanggan.error = "ID Pelanggan Anda"
            isValid = false
        } else {
            binding.tfLayoutIdPelanggan.error = null
        }

        binding.btnRegister.isEnabled = isValid
    }

    private fun checkInput(){
        binding.tfEditIdPelanggan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkValid()
            }
            override fun afterTextChanged(string: Editable?) {}
        })
        binding.tfEditPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkValid()
            }
            override fun afterTextChanged(string: Editable?) {}
        })


    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE

        // Create an instance of the custom dialog fragment
        val errorDialog = ErrorDialog().apply {
            arguments = Bundle().apply {
                putString("message", message)
            }
        }

        // Show the dialog
        errorDialog.show(supportFragmentManager, "ErrorDialogFragment")
    }

}