package com.einz.solnetcs.ui.cust.new_ticket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.einz.solnetcs.data.di.ViewModelFactory
import com.einz.solnetcs.databinding.ActivityNewTicketBinding
import com.google.firebase.auth.FirebaseAuth
import com.einz.solnetcs.data.State
import com.einz.solnetcs.data.model.FirebaseTimestamp
import com.einz.solnetcs.data.model.Laporan
import com.einz.solnetcs.data.model.gangguan
import com.einz.solnetcs.ui.auth.login.LoginActivity
import com.einz.solnetcs.ui.cust.active_ticket.ActiveTicketActivity
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NewTicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewTicketBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: NewTicketViewModel by viewModels{factory}

    private val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var idCustomer: String
    private lateinit var daerahCustomer: String
    private lateinit var alamatCustomer: String
    private lateinit var telponCustomer: String

    private lateinit var kategoriKeluhan:String
    private lateinit var deskripsiKeluhan:String
    private lateinit var waktuKeluhan: FirebaseTimestamp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)

        viewModel.getCustomer()
        viewModel.customerLiveData.observe(this){
            result ->
            when(result){
                is State.Success -> {
                    idCustomer = result.data?.idCustomer.toString()
                    daerahCustomer = result.data?.daerahCustomer.toString()
                    alamatCustomer = result.data?.alamatCustomer.toString()
                    telponCustomer = result.data?.noTelpCustomer.toString()

                }
                is State.Error -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
                is State.Loading -> {

                }
            }
        }

        binding.btnReport.isEnabled = false

        populateGangguanSpinner()
        populateSpinnerWithAvailableTimes()
        checkValid()

        binding.btnReport.setOnClickListener {
            var idLaporan = ""

            var idDaerah = if (daerahCustomer == "Tanjungpinang") {
                "SI/TP"
            } else {
                "SI/BTN"
            }

            val currentTime = Calendar.getInstance().time
            val firebaseTimestampNow = FirebaseTimestamp.fromTimestamp(Timestamp(Calendar.getInstance().time))
            val timestampCode = SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault()).format(currentTime)
            val timestampCodeString = timestampCode.toString()

            idLaporan = "$idDaerah/$idCustomer/$timestampCodeString"

            val Laporan = Laporan(
                idLaporan,
                idCustomer,
                daerahCustomer,
                alamatCustomer,
                kategoriKeluhan,
                deskripsiKeluhan,
                0,
                waktuKeluhan,
                firebaseTimestampNow,
                telponCustomer,
                "",
                null
            )

            //message dialog to confirm if user want to submit the report, showing current address and preferred time
            val dialog = android.app.AlertDialog.Builder(this)
            dialog.setTitle("Konfirmasi")
            val formattedTime = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()).format(waktuKeluhan.toTimestamp().toDate())
            dialog.setMessage("Laporkan gangguan di \n$alamatCustomer\nTeknisi akan datang pada\n$formattedTime")
            dialog.setPositiveButton("Ya") { _, _ ->
                viewModel.createLaporan(idCustomer,Laporan)
                viewModel.createLaporanLiveData.observe(this){
                        result ->
                    when(result){
                        is State.Success -> {
                            Toast.makeText(this, "Laporan berhasil dikirim", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, ActiveTicketActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        is State.Error -> {
                            Toast.makeText(this, "Laporan gagal dikirim", Toast.LENGTH_SHORT).show()
                        }
                        is State.Loading -> {
                            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            dialog.setNegativeButton("Batal") { _, _ ->
                Toast.makeText(this, "Laporan dibatalkan", Toast.LENGTH_SHORT).show()
            }
            dialog.show()
        }

    }

    private fun populateSpinnerWithAvailableTimes() {
        val spinner = binding.spinnerSelectTime
        val textViewStatus = binding.tvTimestamp

        val currentTime = Calendar.getInstance()
        val availableTimes = ArrayList<String>()

        // Set working hours
        val workingHoursStart = 8
        val workingHoursEnd = 23

        // Determine if we need to show today's or tomorrow's times
        var showTodayTimes = true
        val maxTimeToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, workingHoursEnd)
            set(Calendar.MINUTE, 0)
        }
        if (currentTime.after(maxTimeToday) || currentTime.get(Calendar.HOUR_OF_DAY) >= workingHoursEnd - 1) {
            showTodayTimes = false
        }

        // Setting up the calendar for time slots
        val timeSlotsCalendar = Calendar.getInstance().apply {
            if (!showTodayTimes) {
                // If we're showing tomorrow's times
                add(Calendar.DAY_OF_MONTH, 1)
            }
            set(Calendar.HOUR_OF_DAY, workingHoursStart)
            set(Calendar.MINUTE, 0)
        }

        // Populate available times
        while (timeSlotsCalendar.get(Calendar.HOUR_OF_DAY) < workingHoursEnd) {
            if (showTodayTimes && timeSlotsCalendar.after(currentTime)) {
                // Only add times for today that are after the current time
                availableTimes.add(SimpleDateFormat("HH:mm", Locale.getDefault()).format(timeSlotsCalendar.time))
            } else if (!showTodayTimes) {
                // Add all times for tomorrow
                availableTimes.add(SimpleDateFormat("HH:mm", Locale.getDefault()).format(timeSlotsCalendar.time))
            }
            timeSlotsCalendar.add(Calendar.HOUR_OF_DAY, 1)
        }

        // Set the appropriate date label
        val dateLabel = if (showTodayTimes) "Hari ini" else "Besok"

        // ArrayAdapter for the Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, availableTimes).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter

        // Item selected listener for Spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedTime = parent.getItemAtPosition(position).toString()
                val selectedDateCalendar = Calendar.getInstance()

                if (dateLabel == "Besok") {
                    selectedDateCalendar.add(Calendar.DAY_OF_MONTH, 1)
                }
                val timeParts = selectedTime.split(":").map { it.toInt() }
                selectedDateCalendar.set(Calendar.HOUR_OF_DAY, timeParts[0])
                selectedDateCalendar.set(Calendar.MINUTE, timeParts[1])

                // Convert Calendar instance to Timestamp
                waktuKeluhan = FirebaseTimestamp.fromTimestamp(Timestamp(Date(selectedDateCalendar.timeInMillis)))

                textViewStatus.text = "$dateLabel $selectedTime"
                checkInput()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional: Handle nothing selected
            }
        }
    }

    private fun populateGangguanSpinner(){
        val spinner = binding.spinnerGangguan

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gangguan).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                kategoriKeluhan = parent.getItemAtPosition(position).toString()
                checkInput()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional: Handle nothing selected
            }
        }
    }

    private fun checkValid(){
        binding.tfEditDeskripsi.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkInput()
            }
            override fun afterTextChanged(string: Editable?) {}
        })
    }

    private fun checkInput(){
        deskripsiKeluhan = binding.tfEditDeskripsi.text.toString().trim()


        if(deskripsiKeluhan.isEmpty()){
            binding.btnReport.error = "Deskripsi tidak boleh kosong"
            binding.tfEditDeskripsi.requestFocus()
            binding.btnReport.isEnabled = false
            return
        }

        if(kategoriKeluhan.isEmpty()){
            binding.btnReport.error = "Mohon pilih kategori gangguan"
            binding.spinnerGangguan.requestFocus()
            binding.btnReport.isEnabled = false
            return
        }
        if(waktuKeluhan == null){
            binding.btnReport.error = "Mohon pilih waktu gangguan"
            binding.spinnerSelectTime.requestFocus()
            binding.btnReport.isEnabled = false
            return
        }

        binding.btnReport.error = null
        binding.btnReport.isEnabled = true
    }






}