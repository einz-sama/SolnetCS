package com.einz.solnetcs.ui.cust

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.einz.solnetcs.data.LoginPreferences
import com.einz.solnetcs.data.di.ViewModelFactory
import com.einz.solnetcs.databinding.ActivityCustomerBinding
import com.einz.solnetcs.data.Result

class CustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: CustomerViewModel by viewModels{factory}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)





    }
}