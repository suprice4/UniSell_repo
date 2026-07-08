package edu.cit.capendit.unisell.auth

data class LoginRequest(
    val email: String,
    val password: String
)