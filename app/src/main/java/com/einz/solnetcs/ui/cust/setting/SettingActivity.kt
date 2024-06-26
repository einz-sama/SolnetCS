package com.einz.solnetcs.ui.cust.setting

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.einz.solnetcs.data.State
import com.einz.solnetcs.data.di.ViewModelFactory
import com.einz.solnetcs.databinding.ActivitySettingBinding
import com.einz.solnetcs.ui.auth.login.LoginActivity

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    private lateinit var factory: ViewModelFactory

    private val viewModel: SettingViewModel by viewModels{factory}

    private lateinit var idCustomer:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)

        viewModel.getCustomer()
        viewModel.customerLiveData.observe(this){
            result ->
            when(result){
                is State.Success -> {

                    binding.apply{
                        var header = "SI-TP"
                        header = if(result.data?.daerahCustomer.equals("Tanjungpinang")){
                            "SI-TP"
                        } else{
                            "SI-BTN"
                        }
                        idCustomer = result.data?.idCustomer.toString()
                        welcome.text = "Selamat Datang"
                        username.text = result.data?.namaCustomer
                        idPelanggan.text = "${header}${result.data?.idCustomer}"
                    }

                }
                is State.Error -> {


                }
                is State.Loading -> {


                }
            }
        }


        binding.apply{
            changePassword.setOnClickListener {
                val intent = Intent(this@SettingActivity, ChangePasswordActivity::class.java)
                startActivity(intent)
            }
            changeAddress.setOnClickListener {
                val intent = Intent(this@SettingActivity, ChangeAlamatActivity::class.java)
                startActivity(intent)
            }
            changePhone.setOnClickListener {
                val intent = Intent(this@SettingActivity, ChangePhoneActivity::class.java)
                startActivity(intent)
            }
            logout.setOnClickListener {
                // Create a confirmation dialog
                val builder = AlertDialog.Builder(this@SettingActivity)
                builder.setTitle("Konfirmasi Keluar")
                builder.setMessage("Apakah anda yakin untuk keluar akun?")

                // Set a positive button (Yes action)
                builder.setPositiveButton("Iya") { _: DialogInterface, _: Int ->
                    // User confirmed, perform the logout action
                    viewModel.logout()
                }

                // Set a negative button (Cancel action)
                builder.setNegativeButton("Batal") { dialog: DialogInterface, _: Int ->
                    // User canceled, dismiss the dialog
                    dialog.dismiss()
                }

                // Create and show the dialog
                val dialog = builder.create()
                dialog.show()
            }



        }

        viewModel.loggedOutLiveData.observe(this){
            result ->
            when(result){
                is State.Success -> {
                    if(result.data == true){
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }

                }
                is State.Error -> {

                }
                is State.Loading -> {
                    Log.d("SettingActivity", "Loading")
                }
            }
        }
    }
}