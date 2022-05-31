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
import com.example.mafia_demo.R
import com.example.mafia_demo.databinding.FragmentAdminLobbyBinding
import com.example.mafia_demo.remote.MafiaApi
import com.example.mafia_demo.remote.response.DeleteLobbyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminLobbyFragment : Fragment(R.layout.fragment_admin_lobby) {
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

        binding.textView.text = arguments?.getString(numberKey)

        binding.buttonCloseLobby.setOnClickListener {
            closeLobby()
        }

        binding.buttonCopy.setOnClickListener {
            copyToClipboard(binding.textView.text.toString())
        }
    }

    private fun closeLobby(){
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

    private fun copyToClipboard(textToCopy: String){
        val clipboardManager: ClipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_LONG).show()
    }

    companion object {
        const val numberKey = "123456"
        const val nicknameKey = "Player"
        const val lobbyIdKey = "0"
    }
}