package com.sQUAD.maome.ui.fragment.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sQUAD.maome.adapter.NotesRCAdapter
import com.sQUAD.maome.databinding.MainNotesFragmentBinding
import com.sQUAD.maome.retrofit.MainApi
import com.sQUAD.maome.retrofit.RetrofitCfg
import com.sQUAD.maome.viewModels.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainNotesFragment : Fragment() {

    private lateinit var binding: MainNotesFragmentBinding
    private lateinit var mainApi: MainApi
    private var retrofitCfg = RetrofitCfg()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainNotesFragmentBinding.inflate(inflater, container, false)

        val sharedPreferences = activity?.getSharedPreferences("User_token", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", null)

        retrofitCfg.setToken(token)
        mainApi = retrofitCfg.getMainApiWithToken()

        // initialize notesList with your data
        val notesAdapter = NotesRCAdapter()
        binding.notesRcView.adapter = notesAdapter
        binding.notesRcView.layoutManager = LinearLayoutManager(requireContext())
        CoroutineScope(Dispatchers.IO).launch {
            val response = token?.let { mainApi.getAllNotes(it) }

            requireActivity().runOnUiThread {
                binding.apply {
                    if (response != null) {
                        if (response.isSuccessful) {
                            val note = response.body()
                            if (note != null) {
                                if (note.isNotEmpty()) {
                                    notesAdapter.submitList(note)
                                    binding.NoNotesTextView.visibility = View.GONE
                                } else {
                                    binding.NoNotesTextView.visibility = View.VISIBLE
                                }
                            }
                        } else {
                            binding.NoNotesTextView.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        return binding.root
    }
}