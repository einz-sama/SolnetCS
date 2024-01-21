package com.einz.solnetcs.ui.cust.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.einz.solnetcs.R
import com.einz.solnetcs.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply{

        }
    }
}