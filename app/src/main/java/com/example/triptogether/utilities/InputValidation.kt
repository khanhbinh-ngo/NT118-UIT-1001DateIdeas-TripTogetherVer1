package com.example.triptogether.utilities

object InputValidation {
    private val timePattern = Regex("^([01]?\\d|2[0-3]):[0-5]\\d$")

    fun validTime(value: String): Boolean = timePattern.matches(value)
}
