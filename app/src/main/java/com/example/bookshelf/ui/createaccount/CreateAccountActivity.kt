package com.example.bookshelf.ui.createaccount

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.databinding.ActivityCreateAccountBinding
import com.google.firebase.auth.FirebaseAuth

class CreateAccountActivity : AppCompatActivity() {
    private  var _binding : ActivityCreateAccountBinding? = null
    val binding get() = _binding!!
    private lateinit var auth:FirebaseAuth
    private lateinit var createAccountViewModelFactory: CreateAccountViewModelFactory
    private lateinit var createAccountViewModel: CreateAccountViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        createAccountViewModelFactory = CreateAccountViewModelFactory(auth)
        createAccountViewModel = ViewModelProvider(this).get(CreateAccountViewModel::class.java)
        binding.viewModel = createAccountViewModel

    }
}