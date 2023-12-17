package com.einz.solnetcs.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.einz.solnetcs.data.LoginPreferences
import com.einz.solnetcs.data.di.ViewModelFactory
import com.einz.solnetcs.databinding.ActivityLoginBinding
import com.einz.solnetcs.util.checkPassword
import com.einz.solnetcs.util.checkUsername
import com.einz.solnetcs.data.Result
import com.einz.solnetcs.ui.cust.CustomerActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: LoginViewModel by viewModels{factory}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)
        binding.btnMasuk.isEnabled = false

        checkValid()

        binding.btnMasuk.setOnClickListener{

//            binding.progressBar.visibility = View.VISIBLE
            val username = binding.editTextUsername.text?.trim().toString()
            val password = binding.editTextPassword.text?.trim().toString()

            try{
                viewModel.login(username, password)
                login()
            }
            catch(e: Exception){

                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }

        }

    }


    private fun login(){
        viewModel.responseLogin.observe(this){login->
            when(login){
                is Result.Success->{
                    val loginPref = LoginPreferences(this)
                    Log.d("TESTING", "loginpreference ID: ${login.data.idCustomer}")
                    login.data.idCustomer?.let { loginPref.setIdCustomer(it)}
                    Toast.makeText(this,"Login Success",Toast.LENGTH_SHORT).show()
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val intent = Intent(this, CustomerActivity::class.java)
                    startActivity(intent)
                }
                is Result.Error->{
//                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Login ${login.errorMessage}" , Toast.LENGTH_SHORT).show()
                }
                is Result.Loading->{
//                    binding.progressBar.visibility = View.VISIBLE
                    Toast.makeText(this, "Loading..}" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkValid(){
        var usernameValid = false
        var passwordValid = false

        binding.editTextUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkUsername(string?.trim().toString()).let {
//                    binding.btnMasuk.isEnabled = it
                    usernameValid = it
                    binding.btnMasuk.isEnabled = usernameValid && passwordValid
                }
            }

            override fun afterTextChanged(string: Editable?) {}
        })

        binding.editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkPassword(string?.trim().toString()).let {
//                    binding.btnMasuk.isEnabled = it
                    passwordValid = it
                    binding.btnMasuk.isEnabled = usernameValid && passwordValid
                }
            }

            override fun afterTextChanged(string: Editable?) {}
        })
    }
}