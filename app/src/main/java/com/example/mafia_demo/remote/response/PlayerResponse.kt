package com.example.mafia_demo.remote

import com.google.gson.annotations.SerializedName
import java.util.*

data class PlayerResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String,
    @SerializedName("alive") val  alive: Boolean,
    @SerializedName("position") val  position: Int,
    @SerializedName("admin") val admin: Boolean,
    @SerializedName("lobbyId") val lobbyId: Int
)