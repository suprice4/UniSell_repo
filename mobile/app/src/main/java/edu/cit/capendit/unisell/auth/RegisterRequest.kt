package edu.cit.capendit.unisell.auth

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)