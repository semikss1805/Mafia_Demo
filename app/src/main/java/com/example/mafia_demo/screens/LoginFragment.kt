package com.example.mafia_demo.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mafia_demo.MainActivity
import com.example.mafia_demo.R
import com.example.mafia_demo.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSetNickname.setOnClickListener {

            if (binding.editTextNickname.text.length > 3){
                val nickname = binding.editTextNickname.text.toString()
                findNavController().navigate(
                    R.id.action_loginFragment_to_homePageFragment,
                    bundleOf(HomePageFragment.nicknameKey to nickname)
                )
            }

            else {
                val text = "ВВЕДІТЬ ІНШИЙ НІКНЕЙМ"
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            }
        }
    }
}