package com.example.mafia_demo.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mafia_demo.R
import com.example.mafia_demo.databinding.FragmentGameBinding

class GameFragment: Fragment(R.layout.fragment_game) {
    private var _binding: FragmentGameBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playerRoleTextView.text = "lobby:${arguments?.getString(numberKey)}\nplayerId:${arguments?.getInt(
            playerIdKey)}\nnickname:${arguments?.getString(nicknameKey)}\nrole:${arguments?.getString(
            roleKey)}\nposition:${arguments?.getInt(positionKey)}"
    }

    companion object{
        const val numberKey = "123456"
        const val playerIdKey = "0"
        const val nicknameKey = "Player"
        const val roleKey = "Role"
        const val positionKey = "1"
    }
}