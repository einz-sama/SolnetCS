package com.einz.solnetcs.ui.cust.helpdesk

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.einz.solnetcs.R
import com.einz.solnetcs.data.Result
import com.einz.solnetcs.data.di.ViewModelFactory
import com.einz.solnetcs.data.model.Customer
import com.einz.solnetcs.data.model.whatsapp
import com.einz.solnetcs.databinding.ActivityHelpdeskActivityBinding
import com.einz.solnetcs.ui.cust.active_ticket.ActiveTicketActivity
import com.einz.solnetcs.ui.cust.customer.CustomerViewModel
import com.einz.solnetcs.ui.cust.info.FaqActivity
import com.einz.solnetcs.ui.cust.new_ticket.NewTicketActivity

class HelpdeskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpdeskActivityBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: CustomerViewModel by viewModels{factory}

    private lateinit var idCustomer: String
    private var cachedCustomerData: Customer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpdeskActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)

        if (cachedCustomerData == null) {
            viewModel.getCustomer()
            viewModel.customerLiveData.observe(this){
                    result ->
                when(result){
                    is Result.Success -> {
                        cachedCustomerData = result.data
                        idCustomer = result.data?.idCustomer.toString()
                        binding.progressBar.visibility = View.GONE
                    }
                    is Result.Error -> {
                        Log.d("CustomerActivity", "Error: ${result.errorMessage}")
                        binding.progressBar.visibility = View.GONE
                    }
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        Log.d("CustomerActivity", "Loading...")
                    }

                }
            }
        } else {
            binding.progressBar.visibility = View.GONE
        }



        binding.apply{
            btnLaporan.setOnClickListener {
                viewModel.checkLaporan(idCustomer)
                viewModel.checkLaporanLiveData.observe(this@HelpdeskActivity){
                        result ->
                    when(result){
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            if(result.data == true){
                                val intent = Intent(this@HelpdeskActivity, ActiveTicketActivity::class.java)
                                startActivity(intent)
                            } else{
                                val intent = Intent(this@HelpdeskActivity, NewTicketActivity::class.java)
                                startActivity(intent)
                            }

                        }
                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Log.d("CustomerActivity", "Error: ${result.errorMessage}")
                        }
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                    }
                }
            }
            btnFaq.setOnClickListener {
                val intent = Intent(this@HelpdeskActivity, FaqActivity::class.java)
                startActivity(intent)
            }
            btnBot.setOnClickListener {
                //implicit intent to open whatsapp on phone number +62812345678
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setPackage("com.whatsapp")
                intent.data = Uri.parse("https://wa.me/$whatsapp")
                startActivity(intent)
            }
        }
    }
}