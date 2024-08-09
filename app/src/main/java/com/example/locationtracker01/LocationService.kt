package com.example.locationtracker01

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService  : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    //it is bound to the lifetime of our service
    /*
    if one job in this scope fails, the others will keep running
     */
    private lateinit var locationClient: LocationClient
    //the abstraction we just created

    override fun onBind(intent: Intent?): IBinder? {
        return null
        //we don't need to bind the service to an activity
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            this,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        //this is called for every single intent that is sent to the service
        //we can use the actions of the service

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient.getLocationUpdates(10000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                //we get the location and update the notification
                val lat = location.latitude.toString()
                val lon = location.longitude.toString()
                val updatedNotification = notification.setContentText(
                    "Location: ($lat, $lon)"
                )
                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope) //this binds the callback flow to the lifetime of out service

        startForeground(1, notification.build())
        //the id must be greater than 0
        //be sure to use the same ID for the startForeground and for the getLocationUpdates
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        //we cancel the scope when the service is destroyed
        //so we don't have any memory leaks
    }


    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
    /*
    A companion object in Kotlin is a singleton object
    associated with a class, allowing you to define methods
    and properties that belong to the class rather
    than to instances of the class. It serves a similar
    purpose to static members in Java, but with more
    flexibility and power.
     */
}

/*
we also need to create an application class
because this notification service needs a notification channel
in which we send these notifications
 */