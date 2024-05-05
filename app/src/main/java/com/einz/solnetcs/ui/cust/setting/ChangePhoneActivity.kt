package com.einz.solnetcs.ui.cust.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.einz.solnetcs.data.State
import com.einz.solnetcs.data.di.ViewModelFactory
import com.einz.solnetcs.databinding.ActivityChangePhoneBinding

class ChangePhoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePhoneBinding
    private lateinit var factory: ViewModelFactory

    private val viewModel: SettingViewModel by viewModels{factory}

    private lateinit var idCustomer:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)
        viewModel.getCustomer()
        viewModel.customerLiveData.observe(this){
                result ->
            when(result){
                is State.Success -> {
                    idCustomer = result.data?.idCustomer.toString()
                    binding.progressBar.visibility = View.GONE
                }
                is State.Error -> {
                    binding.progressBar.visibility = View.GONE

                }
                is State.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE

                }
            }
        }

        binding.btnConfirm.isEnabled = false

        checkInput()

        binding.btnConfirm.setOnClickListener {
            val phone = binding.tfEditPhone.text?.trim().toString()
            viewModel.changePhone(idCustomer, phone)
            viewModel.changePhoneLiveData.observe(this){
                    result ->
                when(result){
                    is State.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Berhasil mengubah nomor telepon", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is State.Error -> {
                        binding.progressBar.visibility = View.GONE

                    }
                    is State.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE

                    }
                }
            }
        }
    }

    private fun checkValid(){
        val textPhone = binding.tfEditPhone.text?.trim().toString()

        var isValid = true

        if (textPhone.length < 10) {
            binding.tfLayoutPhone.error = "Isi Nomor Telepon dengan benar"
            isValid = false
        } else {
            binding.tfLayoutPhone.error = null
        }

        binding.btnConfirm.isEnabled = isValid
    }

    private fun checkInput(){
        binding.tfEditPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkValid()
            }
            override fun afterTextChanged(string: Editable?) {}
        })
    }
}