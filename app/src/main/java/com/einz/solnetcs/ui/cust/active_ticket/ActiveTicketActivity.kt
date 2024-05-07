package com.einz.solnetcs.ui.cust.active_ticket

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
import com.einz.solnetcs.util.phoneValidator

class ActiveTicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActiveTicketBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: ActiveTicketViewModel by viewModels{factory}

    private lateinit var idCustomer: String
    private lateinit var idLaporan:String
    private lateinit var teknisiPhone:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActiveTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)



        viewModel.getCustomer()
        viewModel.customerLiveData.observe(this){
            custData ->
            when(custData){
                is State.Success -> {
                    idCustomer = custData.data?.idCustomer.toString()
                    viewModel.getLaporan(idCustomer)
                    viewModel.getLaporanLiveData.observe(this){
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
                                    val teknisiName = laporan.data?.teknisi
                                    teknisiPhone = phoneValidator(this@ActiveTicketActivity,teknisiPhone!!)


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
                                            putExtra(ContactsContract.Intents.Insert.NAME, teknisiPhone)
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

                                binding.apply{
                                    tvDateReported.text = formattedTimeStamp
                                    tvInfo.text = "Perbaikan diharapkan pada $formattedDesiredTime"
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
                                            tvProgress1.text = "Menunggu perbaikan"

                                            dotProgress2.setBackgroundResource(R.drawable.progress_dot_inactive)
                                            tvProgress2.text = "Konfirmasi Perbaikan"

                                            dotProgress3.setBackgroundResource(R.drawable.progress_dot_inactive)
                                            tvProgress3.text = "Perbaikan Selesai"
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

                                            dotProgress1.setBackgroundResource(R.drawable.progress_dot)
                                            tvProgress1.text = "Perbaikan sedang dilakukan"

                                            dotProgress2.setBackgroundResource(R.drawable.progress_dot_inactive)
                                            tvProgress2.text = "Konfirmasi Perbaikan"

                                            dotProgress3.setBackgroundResource(R.drawable.progress_dot_inactive)
                                            tvProgress3.text = "Perbaikan Selesai"
                                        }

                                    }
                                    2 -> {
                                        binding.apply{
                                            view1.visibility = View.GONE
                                            tvInfo.visibility = View.GONE

                                            fabChat.visibility = View.VISIBLE
                                            btnConfirm.visibility = View.VISIBLE

                                            tvTeknisi.text = "Menunggu Konfirmasi Perbaikan Selesai"
                                            tvTeknisiDesc.text = laporan.data.teknisi

                                            dotProgress0.setBackgroundResource(R.drawable.progress_dot)
                                            tvProgress0.text = "Laporan sudah diproses"

                                            dotProgress1.setBackgroundResource(R.drawable.progress_dot)
                                            tvProgress1.text = "Perbaikan sudah selesai"

                                            dotProgress2.setBackgroundResource(R.drawable.progress_dot)
                                            tvProgress2.text = "Menunggu konfirmasi"

                                            dotProgress3.setBackgroundResource(R.drawable.progress_dot_inactive)
                                            tvProgress3.text = "Perbaikan Selesai"

                                            btnConfirm.visibility = View.VISIBLE
                                        }

                                    }
                                    3 -> {
                                        binding.apply{
                                            view1.visibility = View.GONE
                                            tvInfo.visibility = View.GONE

                                            fabChat.visibility = View.VISIBLE
                                            btnConfirm.visibility = View.GONE

                                            tvTeknisi.text = "Perbaikan telah selesai dilakukan"
                                            tvTeknisiDesc.text = laporan.data.teknisi

                                            dotProgress0.setBackgroundResource(R.drawable.progress_dot)
                                            tvProgress0.text = "Laporan sudah diproses"

                                            dotProgress1.setBackgroundResource(R.drawable.progress_dot)
                                            tvProgress1.text = "Perbaikan sudah selesai"

                                            dotProgress2.setBackgroundResource(R.drawable.progress_dot)
                                            tvProgress2.text = "Perbaikan dikonfirmasi"

                                            dotProgress3.setBackgroundResource(R.drawable.progress_dot)
                                            tvProgress3.text = "Perbaikan Selesai"

                                        }

                                    }
                                    4 -> {

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
                is State.Error -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                is State.Loading -> {

                }
            }
        }





        binding.btnConfirm.setOnClickListener {

            val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            dialog.setTitle("Konfirmasi Perbaikan")
            dialog.setMessage("Apakah anda yakin ingin melakukan konfirmasi perbaikan?")
            dialog.setPositiveButton("Ya") { _, _ ->
                viewModel.finishLaporan(idLaporan, 4)

            }
            dialog.setNegativeButton("Tidak") { _, _ ->}
            dialog.show()

        }

        viewModel.laporanDoneLiveData.observe(this){
                result ->
            when(result){
                is State.Success -> {
                    viewModel.resetLaporanDone()
                    //dialog to show laporan is completed, show OK button
                    val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
                    dialog.setTitle("Perbaikan Selesai")
                    dialog.setMessage("Perbaikan selesai dilakukan, jika ada gangguan silahkan buat laporan \n terimakasih telah setia menggunakan Solnet! 😀")
                    dialog.setPositiveButton("OK") { _, _ ->
                        val intent = Intent(this, CustomerActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    dialog.setOnDismissListener() {
                        val intent = Intent(this, CustomerActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    dialog.show()

                }
                is State.Error -> {
                    Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                }
                is State.Loading -> {

                }

            }
        }

    }
}