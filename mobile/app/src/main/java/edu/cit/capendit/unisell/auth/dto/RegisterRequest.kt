package edu.cit.capendit.unisell.auth.dto

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)