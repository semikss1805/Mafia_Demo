package com.example.mafia_demo.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mafia_demo.R
import com.example.mafia_demo.adapters.*
import com.example.mafia_demo.databinding.FragmentGameBinding
import com.example.mafia_demo.remote.MafiaApi
import com.example.mafia_demo.remote.PlayerResponse
import com.example.mafia_demo.remote.response.DefaultResponse
import com.example.mafia_demo.remote.response.GameStatusResponse
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.log

class GameFragment : Fragment(R.layout.fragment_game) {
    private lateinit var adapter: GameUsersAdapter

    private lateinit var currentAdapter: GameUsersForCurrentAdapter

    private val executorService = Executors.newSingleThreadScheduledExecutor()

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

        executorService.scheduleAtFixedRate({
            updateGame()
        }, 0, 1, TimeUnit.SECONDS)

        binding.showInfoButton.setOnClickListener {
            if (binding.playerRoleTextView.visibility == View.VISIBLE) {
                binding.playerRoleTextView.visibility = View.INVISIBLE
                binding.showInfoButton.text = "Info"
            } else {
                binding.playerRoleTextView.visibility = View.VISIBLE
                binding.showInfoButton.text = "Close Info"
            }
        }

        binding.playerRoleTextView.text = "lobby:${arguments?.getString(numberKey)}\nplayerId:${
            arguments?.getInt(
                playerIdKey
            )
        }\nnickname:${arguments?.getString(nicknameKey)}\nrole:${
            arguments?.getString(
                roleKey
            )
        }\nposition:${arguments?.getInt(positionKey)}"
    }

    private fun updateGame() {
        val mafiaApi = MafiaApi.create()

        binding.skipImageButton.setOnClickListener {
            skipTurn(mafiaApi)
        }

        mafiaApi.getGameStatus(arguments?.getString(UserLobbyFragment.numberKey))
            .enqueue(object : Callback<GameStatusResponse> {
                override fun onResponse(
                    call: Call<GameStatusResponse>,
                    response: Response<GameStatusResponse>
                ) {
                    binding.phaseTextView.text = "Phase: " + response.body()?.phase
                    Log.i("GameStatus", response.body().toString())

                    if (response.body()?.winStatus != null) {
                        Toast.makeText(context, response.body()?.winStatus, Toast.LENGTH_SHORT)
                            .show()
                        executorService.shutdown()
                        view?.postDelayed({
                            findNavController().navigate(
                                R.id.action_gameFragment_to_homePageFragment,
                                bundleOf(
                                    HomePageFragment.nicknameKey to arguments?.getString(
                                        nicknameKey
                                    )
                                )
                            )
                        }, 4000)
                    }

                    if (response.body()?.phase == "SPEECH" || response.body()?.phase == "VOTING")
                        binding.currentPlayerTextView.text =
                            "Current Player: " + response.body()!!.currentPlayer
                    else
                        binding.currentPlayerTextView.text = "Current Player: ?"

                    if (response.body()?.currentPlayer == arguments?.getInt(positionKey) && (!response.body()!!.startGameTimerIsWorking || response.body()?.startGameTimerIsWorking == null)) {
                        binding.skipImageButton.visibility = View.VISIBLE

                        if (response.body()?.phase == "SPEECH")
                            if (!response.body()!!.timerForSpeechIsWorking) {
                                mafiaApi.speech(arguments?.getString(UserLobbyFragment.numberKey))
                                    .enqueue(object : Callback<DefaultResponse> {
                                        override fun onResponse(
                                            call: Call<DefaultResponse>,
                                            response: Response<DefaultResponse>
                                        ) {
                                            Log.i("Speech", "OK")
                                        }

                                        override fun onFailure(
                                            call: Call<DefaultResponse>,
                                            t: Throwable
                                        ) {
                                            Log.e("Error", "NetworkError", t)
                                        }
                                    })
                            }

                        if (response.body()?.phase == "VOTING") {
                            currentAdapter = GameUsersForCurrentAdapter(arguments?.getInt(
                                positionKey
                            ), object : GameUserActionListener {
                                override fun onUserDelete(user: PlayerResponse) {
                                    vote(user, mafiaApi)
                                }
                            })

                            currentAdapter.users = response.body()!!.players
                            val layoutManager = LinearLayoutManager(context)
                            binding.recyclerView.layoutManager = layoutManager
                            binding.recyclerView.adapter = currentAdapter
                        }

                        if (response.body()?.phase == "MAFIA") {
                            currentAdapter = GameUsersForCurrentAdapter(arguments?.getInt(
                                positionKey
                            ), object : GameUserActionListener {
                                override fun onUserDelete(user: PlayerResponse) {
                                    mafiaTurn(user, mafiaApi)
                                }
                            })

                            currentAdapter.users = response.body()!!.players
                            val layoutManager = LinearLayoutManager(context)
                            binding.recyclerView.layoutManager = layoutManager
                            binding.recyclerView.adapter = currentAdapter
                        }

                        if (response.body()?.phase == "SHERIFF") {
                            currentAdapter = GameUsersForCurrentAdapter(arguments?.getInt(
                                positionKey
                            ), object : GameUserActionListener {
                                override fun onUserDelete(user: PlayerResponse) {
                                    sheriffTurn(user, mafiaApi)
                                }
                            })

                            currentAdapter.users = response.body()!!.players
                            val layoutManager = LinearLayoutManager(context)
                            binding.recyclerView.layoutManager = layoutManager
                            binding.recyclerView.adapter = currentAdapter
                        }
                    } else {
                        adapter = GameUsersAdapter(arguments!!.getInt(positionKey))
                        adapter.users = response.body()!!.players
                        val layoutManager2 = LinearLayoutManager(context)
                        binding.recyclerView.layoutManager = layoutManager2
                        binding.recyclerView.adapter = adapter
                        binding.skipImageButton.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<GameStatusResponse>, t: Throwable) {
                    Log.e("Error", "NetworkError", t)
                }
            })
    }

    private fun skipTurn(mafiaApi: MafiaApi) {
        mafiaApi.skip(arguments?.getString(numberKey)).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                Log.i("skipped", response.body().toString())
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Log.e("Error", "NetworkError", t)
            }
        })
    }

    private fun sheriffTurn(user: PlayerResponse, mafiaApi: MafiaApi) {
        mafiaApi.sheriffTurn(arguments?.getString(numberKey), user.position)
            .enqueue(object : Callback<GameStatusResponse> {
                override fun onResponse(
                    call: Call<GameStatusResponse>,
                    response: Response<GameStatusResponse>
                ) {
                    Toast.makeText(context, user.name + " is " + user.role, Toast.LENGTH_SHORT)
                        .show()
                    Log.i("Voted", response.body().toString())
                }

                override fun onFailure(call: Call<GameStatusResponse>, t: Throwable) {
                    Log.e("Error", "NetworkError", t)
                }
            })
    }

    private fun mafiaTurn(user: PlayerResponse, mafiaApi: MafiaApi) {
        mafiaApi.mafiaTurn(arguments?.getString(numberKey), user.position)
            .enqueue(object : Callback<GameStatusResponse> {
                override fun onResponse(
                    call: Call<GameStatusResponse>,
                    response: Response<GameStatusResponse>
                ) {
                    Log.i("Voted", response.body().toString())
                }

                override fun onFailure(call: Call<GameStatusResponse>, t: Throwable) {
                    Log.e("Error", "NetworkError", t)
                }
            })
    }

    private fun vote(user: PlayerResponse, mafiaApi: MafiaApi) {
        mafiaApi.vote(arguments?.getString(numberKey), user.position)
            .enqueue(object : Callback<GameStatusResponse> {
                override fun onResponse(
                    call: Call<GameStatusResponse>,
                    response: Response<GameStatusResponse>
                ) {
                    Log.i("Voted", response.body().toString())
                }

                override fun onFailure(call: Call<GameStatusResponse>, t: Throwable) {
                    Log.e("Error", "NetworkError", t)
                }
            })
    }

    companion object {
        const val numberKey = "123456"
        const val playerIdKey = "0"
        const val nicknameKey = "Player"
        const val roleKey = "Role"
        const val positionKey = "1"
    }
}