package com.example.mafia_demo.remote

import com.google.gson.annotations.SerializedName

data class LobbyCreateRequest(
    @SerializedName("lobbyName") val lobbyName: String?,
    @SerializedName("adminName") val adminName: String?
)