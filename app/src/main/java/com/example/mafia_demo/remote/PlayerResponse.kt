package com.example.mafia_demo.remote

import com.google.gson.annotations.SerializedName
import java.util.*

data class Player(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("admin") val admin: Boolean,
    @SerializedName("lobbyId") val lobbyId: Int,
)