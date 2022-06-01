package com.example.mafia_demo.screens

import android.content.ClipboardManager
import android.content.Context
import android.content.ClipData
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mafia_demo.R
import com.example.mafia_demo.UsersAdapter
import com.example.mafia_demo.databinding.FragmentUserLobbyBinding
import com.example.mafia_demo.remote.response.DeletePlayerResponse
import com.example.mafia_demo.remote.MafiaApi
import com.example.mafia_demo.remote.PlayerResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class UserLobbyFragment : Fragment(R.layout.fragment_user_lobby) {
    private lateinit var adapter: UsersAdapter

    private val executorService = Executors.newSingleThreadScheduledExecutor()

    private var _binding: FragmentUserLobbyBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUserLobbyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        executorService.scheduleAtFixedRate({
            updateLobby()
        }, 0, 1, TimeUnit.SECONDS)

        binding.buttonLeave.setOnClickListener {
            executorService.shutdown()
            leaveLobby()
        }

        binding.lobbyCode.text = arguments?.getString(numberKey)

        binding.buttonCopy.setOnClickListener {
            copyToClipboard(binding.lobbyCode.text.toString())
        }
    }

    private fun leaveLobby() {
        val mafiaApi = MafiaApi.create()
        val playerId = arguments?.getInt(playerIdKey)

        mafiaApi.deletePlayerInLobby(playerId).enqueue(object : Callback<DeletePlayerResponse> {
            override fun onResponse(
                call: Call<DeletePlayerResponse>,
                response: Response<DeletePlayerResponse>
            ) {
                Log.i("PlayerDeleted", response.body().toString())
            }

            override fun onFailure(call: Call<DeletePlayerResponse>, t: Throwable) {
                Log.e("Error", "NetworkError", t)
            }

        })

        findNavController().navigate(
            R.id.action_userLobbyFragment_to_homePageFragment,
            bundleOf(HomePageFragment.nicknameKey to arguments?.getString(nicknameKey))
        )
    }

    private fun updateLobby() {
        val mafiaApi = MafiaApi.create()
        var prevResponse: List<PlayerResponse> = listOf(PlayerResponse(1, "1", false, 1))
        mafiaApi.getLobbyPlayers(arguments?.getString(numberKey))
            .enqueue(object : Callback<List<PlayerResponse>> {
                override fun onResponse(
                    call: Call<List<PlayerResponse>>,
                    response: Response<List<PlayerResponse>>
                ) {
                    if (response.body() != null) {
                        if (!isInLobby(response.body()!!)) {
                            executorService.shutdown()
                            leaveLobby()
                            Toast.makeText(context, "Ви були виключені з лоббі", Toast.LENGTH_SHORT).show()
                        }

                        adapter = UsersAdapter()
                        adapter.users = response.body()!!
                        val layoutManager = LinearLayoutManager(context)
                        binding.recyclerView.layoutManager = layoutManager
                        binding.recyclerView.adapter = adapter
                    }
                    else{
                        findNavController().navigate(
                            R.id.action_userLobbyFragment_to_homePageFragment,
                            bundleOf(HomePageFragment.nicknameKey to arguments?.getString(nicknameKey))
                        )
                        Toast.makeText(context, "Лоббі розпущено", Toast.LENGTH_SHORT).show()
                        executorService.shutdown()
                    }
                }

                override fun onFailure(call: Call<List<PlayerResponse>>, t: Throwable) {
                    Log.e("Error", "NetworkError", t)
                }

            })
    }

    private fun isInLobby(users: List<PlayerResponse>): Boolean {
        var isInLobby = false
        val id = arguments?.getInt(playerIdKey)
        for (user in users) {
            if (user.id == id)
                isInLobby = true
        }
        return isInLobby
    }

    private fun copyToClipboard(textToCopy: String) {
        val clipboardManager: ClipboardManager =
            activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_LONG).show()
    }

    companion object {
        const val numberKey = "123456"
        const val playerIdKey = "0"
        const val nicknameKey = "Player"
    }
}