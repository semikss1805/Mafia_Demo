package com.example.mafia_demo.remote

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface MafiaApi {

    @POST("v1/lobby")
    fun createLobby(@Body lobbyRequest: LobbyCreateRequest): Call<LobbyResponse>

    @POST("v1/lobby/{lobbyNumber}/players")
    fun connectPlayerToLobby(
        @Path("lobbyNumber") lobbyNumber: String,
        @Body connectRequest: ConnectRequest
    ): Call<Player>

    @DELETE("v1/lobby/{playerId}")
    fun deletePlayerInLobby(@Path("playerId") playerId: Int?): Call<DeleteResponse>

    companion object {

        var BASE_URL = "https://mafia-game-official.herokuapp.com/"

        fun create(): MafiaApi {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(MafiaApi::class.java)

        }
    }
}