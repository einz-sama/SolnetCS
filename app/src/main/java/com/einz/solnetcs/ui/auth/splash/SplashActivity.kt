package com.einz.solnetcs.ui.auth.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.einz.solnetcs.databinding.ActivitySplashBinding
import com.einz.solnetcs.ui.auth.login.LoginActivity
import com.einz.solnetcs.ui.cust.customer.CustomerActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding
    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Handler(Looper.getMainLooper()).postDelayed({
            if (firebaseAuth.currentUser != null) {
                val intent = Intent(this, CustomerActivity::class.java)
                finish()
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                Toast.makeText(this, "Anda perlu login", Toast.LENGTH_SHORT).show()
                finish()
                startActivity(intent)

            }
        }, 3000L)
    }
}