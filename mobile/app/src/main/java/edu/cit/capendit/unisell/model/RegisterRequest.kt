package edu.cit.capendit.unisell.model

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)