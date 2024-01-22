package com.einz.solnetcs.ui.cust.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import com.einz.solnetcs.R
import com.einz.solnetcs.data.di.ViewModelFactory
import com.einz.solnetcs.databinding.ActivityChangePasswordBinding
import com.einz.solnetcs.data.Result
import com.einz.solnetcs.ui.auth.login.LoginActivity

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var factory: ViewModelFactory

    private val viewModel: SettingViewModel by viewModels{factory}

    private lateinit var idCustomer:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfirm.isEnabled = false

        factory = ViewModelFactory.getInstance(this)
        viewModel.getCustomer()
        viewModel.customerLiveData.observe(this){
            result ->
            when(result){
                is Result.Success -> {
                    idCustomer = result.data?.idCustomer.toString()
                    binding.progressBar.visibility = View.GONE
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE

                }
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE

                }
            }
        }

        checkInput()

        binding.btnConfirm.setOnClickListener {
            val password = binding.tfEditPassword.text?.trim().toString()
            viewModel.changePassword(password)
            viewModel.changePasswordLiveData.observe(this){
                result ->
                when(result){
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun checkValid(){
        val textPassword = binding.tfEditPassword.text?.trim().toString()
        val textConfirm = binding.tfEditPasswordConfirm.text?.trim().toString()

        var isValid = true

        if (textPassword.length <= 7) {
            binding.tfLayoutPassword.error = "Password minimal 8 karakter"
            isValid = false
        } else {
            binding.tfLayoutPassword.error = null
        }

        if (textPassword != textConfirm) {
            binding.tfLayoutPasswordConfirm.error = "Pastikan password sama"
            isValid = false
        } else {
            binding.tfLayoutPasswordConfirm.error = null
        }

        binding.btnConfirm.isEnabled = isValid
    }

    private fun checkInput(){
        binding.tfEditPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkValid()
            }
            override fun afterTextChanged(string: Editable?) {}
        })

        binding.tfEditPasswordConfirm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkValid()
            }
            override fun afterTextChanged(string: Editable?) {}
        })
    }
}