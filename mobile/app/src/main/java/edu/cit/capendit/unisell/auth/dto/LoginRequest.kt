package edu.cit.capendit.unisell.auth.dto

data class LoginRequest(
    val email: String,
    val password: String
)