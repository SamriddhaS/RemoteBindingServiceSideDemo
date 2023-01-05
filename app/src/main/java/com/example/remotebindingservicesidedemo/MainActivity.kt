package com.example.remotebindingservicesidedemo

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.remotebindingservicesidedemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var serviceIntent:Intent
    private lateinit var jobScheduler:JobScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceIntent = Intent(applicationContext,MyService::class.java)
        jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler

        binding.btnStart.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startService(serviceIntent)
//            }else{
//                startService(serviceIntent)
//            }
//            Toast.makeText(this,"Service Started",Toast.LENGTH_SHORT).show()
            startJob()
        }

        binding.btnStop.setOnClickListener {
            //stopService(serviceIntent)
            stopJob()
        }
    }

    private fun startJob(){
        val componentName = ComponentName(this,MyJobService::class.java)
        val jobInfo = JobInfo.Builder(102,componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_CELLULAR)
            .setPeriodic(15*60*1000)
            .setRequiresCharging(false)
            .setPersisted(true)
            .build()

        jobScheduler.schedule(jobInfo).also {
            Log.d("MainActivity",
                if (it==JobScheduler.RESULT_SUCCESS) "successfully job scheduled"
                else "failed to schedule job"
            )
        }

    }

    private fun stopJob(){
        jobScheduler.cancel(102)
    }
}