package com.einz.solnetcs.ui.auth.register

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.einz.solnetcs.R
import com.einz.solnetcs.data.State
import com.einz.solnetcs.data.di.ViewModelFactory
import com.einz.solnetcs.databinding.ActivityNewRegisterBinding
import com.einz.solnetcs.databinding.ActivityRegisterBinding
import com.einz.solnetcs.ui.auth.login.LoginActivity
import com.einz.solnetcs.util.ErrorDialog
import com.einz.solnetcs.util.phoneValidator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class NewRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewRegisterBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: RegisterViewModel by viewModels{factory}

    var location = "Tanjungpinang"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)

        viewModel.verifyLiveData.observe(this){result->
            when(result) {
                is State.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.apply{
                        tfEditFullName.setText(result.data?.namaCustomer)
                        tfEditAlamat.setText(result.data?.alamatCustomer)
                        tfEditPhone.setText(result.data?.noTelpCustomer)
                        tfEditIdPelanggan.setText(result.data?.idCustomer.toString())
                        if(result.data?.daerahCustomer.equals("Tanjungpinang")){
                            spinnerLocation.setSelection(0)
                        }
                        else if(result.data?.daerahCustomer.equals("Bintan")){
                            spinnerLocation.setSelection(1)
                        }

                        freezeLayout(tfLayoutFullName)
                        freezeLayout(tfLayoutAlamat)
                        freezeLayout(tfLayoutPhone)
                        freezeLayout(tfLayoutIdPelanggan)

                        freezeField(tfEditFullName)
                        freezeField(tfEditAlamat)
                        freezeField(tfEditPhone)
                        freezeField(tfEditIdPelanggan)

                        spinnerLocation.isEnabled = false

                    }

                }

                is State.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is State.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        binding.btnRegister.isEnabled = false

        binding.btnRegister.setOnClickListener{
            val email = binding.tfEditEmail.text?.trim().toString()
            val password = binding.tfEditPassword.text?.trim().toString()
            val idPelanggan = binding.tfEditIdPelanggan.text?.trim().toString()

            viewModel.newRegister(idPelanggan.toInt(), email, password)
        }

        viewModel.userLiveData.observe(this){result->
            when(result) {
                is State.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val intent = Intent(this, LoginActivity::class.java)
                    Toast.makeText(this, "Pendaftara Berhasil, silahkan masuk", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                    finish()
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


        checkValid()
        setupSpinner()
    }

    private fun setupSpinner() {

        val enumLocation = arrayOf<String?>("Tanjungpinang", "Bintan")
        val locationSpinner = binding.spinnerLocation
        val locationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, enumLocation)
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationSpinner.adapter = locationAdapter
        locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                location = locationSpinner.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                locationSpinner.setSelection(0)
            }

        }
    }
    private fun checkInput(){
        val textEmail = binding.tfEditEmail.text?.trim().toString()
        val textPassword = binding.tfEditPassword.text?.trim().toString()
        val textConfirm = binding.tfEditPasswordConfirm.text?.trim().toString()
        val textName = binding.tfEditFullName.text?.trim().toString()
        val textPhone = binding.tfEditPhone.text?.trim().toString()
        val textAddress = binding.tfEditAlamat.text?.trim().toString()
        val idPelanggan = binding.tfEditIdPelanggan.text?.trim().toString()

        val location = binding.spinnerLocation.selectedItem.toString()

        var isValid = true

        if(location.equals("Tanjungpinang")){
            binding.tvIdPelanggan.text = "SI-TP"
        }
        else if(location.equals("Bintan")){
            binding.tvIdPelanggan.text = "SI-BTN"
        }


        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
            binding.tfLayoutEmail.error = "Pastikan Email benar"
            isValid = false
        } else {
            binding.tfLayoutEmail.error = null
        }

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

        if (textName.isEmpty()) {
            binding.tfLayoutFullName.error = "Isi Nama Lengkap"
            isValid = false
        } else {
            binding.tfLayoutFullName.error = null
        }

        if (textPhone.isEmpty()) {
            binding.tfLayoutPhone.error = "Isi Nomor Telpon"
            isValid = false
        } else {
            binding.tfLayoutPhone.error = null
            if( phoneValidator(this@NewRegisterActivity, textPhone).isEmpty() ){
                binding.tfLayoutPhone.error = "Nomor Telepon tidak valid"
                isValid = false
            }
            else{
                binding.tfLayoutPhone.error = null
            }
        }

        if (textAddress.isEmpty()) {
            binding.tfLayoutAlamat.error = "Isi Alamat"
            isValid = false
        } else {
            binding.tfLayoutAlamat.error = null
        }

        if (idPelanggan.length <6) {
            binding.tfLayoutIdPelanggan.error = "ID Pelanggan Anda"
            isValid = false
        } else {
            binding.tfLayoutIdPelanggan.error = null
        }

        binding.btnRegister.isEnabled = isValid
    }

    private fun checkValid(){
        binding.tfEditPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkInput()
            }
            override fun afterTextChanged(string: Editable?) {}
        })

        binding.tfEditPasswordConfirm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkInput()
            }
            override fun afterTextChanged(string: Editable?) {}
        })

        binding.tfEditIdPelanggan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkInput()
            }
            override fun afterTextChanged(string: Editable?) {}
        })

        binding.tfEditEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkInput()
            }
            override fun afterTextChanged(string: Editable?) {}
        })

        binding.tfEditFullName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkInput()
            }
            override fun afterTextChanged(string: Editable?) {}
        })

        binding.tfEditAlamat.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkInput()
            }
            override fun afterTextChanged(string: Editable?) {}
        })

        binding.tfEditPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkInput()
            }
            override fun afterTextChanged(string: Editable?) {}
        })

        binding.spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) { checkInput() }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                binding.spinnerLocation.setSelection(0)
            }

        }
    }

    // show error using a message box with an OK button to continue
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

    private fun freezeField(editText: TextInputEditText){
        editText.setTextColor(Color.parseColor("#000000")) // Custom disabled text color
        editText.isEnabled = false
    }

    private fun freezeLayout(textInputLayout: TextInputLayout){
        textInputLayout.boxStrokeColor = Color.parseColor("#000000") // Custom disabled box stroke color
    }
}