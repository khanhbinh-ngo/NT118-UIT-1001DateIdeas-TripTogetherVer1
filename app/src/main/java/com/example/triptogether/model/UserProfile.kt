package com.example.triptogether.model

data class UserProfile(
    val uid: String,
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val role: String = "",
    val imageUrl: String? = null,
) {
    val name: String
        get() = listOf(firstName, lastName).filter(String::isNotBlank).joinToString(" ")
}
