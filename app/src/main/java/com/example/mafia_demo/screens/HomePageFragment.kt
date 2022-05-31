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
import com.example.mafia_demo.MainActivity
import com.example.mafia_demo.R
import com.example.mafia_demo.databinding.FragmentHomepageBinding
import com.example.mafia_demo.remote.*
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
            val name = arguments?.getString(MainActivity.nicknameKey)
            val lobbyName = arguments?.getString(MainActivity.nicknameKey) + "`s lobby"

            val mafiaApi = MafiaApi.create()
            val lobbyCreateRequest = LobbyCreateRequest(lobbyName, name)

            mafiaApi.createLobby(lobbyCreateRequest).enqueue(object : Callback<LobbyResponse> {
                override fun onResponse(
                    call: Call<LobbyResponse>,
                    response: Response<LobbyResponse>
                ) {
                    Log.i("LobbyCreated", response.body().toString())

                    if (response.body() != null) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            findNavController().navigate(R.id.action_homePageFragment_to_adminLobbyFragment)
                        }
                    }
                }

                override fun onFailure(call: Call<LobbyResponse>, t: Throwable) {
                    Log.e("Error", "NetworkError", t)
                }

            })
        }

        binding.buttonJoin.setOnClickListener {
            val lobbyNumber = binding.CodeInput.text.toString()

            val mafiaApi = MafiaApi.create()
            val connectRequest = ConnectRequest(arguments?.getString(MainActivity.nicknameKey))

            mafiaApi.connectPlayerToLobby(lobbyNumber, connectRequest)
                .enqueue(object : Callback<Player> {

                    override fun onResponse(call: Call<Player>, response: Response<Player>) {
                        Log.i("AddedPlayer", response.body().toString())

                        if (response.body() != null)
                            viewLifecycleOwner.lifecycleScope.launch {
                                val id = response.body()?.id

                                findNavController().navigate(
                                    R.id.action_homePageFragment_to_userLobbyFragment,
                                    bundleOf(
                                        UserLobbyFragment.numberKey to lobbyNumber,
                                        UserLobbyFragment.playerIdKey to id
                                    )
                                )
                            }
                        else
                            viewLifecycleOwner.lifecycleScope.launch {
                                val text = "ПОМИЛКОВИЙ НОМЕР КІМНАТИ"
                                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                            }


                    }

                    override fun onFailure(call: Call<Player>, t: Throwable) {
                        Log.e("Error", "NetworkError", t)
                    }

                })

        }

    }
}