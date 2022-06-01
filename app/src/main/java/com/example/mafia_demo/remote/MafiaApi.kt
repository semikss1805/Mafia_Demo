package com.example.mafia_demo.remote

import com.example.mafia_demo.remote.request.ConnectRequest
import com.example.mafia_demo.remote.request.LobbyCreateRequest
import com.example.mafia_demo.remote.response.DeleteLobbyResponse
import com.example.mafia_demo.remote.response.DeletePlayerResponse
import com.example.mafia_demo.remote.response.LobbyResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface MafiaApi {

    @GET("v1/lobby/{lobbyNumber}/players")
    fun getLobbyPlayers(@Path("lobbyNumber") lobbyNumber: String?): Call<List<PlayerResponse>>

    @POST("v1/lobby")
    fun createLobby(@Body lobbyRequest: LobbyCreateRequest): Call<LobbyResponse>

    @POST("v1/lobby/{lobbyNumber}/players")
    fun connectPlayerToLobby(
        @Path("lobbyNumber") lobbyNumber: String,
        @Body connectRequest: ConnectRequest
    ): Call<PlayerResponse>

    @DELETE("v1/lobby/{playerId}")
    fun deletePlayerInLobby(@Path("playerId") playerId: Int?): Call<DeletePlayerResponse>

    @DELETE("v1/lobby")
    fun deleteLobbyById(@Query("id") id: String?): Call<DeleteLobbyResponse>

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