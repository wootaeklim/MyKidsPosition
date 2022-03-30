package com.wt.kids.mykidsposition.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.wt.kids.mykidsposition.BuildConfig
import com.wt.kids.mykidsposition.utils.LocationUtils
import com.wt.kids.mykidsposition.utils.Logger
import com.wt.kids.mykidsposition.utils.SmsUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class JeffService : Service(), LocationListener {
    private val logTag = this::class.java.simpleName
    private val channelId = BuildConfig.APPLICATION_ID
    private val channelName = "Jeff"
    private var sendingSms = true

    @Inject lateinit var logger: Logger
    @Inject lateinit var locationUtils: LocationUtils
    @Inject lateinit var smsUtils: SmsUtils

    override fun onCreate() {
        super.onCreate()
        logger.logD(logTag, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        logger.logD(logTag, "onStartCommand")
        startNotificationService()
        locationUtils.registerLocationUpdates(this)
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        logger.logD(logTag, "onBind")
        return null
    }

    private fun startNotificationService() {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("")
            .setContentText("").build()
        startForeground(1, notification)
    }

    override fun onLocationChanged(p: Location) {
        if (sendingSms) {
            sendingSms = false
            smsUtils.sendSms()
            logger.logD(logTag, "onLocationChanged : ${p.latitude}, ${p.longitude}")
        }
    }
}