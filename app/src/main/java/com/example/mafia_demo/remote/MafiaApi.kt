package com.example.mafia_demo.remote

import com.example.mafia_demo.remote.request.ConnectRequest
import com.example.mafia_demo.remote.request.LobbyCreateRequest
import com.example.mafia_demo.remote.response.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface MafiaApi {

    @GET("v1/lobby/{lobbyNumber}/players")
    fun getLobbyPlayers(@Path("lobbyNumber") lobbyNumber: String?): Call<List<PlayerResponse>>

    @GET("v1/lobby/{lobbyNumber}/mafia_game")
    fun getGameStatus(@Path("lobbyNumber") lobbyNumber: String?): Call<GameStatusResponse>

    @GET("v1/lobby")
    fun getLobby(@Query("number") number: String?): Call<LobbyResponse>

    @POST("v1/lobby")
    fun createLobby(@Body lobbyRequest: LobbyCreateRequest): Call<LobbyResponse>

    @POST("v1/lobby/{lobbyNumber}/players")
    fun connectPlayerToLobby(
        @Path("lobbyNumber") lobbyNumber: String,
        @Body connectRequest: ConnectRequest
    ): Call<PlayerResponse>

    @POST("v1/lobby/{lobbyNumber}/speech")
    fun speech(@Path("lobbyNumber") lobbyNumber: String?): Call<DefaultResponse>

    @POST("v1/lobby/{lobbyNumber}/candidates")
    fun vote(@Path("lobbyNumber") lobbyNumber: String?, @Query("player_position") player_position: Int?): Call<GameStatusResponse>

    @POST("v1/lobby/{lobbyNumber}/mafia_turn")
    fun mafiaTurn(@Path("lobbyNumber") lobbyNumber: String?, @Query("player_position") player_position: Int?): Call<GameStatusResponse>

    @POST("v1/lobby/{lobbyNumber}/sheriff_turn")
    fun sheriffTurn(@Path("lobbyNumber") lobbyNumber: String?, @Query("player_position") player_position: Int?): Call<GameStatusResponse>

    @POST("v1/lobby/{lobbyNumber}/skip")
    fun skip(@Path("lobbyNumber") lobbyNumber: String?): Call<DefaultResponse>

    @DELETE("v1/lobby/{playerId}")
    fun deletePlayerInLobby(@Path("playerId") playerId: Int?): Call<DeletePlayerResponse>

    @DELETE("v1/lobby")
    fun deleteLobbyById(@Query("id") id: String?): Call<DeleteLobbyResponse>

    @PUT("v1/lobby/{lobbyNumber}/mafia_game")
    fun startGame(@Path("lobbyNumber") lobbyNumber: String?): Call<LobbyResponse>

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