package com.example.remotebindingservicesidedemo
import android.app.job.JobParameters
import android.app.job.JobService
import android.os.*
import android.util.Log
import java.util.*

class MyJobService : JobService() {

    companion object {
        private val TAG = "MyService"
        private var mRandomNumber = 0
    }

    private var isRandomGenOn = false

    override fun onStartJob(p0: JobParameters?): Boolean {
        Log.d(TAG,"onStartJob")
        //startBgTask()
        startBgTask2(p0)
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        Log.d(TAG,"onStopJob")
        /*
        * Returning true means If the job is killed for some reason
        * I want it to be re scheduled.
        * */
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRandomNumberGenerator()
    }

    private fun startBgTask(){
        isRandomGenOn = true
        Thread {
            startRandomNumberGenerator()
        }.start()
    }
    private fun startBgTask2(p0: JobParameters?) {
        Thread {
            generateLimitedRandomNumber(p0)
        }.start()
    }

    private fun startRandomNumberGenerator() {
        while (isRandomGenOn) {
            try {
                Thread.sleep(1000)
                if (isRandomGenOn) {
                    mRandomNumber = Random().nextInt(1000) + 11
                    Log.i(TAG, "Random Number: $mRandomNumber")
                }
            } catch (e: InterruptedException) {
                Log.i(TAG, "Thread Interrupted")
            }
        }
    }

    private fun generateLimitedRandomNumber(p0: JobParameters?) {
        var count=0
        while (count<50) {
            try {
                Thread.sleep(1000)
                mRandomNumber = Random().nextInt(1000) + 11
                Log.i(TAG, "Random Number: $mRandomNumber")
            } catch (e: InterruptedException) {
                Log.i(TAG, "Thread Interrupted")
            }
            count++
        }
        jobFinished(p0,true)
    }

    private fun stopRandomNumberGenerator() {
        isRandomGenOn = false
    }

}