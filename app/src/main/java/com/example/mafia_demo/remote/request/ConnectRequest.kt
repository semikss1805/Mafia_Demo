package com.example.mafia_demo.remote.request

import com.google.gson.annotations.SerializedName

data class ConnectRequest(
    @SerializedName("playerName") val playerName: String?
)