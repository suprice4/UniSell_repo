package edu.cit.capendit.unisell.platform

data class PlatformRequest(
    val name: String
)

data class PlatformResponse(
    val id: Long,
    val name: String
)