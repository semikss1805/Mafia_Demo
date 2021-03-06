package com.example.mafia_demo.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mafia_demo.R
import com.example.mafia_demo.databinding.ItemUserBinding
import com.example.mafia_demo.remote.PlayerResponse

class GameUsersAdapter(val playerPosition: Int): RecyclerView.Adapter<GameUsersAdapter.UsersViewHolder>() {

    var users: List<PlayerResponse> = emptyList()
        set(newValue){
            field = newValue
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater,parent,false)
        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val user = users[position]
        with(holder){
            binding.nameTextView.text = user.name
            binding.userImageView.setImageResource(R.drawable.ic_baseline_account_circle_24)
            if (playerPosition == user.position)
                binding.nameTextView.setTextColor(Color.parseColor("#AE2D2D"))
            if (!user.alive){
                binding.nameTextView.setTextColor(Color.parseColor("#808080"))
            }
        }
    }

    override fun getItemCount(): Int = users.size

    fun sendUsers(players: List<PlayerResponse>){
        users = players
    }

    class UsersViewHolder(
        val binding: ItemUserBinding
    ): RecyclerView.ViewHolder(binding.root)

}