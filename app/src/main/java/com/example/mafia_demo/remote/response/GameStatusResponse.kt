package com.example.mafia_demo.remote.response

import com.example.mafia_demo.remote.PlayerResponse
import com.google.gson.annotations.SerializedName

data class GameStatusResponse(
    @SerializedName("day") val day: Int,
    @SerializedName("dayTime") val dayTime: String,
    @SerializedName("phase") val phase: String,
    @SerializedName("currentPlayer") val currentPlayer: Int,
    @SerializedName("players") val players: List<PlayerResponse>,
    @SerializedName("winStatus") val winStatus: String,
    @SerializedName("startGameTimerIsWorking") val startGameTimerIsWorking:Boolean,
    @SerializedName("playerIsMafia") val playerIsMafia: Boolean,
    @SerializedName("timerForSpeechIsWorking") val timerForSpeechIsWorking:Boolean,
    @SerializedName("timerForDayVotingIsWorking") val timerForDayVotingIsWorking:Boolean,
    @SerializedName("timerForNightMafiaVotingIsWorking") val timerForNightMafiaVotingIsWorking:Boolean,
    @SerializedName("timerForSheriffCheckingIsWorking") val timerForSheriffCheckingIsWorking:Boolean
)