package com.example.mafia_demo.remote.response

import com.example.mafia_demo.remote.PlayerResponse
import com.google.gson.annotations.SerializedName

data class GameStatusResponse(
    @SerializedName("day") val day: Int,
    @SerializedName("dayTime") val dayTime: String,
    @SerializedName("phase") val phase: String,
    @SerializedName("currentPlayer") val currentPlayer: Int,
    @SerializedName("players") val players: List<PlayerResponse>
)