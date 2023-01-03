package com.example.remotebindingservicesidedemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.util.*


class MyService : Service() {

    companion object {
        private val TAG = "MyService"
        private const val GET_RANDOM_NUMBER_FLAG = 0

        class RandomNumberRequestHandler : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    GET_RANDOM_NUMBER_FLAG -> {
                        val messageSendRandomNumber = Message.obtain(null, GET_RANDOM_NUMBER_FLAG)
                        messageSendRandomNumber.arg1 = getRandomNumber()
                        try {
                            msg.replyTo.send(messageSendRandomNumber)
                        } catch (e: RemoteException) {
                            Log.i(TAG, "" + e.message)
                        }
                    }
                }
                super.handleMessage(msg)
            }
        }

        fun getRandomNumber(): Int {
            return mRandomNumber
        }

        private var mRandomNumber = 0
    }

    private var isRandomGenOn = false
    private val randomNumberMessenger = lazy {
        Messenger(RandomNumberRequestHandler())
    }

    override fun onBind(p0: Intent?): IBinder? {
        return randomNumberMessenger.value.binder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRandomGenOn = true
        Thread {
            startRandomNumberGenerator()
        }.start()

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(
                    this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }
        val notificationBuilder: Notification.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val chan = NotificationChannel(
                    "Channel ID",
                    "My Foreground Service",
                    NotificationManager.IMPORTANCE_LOW
                )
                val manager =
                    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                manager.createNotificationChannel(chan)
                Notification.Builder(this, "Channel ID")
            } else {
                Notification.Builder(this)
            }

        val noti = notificationBuilder.setContentTitle("Title Content")
            .setContentText("App Running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setTicker("Ticker Text")
            .build()

        startForeground(1, noti)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRandomNumberGenerator()
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

    private fun stopRandomNumberGenerator() {
        isRandomGenOn = false
        Toast.makeText(applicationContext, "Service Stopped", Toast.LENGTH_SHORT).show()
    }

}