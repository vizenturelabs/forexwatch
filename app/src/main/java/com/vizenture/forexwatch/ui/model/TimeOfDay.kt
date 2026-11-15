package com.vizenture.forexwatch.ui.model

import java.time.LocalTime

enum class TimeOfDay {
    Morning,
    Afternoon,
    Evening;

    companion object {
        fun current(): TimeOfDay {
            val hour = LocalTime.now().hour
            return when (hour) {
                in 5..11 -> Morning
                in 12..16 -> Afternoon
                else -> Evening
            }
        }
    }
}
