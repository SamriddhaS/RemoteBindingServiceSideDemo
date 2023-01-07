package com.example.remotebindingservicesidedemo

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*

class MyWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    private val TAG = "MyWorker"
    private var mRandomNumber = 0

    private fun startRandomNumberGenerator() {
        var i = 0
        while (i < 10 && !isStopped) {
            try {
                Thread.sleep(1000)
                mRandomNumber = Random().nextInt(1000) + 11
                Log.i(TAG, "Random Number: $mRandomNumber")
                i++
            } catch (e: InterruptedException) {
                Log.i(TAG, "Thread Interrupted")
            }
        }
    }

    override fun doWork(): Result {
        startRandomNumberGenerator()
        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        Log.i(TAG, "Worker has been cancelled");
    }

}