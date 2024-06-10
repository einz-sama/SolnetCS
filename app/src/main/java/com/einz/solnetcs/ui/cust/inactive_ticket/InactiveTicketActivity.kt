package com.einz.solnetcs.ui.cust.inactive_ticket

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.einz.solnetcs.R
import com.einz.solnetcs.data.di.ViewModelFactory
import com.einz.solnetcs.databinding.ActivityActiveTicketBinding
import com.einz.solnetcs.data.State
import com.einz.solnetcs.ui.auth.login.LoginActivity
import com.einz.solnetcs.ui.cust.customer.CustomerActivity
import java.text.SimpleDateFormat
import java.util.Locale
import android.provider.ContactsContract
import com.einz.solnetcs.ui.cust.history.HistoryActivity
import com.einz.solnetcs.util.phoneValidator

class InactiveTicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActiveTicketBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: InactiveTicketViewModel by viewModels{factory}

    private lateinit var idCustomer: String
    private lateinit var idLaporan:String
    private lateinit var teknisiPhone:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActiveTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)

        viewModel.getLaporanById(intent.getStringExtra(EXTRA_LAPORAN)!!)

        viewModel.laporanLiveData.observe(this){
                laporan ->
            when(laporan){
                is State.Success -> {
                    idLaporan = laporan.data?.idLaporan.toString()
                    val desiredTime = laporan.data?.desiredTime?.toTimestamp()?.toDate()
                    val formattedDesiredTime = if (desiredTime != null) {
                        SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()).format(desiredTime)
                    } else {
                        "Waktu tidak ditentukan"
                    }

                    binding.fabChat.setOnClickListener {
                        var teknisiPhone = laporan.data?.teknisiPhone
                        teknisiPhone = phoneValidator(this@InactiveTicketActivity,teknisiPhone!!)


                        try {
                            // Check if WhatsApp is installed
                            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA)

                            // Start WhatsApp chat
                            val intent = Intent(Intent.ACTION_VIEW)
                            val whatsappUrl = "https://wa.me/$teknisiPhone" // WhatsApp's URL scheme
                            intent.data = Uri.parse(whatsappUrl)
                            startActivity(intent)
                        } catch (e: PackageManager.NameNotFoundException) {
                            // WhatsApp not installed, open Contacts app to add a new contact
                            val intent = Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI).apply {
                                putExtra(ContactsContract.Intents.Insert.PHONE, teknisiPhone)
                                putExtra(ContactsContract.Intents.Insert.NAME, "Pelanggan")
                            }
                            startActivity(intent)
                        }
                    }

                    val timeStamp = laporan.data?.timestamp?.toTimestamp()?.toDate()
                    val formattedTimeStamp = if (timeStamp != null) {
                        SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(timeStamp)
                    } else {
                        "Waktu tidak ditentukan"
                    }

                    val time_process = laporan.data?.time_processed?.toTimestamp()?.toDate()
                    val formatted_time_process = if (time_process != null) {
                        SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(time_process)
                    } else {
                        "Waktu tidak ditentukan"
                    }

                    val time_start = laporan.data?.time_repair_started?.toTimestamp()?.toDate()
                    val formatted_time_start = if (time_start != null) {
                        SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(time_start)
                    } else {
                        "Waktu tidak ditentukan"
                    }


                    val time_finish = laporan.data?.time_repair_finished?.toTimestamp()?.toDate()
                    val formatted_time_finish = if (time_finish != null) {
                        SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(time_finish)
                    } else {
                        "Waktu tidak ditentukan"
                    }

                    val time_closed = laporan.data?.time_repair_closed?.toTimestamp()?.toDate()
                    val formatted_time_closed = if (time_closed != null) {
                        SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(time_closed)
                    } else {
                        "Waktu tidak ditentukan"
                    }

                    binding.apply{
                        tvDateReported.text = formattedTimeStamp
                        tvInfo.text = "Penanganan diharapkan pada $formattedDesiredTime"
                        tvNoReferensi.text = laporan.data?.idLaporan
                        tvJenisGangguan.text = laporan.data?.kategori

                    }

                    when (laporan.data?.status) {
                        0 -> {
                            binding.apply{
                                view1.visibility = View.VISIBLE
                                tvInfo.visibility = View.VISIBLE

                                tvInfo.text = "Teknisi akan datang pada: \n $formattedDesiredTime"
                                fabChat.visibility = View.GONE
                                btnConfirm.visibility = View.GONE

                                dotProgress0.setBackgroundResource(R.drawable.progress_dot)
                                tvProgress0.text = "Laporan sedang diproses"

                                dotProgress1.setBackgroundResource(R.drawable.progress_dot_inactive)
                                tvProgress1.text = "Menunggu Penanganan"

                                dotProgress2.setBackgroundResource(R.drawable.progress_dot_inactive)
                                tvProgress2.text = "Konfirmasi Penanganan"

                                dotProgress3.setBackgroundResource(R.drawable.progress_dot_inactive)
                                tvProgress3.text = "Penanganan Selesai"
                            }


                        }
                        1 -> {
                            binding.apply{
                                view1.visibility = View.GONE
                                tvInfo.visibility = View.GONE

                                fabChat.visibility = View.VISIBLE
                                btnConfirm.visibility = View.GONE

                                tvTeknisi.text = "Laporan anda sedang ditangani oleh ${laporan.data?.teknisi} "
                                tvTeknisiDesc.text = "ID Teknisi: ${laporan.data?.idTeknisi}"

                                dotProgress0.setBackgroundResource(R.drawable.progress_dot)
                                tvProgress0.text = "Laporan sudah diproses"
                                tvProgress0Timestamp.text = formatted_time_process

                                dotProgress1.setBackgroundResource(R.drawable.progress_dot)
                                tvProgress1.text = "Penanganan sedang dilakukan"

                                dotProgress2.setBackgroundResource(R.drawable.progress_dot_inactive)
                                tvProgress2.text = "Konfirmasi Penanganan"

                                dotProgress3.setBackgroundResource(R.drawable.progress_dot_inactive)
                                tvProgress3.text = "Penanganan Selesai"
                            }

                        }
                        2 -> {
                            binding.apply{
                                view1.visibility = View.GONE
                                tvInfo.visibility = View.GONE

                                fabChat.visibility = View.VISIBLE
                                btnConfirm.visibility = View.VISIBLE

                                tvTeknisi.text = "Menunggu Konfirmasi Penanganan Selesai"
                                tvTeknisiDesc.text = laporan.data.teknisi

                                dotProgress0.setBackgroundResource(R.drawable.progress_dot)
                                tvProgress0.text = "Laporan sudah diproses"
                                tvProgress0Timestamp.text = formatted_time_process

                                dotProgress1.setBackgroundResource(R.drawable.progress_dot)
                                tvProgress1.text = "Penanganan sudah selesai"
                                tvProgress1Timestamp.text = formatted_time_start

                                dotProgress2.setBackgroundResource(R.drawable.progress_dot)
                                tvProgress2.text = "Menunggu konfirmasi"

                                dotProgress3.setBackgroundResource(R.drawable.progress_dot_inactive)
                                tvProgress3.text = "Penanganan Selesai"

                                btnConfirm.visibility = View.VISIBLE
                            }

                        }
                        3 -> {
                            binding.apply{
                                view1.visibility = View.GONE
                                tvInfo.visibility = View.GONE

                                fabChat.visibility = View.VISIBLE
                                btnConfirm.visibility = View.GONE

                                tvTeknisi.text = "Penanganan telah selesai dilakukan"
                                tvTeknisiDesc.text = "oleh ${laporan.data?.teknisi} : ${laporan.data?.solution} "

                                dotProgress0.setBackgroundResource(R.drawable.progress_dot)
                                tvProgress0.text = "Laporan sudah diproses"
                                tvProgress0Timestamp.text = formatted_time_process

                                dotProgress1.setBackgroundResource(R.drawable.progress_dot)
                                tvProgress1.text = "Penanganan sudah selesai"
                                tvProgress1Timestamp.text = formatted_time_start

                                dotProgress2.setBackgroundResource(R.drawable.progress_dot)
                                tvProgress2.text = "Penanganan dikonfirmasi"
                                tvProgress2Timestamp.text = formatted_time_finish

                                dotProgress3.setBackgroundResource(R.drawable.progress_dot)
                                tvProgress3.text = "Penanganan Selesai"

                            }

                        }
                        4 -> {

                            binding.apply{
                                view1.visibility = View.GONE
                                tvInfo.visibility = View.GONE

                                fabChat.visibility = View.GONE
                                btnConfirm.visibility = View.GONE
                                btnDone.visibility = View.VISIBLE
                                btnDone.setOnClickListener {
                                    finish()
                                }

                                tvTeknisi.text = "Tiket Pengaduan Ditutup"
                                tvTeknisiDesc.text = "oleh ${laporan.data?.teknisi} : ${laporan.data?.solution} "

                                dotProgress0.setBackgroundResource(R.drawable.progress_dot)
                                tvProgress0.text = "Laporan sudah diproses"
                                tvProgress0Timestamp.text = formatted_time_process

                                dotProgress1.setBackgroundResource(R.drawable.progress_dot)
                                tvProgress1.text = "Penanganan sudah selesai"
                                tvProgress1Timestamp.text = formatted_time_start

                                dotProgress2.setBackgroundResource(R.drawable.progress_dot)
                                tvProgress2.text = "Penanganan dikonfirmasi"
                                tvProgress2Timestamp.text = formatted_time_finish

                                dotProgress3.setBackgroundResource(R.drawable.progress_dot)
                                tvProgress3.text = "Penanganan Selesai"
                                tvProgress3Timestamp.text = formatted_time_closed

                            }

                        }
                    }

                }
                is State.Error -> {
//                    val intent = Intent(this, CustomerActivity::class.java)
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(intent)
//                    finish()
                }
                is State.Loading -> {

                }
            }
        }


    }
    companion object {
        const val EXTRA_LAPORAN = "idLaporan"
    }
}