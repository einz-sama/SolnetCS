package com.einz.solnetcs.ui.auth.forgot

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.einz.solnetcs.R
import com.einz.solnetcs.data.State
import com.einz.solnetcs.data.di.ViewModelFactory
import com.einz.solnetcs.databinding.ActivityForgotPasswordBinding
import com.einz.solnetcs.databinding.ActivityLoginBinding
import com.einz.solnetcs.ui.auth.login.LoginActivity
import com.einz.solnetcs.ui.auth.login.LoginViewModel

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: ForgotPasswordViewModel by viewModels{factory}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)
        binding.btnReset.isEnabled = false

        checkValid()

        binding.btnReset.setOnClickListener{
            val email = binding.tfEditEmail.text?.trim().toString()
            viewModel.resetPassword(email)
        }

        viewModel.resetPasswordLiveData.observe(this){
            result ->
            when(result){
                is State.Success -> {
                    Toast.makeText(this, "Permintaan reset berhasil diproses, mohon cek Email anda", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is State.Error -> {
                    Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
                is State.Loading -> {

                }
            }
        }
    }

    private fun checkInput(){
        val textEmail = binding.tfEditEmail.text?.trim().toString()

        if(android.util.Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                binding.btnReset.isEnabled = true
                binding.btnReset.error = null
                binding.tfLayoutEmail.error = null
        }
        else{
            binding.btnReset.isEnabled = false
            binding.tfLayoutEmail.error = "Email tidak valid!"
        }
    }

    private fun checkValid(){
        binding.tfEditEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkInput()
            }
            override fun afterTextChanged(string: Editable?) {}
        })
    }
}