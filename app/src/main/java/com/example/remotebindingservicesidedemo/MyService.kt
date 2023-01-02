package com.example.remotebindingservicesidedemo

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log

class MyService:Service() {

    companion object{
        private val TAG = "MyService"
        private const val GET_RANDOM_NUMBER_FLAG = 0
        class RandomNumberRequestHandler : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    GET_RANDOM_NUMBER_FLAG -> {
                        val messageSendRandomNumber: Message =
                            Message.obtain(null, GET_RANDOM_NUMBER_FLAG)
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
            return 112
        }
    }

    private val randomNumberMessenger = lazy {
        Messenger(RandomNumberRequestHandler())
    }

    override fun onBind(p0: Intent?): IBinder? {
        return randomNumberMessenger.value.binder
    }
}