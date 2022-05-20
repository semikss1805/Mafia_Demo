package com.example.mafia_demo.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mafia_demo.R
import com.example.mafia_demo.databinding.FragmentUserLobbyBinding

class UserLobbyFragment: Fragment(R.layout.fragment_user_lobby) {
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
            findNavController().navigate(R.id.action_userLobbyFragment_to_homePageFragment)
        }
    }
}