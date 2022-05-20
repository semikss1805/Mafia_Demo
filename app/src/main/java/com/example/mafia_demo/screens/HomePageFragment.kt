package com.example.mafia_demo.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mafia_demo.R
import com.example.mafia_demo.databinding.FragmentHomepageBinding

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

        binding.buttonJoin.setOnClickListener {
            findNavController().navigate(R.id.action_homePageFragment_to_userLobbyFragment)
        }

        binding.buttonCreate.setOnClickListener {
            findNavController().navigate(R.id.action_homePageFragment_to_adminLobbyFragment)
        }
    }
}