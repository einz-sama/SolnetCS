package com.einz.solnetcs.ui.cust.customer

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.einz.solnetcs.data.di.ViewModelFactory
import com.einz.solnetcs.databinding.ActivityCustomerBinding
import com.einz.solnetcs.data.State
import com.einz.solnetcs.data.model.Customer
import com.einz.solnetcs.ui.auth.login.LoginActivity
import com.einz.solnetcs.ui.cust.active_ticket.ActiveTicketActivity
import com.einz.solnetcs.ui.cust.info.FaqActivity
import com.einz.solnetcs.ui.cust.helpdesk.HelpdeskActivity
import com.einz.solnetcs.ui.cust.history.HistoryActivity
import com.einz.solnetcs.ui.cust.new_ticket.NewTicketActivity
import com.einz.solnetcs.ui.cust.setting.SettingActivity
import com.einz.solnetcs.util.observeOnce

class CustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerBinding

    private lateinit var factory: ViewModelFactory
    private val viewModel: CustomerViewModel by viewModels{factory}

    private var cachedCustomerData: Customer? = null

    private lateinit var idCustomer: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)



        viewModel.getCustomer()

        viewModel.customerLiveData.observe(this@CustomerActivity) { result ->
            when(result){
                is State.Success -> {
                    cachedCustomerData = result.data
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
                        idPelanggan.text = "ID PELANGGAN: ${header}${result.data?.idCustomer}"
                        alamat.text = "${result.data?.alamatCustomer}"
                        daerah.text = "${result.data?.daerahCustomer}"
                        telepon.text = "${result.data?.noTelpCustomer}"
                        showLoading(false)
                        setupViews()
                    }
                }
                is State.Error -> {
                    Log.d("CustomerActivity", "Error: ${result.errorMessage}")
                    showLoading(false)
                }
                is State.Loading -> {
                    showLoading(true)
                    Log.d("CustomerActivity", "Loading...")
                }

                else -> {
                    Toast.makeText(this@CustomerActivity, "Login Failed", Toast.LENGTH_SHORT).show()}
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


    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setupViews(){
        binding.apply{

            ticket.setOnClickListener(){
                viewModel.checkLaporan(idCustomer)
                viewModel.checkLaporanLiveData.observeOnce(this@CustomerActivity) { result ->
                    when(result){
                        is State.Success -> {
                            showLoading(false)
                            if(result.data == true){
                                val intent = Intent(this@CustomerActivity, ActiveTicketActivity::class.java)
                                startActivity(intent)
                            } else{
                                val intent = Intent(this@CustomerActivity, NewTicketActivity::class.java)
                                startActivity(intent)
                            }

                        }
                        is State.Error -> {
                            showLoading(false)
                            Log.d("CustomerActivity", "Error: ${result.errorMessage}")
                        }
                        is State.Loading -> {
                            showLoading(true)
                        }

                        else -> {
                            showLoading(false)
                            Toast.makeText(this@CustomerActivity, "Login Failed", Toast.LENGTH_SHORT).show()}
                    }
                }
            }
            helpdesk.setOnClickListener(){
                val intent = Intent(this@CustomerActivity, HelpdeskActivity::class.java)
                startActivity(intent)
            }
            setting.setOnClickListener(){
                val intent = Intent(this@CustomerActivity, SettingActivity::class.java)
                startActivity(intent)
            }
            logout.setOnClickListener {
                // Create a confirmation dialog
                val builder = AlertDialog.Builder(this@CustomerActivity)
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
            info.setOnClickListener(){
                val intent = Intent(this@CustomerActivity, FaqActivity::class.java)
                startActivity(intent)
            }
            riwayat.setOnClickListener(){
                val intent = Intent(this@CustomerActivity, HistoryActivity::class.java)
                intent.putExtra(HistoryActivity.EXTRA_CUSTOMER, idCustomer)
                startActivity(intent)

            }


        }
    }

}