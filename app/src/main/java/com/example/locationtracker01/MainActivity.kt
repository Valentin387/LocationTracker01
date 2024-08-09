package com.example.locationtracker01

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var btnStart: Button
    lateinit var btnStop: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //we just assume the user accepts to grant all the permissions
        //in the real life, you should fine-grained permission handling here
        ActivityCompat.requestPermissions(

            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            0
        )

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)

        btnStart.setOnClickListener(this)
        btnStop.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.btn_start -> {
                //we send an intent to our service to start the location tracking
                Intent(applicationContext, LocationService::class.java).apply {
                    action = LocationService.ACTION_START
                    startService(this)
                }
            }
            R.id.btn_stop -> {
                //we send an intent to our service to stop the location tracking
                Intent(applicationContext, LocationService::class.java).apply {
                    action = LocationService.ACTION_STOP
                    //send this command to the service
                    startService(this)

                }
            }
        }
    }
}