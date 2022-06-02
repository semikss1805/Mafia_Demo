package com.example.mafia_demo.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mafia_demo.R
import com.example.mafia_demo.databinding.ItemUserForAdminBinding
import com.example.mafia_demo.remote.PlayerResponse

interface UserActionListener {
    fun onUserDelete(user: PlayerResponse)
}

class UsersForAdminAdapter(private val actionListener: UserActionListener) :
    RecyclerView.Adapter<UsersForAdminAdapter.UsersViewHolder>(), View.OnClickListener {


    var users: List<PlayerResponse> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserForAdminBinding.inflate(inflater, parent, false)

        binding.deletePlayerImageButton.setOnClickListener(this)

        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val user = users[position]
        with(holder) {
            binding.deletePlayerImageButton.tag = user
            binding.nameTextView.text = user.name
            binding.userImageView.setImageResource(R.drawable.ic_baseline_account_circle_24)
            if (user.admin) {
                binding.nameTextView.setTextColor(Color.parseColor("#AE2D2D"))
                binding.deletePlayerImageButton.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = users.size

    fun sendUsers(players: List<PlayerResponse>) {
        users = players
    }

    class UsersViewHolder(
        val binding: ItemUserForAdminBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onClick(v: View) {
        val user = v.tag as PlayerResponse
        when (v.id) {
            R.id.deletePlayerImageButton -> {
                actionListener.onUserDelete(user)
            }
            else -> {
                //todo
            }
        }
    }

}