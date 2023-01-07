package com.example.remotebindingservicesidedemo

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.remotebindingservicesidedemo.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var serviceIntent: Intent
    private lateinit var jobScheduler: JobScheduler
    private lateinit var workManager: WorkManager
    private lateinit var workRequest: WorkRequest
    private lateinit var workRequest1: OneTimeWorkRequest
    private lateinit var workRequest2: OneTimeWorkRequest
    private lateinit var workRequest3: OneTimeWorkRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceIntent = Intent(applicationContext, MyService::class.java)
        jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        workManager = WorkManager.getInstance(this)
        workRequest = PeriodicWorkRequest.Builder(MyWorker::class.java,15,TimeUnit.MINUTES).build()
        workRequest1 = OneTimeWorkRequest.Builder(MyWorker::class.java).addTag("Work1").build()
        workRequest2 = OneTimeWorkRequest.Builder(MyWorker::class.java).addTag("Work2").build()
        workRequest3 = OneTimeWorkRequest.Builder(MyWorker::class.java).addTag("Work3").build()

        binding.btnStart.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startService(serviceIntent)
//            }else{
//                startService(serviceIntent)
//            }
//            Toast.makeText(this,"Service Started",Toast.LENGTH_SHORT).show()


            //startJob()
            //startWorker()
            startChainWorker()

        }

        binding.btnStop.setOnClickListener {
            //stopService(serviceIntent)
            //stopJob()
            //stopWorker()
            stopWorkByTag(tag = "Work2")
        }
    }

    private fun startWorker() {
        workManager.enqueue(workRequest)
    }

    private fun startChainWorker() {
        /*
        * As of now you cant use work chaining with periodic work request.
        * It only works with one time request.
        * */
        workManager.beginWith(listOf(workRequest1,workRequest2)).then(workRequest3).enqueue()
    }

    private fun stopWorker() {
        workManager.cancelWorkById(workRequest.id)
    }

    private fun stopWorkByTag(tag:String) {
        workManager.cancelAllWorkByTag(tag)
    }

    private fun startJob() {
        val componentName = ComponentName(this, MyJobService::class.java)
        val jobInfo = JobInfo.Builder(102, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPeriodic(15 * 60 * 1000)
            .setRequiresCharging(false)
            .setPersisted(true)
            .build()

        jobScheduler.schedule(jobInfo).also {
            Log.d(
                "MainActivity",
                if (it == JobScheduler.RESULT_SUCCESS) "successfully job scheduled"
                else "failed to schedule job"
            )
        }

    }

    private fun stopJob() {
        jobScheduler.cancel(102)
    }
}