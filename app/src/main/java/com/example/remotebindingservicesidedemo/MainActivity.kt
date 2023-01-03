package com.example.remotebindingservicesidedemo

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.remotebindingservicesidedemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var serviceIntent:Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceIntent = Intent(applicationContext,MyService::class.java)

        binding.btnStart.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            }else{
                startService(serviceIntent)
            }
            Toast.makeText(this,"Service Started",Toast.LENGTH_SHORT).show();
        }

        binding.btnStop.setOnClickListener {
            stopService(serviceIntent)
        }
    }
}