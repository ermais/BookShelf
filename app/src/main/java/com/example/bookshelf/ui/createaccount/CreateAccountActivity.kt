package com.example.bookshelf.ui.createaccount

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.databinding.ActivityCreateAccountBinding
import com.example.bookshelf.ui.login.LoginActivity
import com.example.bookshelf.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAccountBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var createAccountViewModelFactory: CreateAccountViewModelFactory
    private lateinit var createAccountViewModel: CreateAccountViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        createAccountViewModelFactory = CreateAccountViewModelFactory(auth)
        createAccountViewModel = ViewModelProvider(
            this,
            createAccountViewModelFactory
        )[CreateAccountViewModel::class.java]
        binding.viewModel = createAccountViewModel


        createAccountViewModel.apply {
            displayName.observe(this@CreateAccountActivity) {
                validDisplayName()
            }
            email.observe(this@CreateAccountActivity) {
                validEmail()
            }
            password.observe(this@CreateAccountActivity) {
                validPassword()
            }
            confirmPassword.observe(this@CreateAccountActivity) {
                validConfirmPassword()
            }
            showDisplayError.observe(this@CreateAccountActivity) {
                if (it) {
                    binding.tvDisplayNameError.visibility = View.VISIBLE
                } else {
                    binding.tvDisplayNameError.visibility = View.GONE
                }
            }
            showEmailError.observe(this@CreateAccountActivity) {
                if (it) {
                    binding.tvEmailError.visibility = View.VISIBLE
                } else {
                    binding.tvEmailError.visibility = View.GONE
                }
            }
            showPasswordError.observe(this@CreateAccountActivity) {
                if (it) {
                    binding.tvPasswordError.visibility = View.VISIBLE
                } else {
                    binding.tvPasswordError.visibility = View.GONE
                }
            }

            showConfirmPasswordError.observe(this@CreateAccountActivity) {
                if (it) {
                    binding.tvConfirmPasswordError.visibility = View.VISIBLE
                } else {
                    binding.tvConfirmPasswordError.visibility = View.GONE
                }
            }

        }

        binding.apply {
            btnHaveAccount.setOnClickListener {
                val loginIntent = Intent(this@CreateAccountActivity, LoginActivity::class.java)
                startActivity(loginIntent)
            }

            btnCreateAccount.setOnClickListener {
                createAccountViewModel.createUserEmailAndPassword(::onCreateSuccess, ::onCreateFail,true)
            }
        }
    }

    private fun onCreateSuccess() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onCreateFail() {
        createAccountViewModel.showCreateAccountError.postValue(true)
        val toast = Toast.makeText(this, "login failed!,check your connection", Toast.LENGTH_LONG)
        toast.show()
    }
}