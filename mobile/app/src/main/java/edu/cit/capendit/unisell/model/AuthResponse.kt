package edu.cit.capendit.unisell.model

data class AuthResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: String
)