package com.example.mafia_demo.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mafia_demo.R
import com.example.mafia_demo.databinding.FragmentHomepageBinding
import com.example.mafia_demo.remote.*
import com.example.mafia_demo.remote.request.ConnectRequest
import com.example.mafia_demo.remote.request.LobbyCreateRequest
import com.example.mafia_demo.remote.response.LobbyResponse
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePageFragment : Fragment(R.layout.fragment_homepage) {

    private var _binding: FragmentHomepageBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomepageBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCreate.setOnClickListener {
            createLobby()
        }

        binding.buttonJoin.setOnClickListener {
            joinLobby()
        }

        binding.nicknameTextView.text = arguments?.getString(nicknameKey)

        binding.editNicknameImageButton.setOnClickListener {
            findNavController().navigate(R.id.action_homePageFragment_to_loginFragment)
        }
    }

    private fun createLobby() {
        val name = arguments?.getString(nicknameKey)
        val lobbyName = arguments?.getString(nicknameKey) + "`s lobby"

        val mafiaApi = MafiaApi.create()
        val lobbyCreateRequest = LobbyCreateRequest(lobbyName, name)

        mafiaApi.createLobby(lobbyCreateRequest).enqueue(object : Callback<LobbyResponse> {
            override fun onResponse(
                call: Call<LobbyResponse>,
                response: Response<LobbyResponse>
            ) {
                Log.i("LobbyCreated", response.body().toString())

                if (response.body() != null) {
                    val lobbyNumber = response.body()?.number.toString()
                    val lobbyId = response.body()?.id.toString()

                    viewLifecycleOwner.lifecycleScope.launch {

                        findNavController().navigate(
                            R.id.action_homePageFragment_to_adminLobbyFragment,
                            bundleOf(
                                AdminLobbyFragment.numberKey to lobbyNumber,
                                AdminLobbyFragment.lobbyIdKey to lobbyId,
                                AdminLobbyFragment.nicknameKey to arguments?.getString(
                                    nicknameKey
                                )
                            )
                        )

                    }
                }
            }

            override fun onFailure(call: Call<LobbyResponse>, t: Throwable) {
                Log.e("Error", "NetworkError", t)
            }

        })
    }

    private fun joinLobby() {
        val lobbyNumber = binding.CodeInput.text.toString()

        val mafiaApi = MafiaApi.create()
        val connectRequest = ConnectRequest(arguments?.getString(nicknameKey))

        mafiaApi.connectPlayerToLobby(lobbyNumber, connectRequest)
            .enqueue(object : Callback<PlayerResponse> {

                override fun onResponse(
                    call: Call<PlayerResponse>,
                    response: Response<PlayerResponse>
                ) {
                    Log.i("AddedPlayer", response.body().toString())

                    if (response.body() != null)
                        viewLifecycleOwner.lifecycleScope.launch {
                            val id = response.body()?.id

                            findNavController().navigate(
                                R.id.action_homePageFragment_to_userLobbyFragment,
                                bundleOf(
                                    UserLobbyFragment.numberKey to lobbyNumber,
                                    UserLobbyFragment.playerIdKey to id,
                                    UserLobbyFragment.nicknameKey to arguments?.getString(
                                        nicknameKey
                                    )
                                )
                            )
                        }
                    else
                        viewLifecycleOwner.lifecycleScope.launch {
                            val text = "ПОМИЛКОВИЙ НОМЕР КІМНАТИ"
                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                        }


                }

                override fun onFailure(call: Call<PlayerResponse>, t: Throwable) {
                    Log.e("Error", "NetworkError", t)
                }
            })
    }

    companion object {
        const val nicknameKey = "Player"
    }
}