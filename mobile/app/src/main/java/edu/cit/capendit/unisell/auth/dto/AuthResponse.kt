package edu.cit.capendit.unisell.auth.dto

data class AuthResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    val token: String?
)