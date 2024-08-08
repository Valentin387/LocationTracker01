package com.example.locationtracker01

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    fun getLocalUpdates(interval: Long): Flow<Location>

    class LocationException(message: String): Exception()

}
