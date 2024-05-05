package com.einz.solnetcs.ui.cust.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.einz.solnetcs.data.di.ViewModelFactory
import com.einz.solnetcs.databinding.ActivityHistoryBinding
import com.einz.solnetcs.data.State
import com.einz.solnetcs.ui.cust.inactive_ticket.InactiveTicketActivity

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: HistoryViewModel by viewModels{factory}
    private lateinit var idCustomer: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)


        idCustomer = intent.getStringExtra(EXTRA_CUSTOMER)!!


        viewModel.getFinishedLaporan(idCustomer)

        viewModel.listFinishedLaporan.observe(this) { result ->
            when (result) {
                is State.Success -> {

                    binding.progressBar.visibility = View.GONE

                    val adapterLaporan = LaporanAdapter(onClick = { laporan ->
                        val intent = Intent(this, InactiveTicketActivity::class.java)
                        intent.putExtra(InactiveTicketActivity.EXTRA_LAPORAN, laporan.idLaporan)
                        startActivity(intent)
                    })

                    with(binding.rvHistory) {
                        layoutManager = LinearLayoutManager(this@HistoryActivity)
                        setHasFixedSize(true)
                        this.adapter = adapterLaporan

                    }
                    adapterLaporan.submitList(null)
                    adapterLaporan.submitList(result.data)


                }

                is State.Error -> {
                    binding.progressBar.visibility = View.GONE
                }

                is State.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                else -> {}
            }
        }


    }
    companion object {
        const val EXTRA_CUSTOMER = "idCustomer"
    }
}