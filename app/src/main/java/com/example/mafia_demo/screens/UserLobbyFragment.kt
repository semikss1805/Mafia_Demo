package com.example.mafia_demo.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mafia_demo.R
import com.example.mafia_demo.databinding.FragmentUserLobbyBinding
import com.example.mafia_demo.remote.DeleteResponse
import com.example.mafia_demo.remote.MafiaApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserLobbyFragment : Fragment(R.layout.fragment_user_lobby) {
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

        binding.buttonLeave.setOnClickListener {

            val mafiaApi = MafiaApi.create()
            val playerId = arguments?.getInt(playerIdKey)

            mafiaApi.deletePlayerInLobby(playerId).enqueue(object : Callback<DeleteResponse> {
                override fun onResponse(
                    call: Call<DeleteResponse>,
                    response: Response<DeleteResponse>
                ) {
                    Log.i("PlayerDeleted", response.body().toString())
                }

                override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                    Log.e("Error", "NetworkError", t)
                }

            })

            findNavController().navigate(R.id.action_userLobbyFragment_to_loginFragment)
        }

        binding.lobbyCode.text = arguments?.getString(numberKey)
    }

    companion object {
        const val numberKey = "123456"
        const val playerIdKey = "0"
    }
}