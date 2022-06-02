package com.example.mafia_demo.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mafia_demo.R
import com.example.mafia_demo.adapters.UserActionListener
import com.example.mafia_demo.adapters.UsersForAdminAdapter
import com.example.mafia_demo.databinding.FragmentAdminLobbyBinding
import com.example.mafia_demo.remote.MafiaApi
import com.example.mafia_demo.remote.PlayerResponse
import com.example.mafia_demo.remote.response.DeleteLobbyResponse
import com.example.mafia_demo.remote.response.DeletePlayerResponse
import com.example.mafia_demo.remote.response.LobbyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class AdminLobbyFragment : Fragment(R.layout.fragment_admin_lobby) {
    private lateinit var adapter: UsersForAdminAdapter

    private var _binding: FragmentAdminLobbyBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAdminLobbyBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val executorService = Executors.newSingleThreadScheduledExecutor()
        executorService.scheduleAtFixedRate({
            updateLobby()
        }, 0, 1, TimeUnit.SECONDS)

        binding.textView.text = arguments?.getString(numberKey)

        binding.buttonCloseLobby.setOnClickListener {
            executorService.shutdown()
            closeLobby()
        }

        binding.buttonCopy.setOnClickListener {
            copyToClipboard(binding.textView.text.toString())
        }

        binding.buttonStartGame.setOnClickListener {
            startGame()
            executorService.shutdown()
        }
    }

    private fun deletePlayer(user: PlayerResponse, mafiaApi: MafiaApi) {
        mafiaApi.deletePlayerInLobby(user.id).enqueue(object : Callback<DeletePlayerResponse> {
            override fun onResponse(
                call: Call<DeletePlayerResponse>,
                response: Response<DeletePlayerResponse>
            ) {
                Toast.makeText(
                    context,
                    "user:${user.name} deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onFailure(
                call: Call<DeletePlayerResponse>,
                t: Throwable
            ) {
                Log.e("Error", "NetworkError", t)
            }
        })
    }

    private fun startGame(){
        val mafiaApi = MafiaApi.create()
        mafiaApi.startGame(arguments?.getString(numberKey)).enqueue(object : Callback<LobbyResponse>{
            override fun onResponse(call: Call<LobbyResponse>, response: Response<LobbyResponse>) {
                if (response.body() == null){
                    Toast.makeText(context, "Потрібно більше гравців", Toast.LENGTH_SHORT).show()
                }
                else{
                    Log.i("gameStatus","gameStarted\n" + response.body().toString())
                    findNavController().navigate(R.id.action_adminLobbyFragment_to_gameFragment,
                        bundleOf(
                            GameFragment.nicknameKey to arguments?.getString(nicknameKey),
                            GameFragment.numberKey to arguments?.getString(numberKey),
                            GameFragment.playerIdKey to arguments?.getInt(playerIdKey),
                            GameFragment.roleKey to response.body()!!.players[0].role,
                            GameFragment.positionKey to response.body()!!.players[0].position
                        ))
                }
            }

            override fun onFailure(call: Call<LobbyResponse>, t: Throwable) {
                Log.e("Error", "NetworkError", t)
            }
        })
    }

    private fun updateLobby() {
        val mafiaApi = MafiaApi.create()
        mafiaApi.getLobbyPlayers(arguments?.getString(UserLobbyFragment.numberKey))
            .enqueue(object : Callback<List<PlayerResponse>> {
                override fun onResponse(
                    call: Call<List<PlayerResponse>>,
                    response: Response<List<PlayerResponse>>
                ) {

                    adapter = UsersForAdminAdapter(object : UserActionListener {
                        override fun onUserDelete(user: PlayerResponse) {
                            deletePlayer(user, mafiaApi)
                        }
                    })

                    adapter.users = response.body()!!
                    val layoutManager = LinearLayoutManager(context)
                    binding.recyclerView.layoutManager = layoutManager
                    binding.recyclerView.adapter = adapter
                    Log.i("players", "find")
                }

                override fun onFailure(call: Call<List<PlayerResponse>>, t: Throwable) {
                    Log.e("Error", "NetworkError", t)
                }

            })
    }

    private fun closeLobby() {
        val lobbyId = arguments?.getString(lobbyIdKey)
        val mafiaApi = MafiaApi.create()

        mafiaApi.deleteLobbyById(lobbyId).enqueue(object : Callback<DeleteLobbyResponse> {
            override fun onResponse(
                call: Call<DeleteLobbyResponse>,
                response: Response<DeleteLobbyResponse>
            ) {
                Log.i("PlayerDeleted", response.body().toString())
            }

            override fun onFailure(call: Call<DeleteLobbyResponse>, t: Throwable) {
                Log.e("Error", "NetworkError", t)
            }

        })

        findNavController().navigate(
            R.id.action_adminLobbyFragment_to_homePageFragment,
            bundleOf(HomePageFragment.nicknameKey to arguments?.getString(nicknameKey))
        )

    }

    private fun copyToClipboard(textToCopy: String) {
        val clipboardManager: ClipboardManager =
            activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_LONG).show()
    }


    companion object {
        const val numberKey = "123456"
        const val nicknameKey = "Player"
        const val lobbyIdKey = "0"
        const val playerIdKey = "0"
    }
}