package com.example.mafia_demo.remote

import com.google.gson.annotations.SerializedName

data class LobbyResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("number") val number: Int,
    @SerializedName("players") val players: List<Player>,

)
