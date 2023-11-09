package com.example.bookshelf.ui.login

import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.bookshelf.R
import com.example.bookshelf.databinding.ActivityLoginBinding
import com.example.bookshelf.ui.createaccount.CreateAccountActivity
import com.example.bookshelf.ui.main.MainActivity
import com.example.bookshelf.util.getConnMgr
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*


@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LoginActivity"
    }


    private lateinit var connMgr: ConnectivityManager
    private lateinit var locale: Locale
    private lateinit var prefListener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var prefManager: SharedPreferences

    //
    private val RC_SIGN_IN: Int = 265
    private lateinit var oneTapClient  : SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loginViewModelFactory: LoginViewModelFactory

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        connMgr = getConnMgr(applicationContext, ::getSystemService)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        loginViewModelFactory = LoginViewModelFactory(auth)
        loginViewModel =
            ViewModelProvider(this, loginViewModelFactory).get(LoginViewModel::class.java)
        connMgr = getConnMgr(applicationContext, ::getSystemService)
        prefManager = PreferenceManager.getDefaultSharedPreferences(this)
        val lang = prefManager.getString("language", "en-us")
        setUpLan(lang)
        prefListener = SharedPreferences.OnSharedPreferenceChangeListener { p0, p1 ->
            val lan = p0?.getString("language", "en-us")
            setUpLan(lan)
        }
        prefManager.registerOnSharedPreferenceChangeListener(prefListener)
        // ...
        // Initialize Firebase Auth
        oneTapClient = Identity.getSignInClient(this)

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso)

//
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.your_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        binding.viewModel = loginViewModel

        Log.d(LoginActivity::getLocalClassName.toString(), "On create")
        binding.btnUserLogin.setOnClickListener { view ->
            if (isConnected()) {
                Log.d("LOGIN", "log-in")
                loginViewModel.loginWithEmailPassword(::loginSuccess, ::loginFail)
            } else {
                val toast = Toast.makeText(
                    this,
                    "connection unavalilable, check your connection",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
        }

        binding.signInBtn.setOnClickListener {
            if (isConnected()) {
                val toast = Toast.makeText(this, "One Tap login ", Toast.LENGTH_SHORT)
                toast.show()
                signIn()
            } else {
                val toast = Toast.makeText(this, "Network Unavailable ", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        loginViewModel.email.observe(this) {
            loginViewModel.validEmail()
        }
        loginViewModel.password.observe(this) {
            loginViewModel.validPassword()
        }
        loginViewModel.invalidPassword.observe(this) {
            if (it) {
                binding.paswordEditTextError.visibility = View.VISIBLE
            } else {
                binding.paswordEditTextError.visibility = View.INVISIBLE
            }
        }

        loginViewModel.invalidEmail.observe(this) {
            if (it) {
                binding.emailEditTextError.visibility = View.VISIBLE
            } else {
                binding.emailEditTextError.visibility = View.INVISIBLE
            }
        }
        loginViewModel.invalidPasswordOrEmail.observe(this) {
            if (it) {
                binding.loginError.visibility = View.VISIBLE
            } else {
                binding.loginError.visibility = View.INVISIBLE
            }
        }
        loginViewModel.showProgressBar.observe(this) {
            if (it) {
                binding.loginSpinner.visibility = View.VISIBLE
            } else {
                binding.loginSpinner.visibility = View.GONE
            }
        }

        binding.btnCreateUser.setOnClickListener {
            val intent = Intent(this,CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//
//                // Google Sign In was successful, authenticate with Firebase
//                val account = task.getResult(ApiException::class.java)!!
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
//                firebaseAuthWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e)
//            }
//        }
        when(requestCode){
            RC_SIGN_IN->{
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    credential.googleIdToken?.let { firebaseAuthWithGoogle(it) }
                }catch (e:Exception){
                    Log.d(TAG, "Google sign in failed", e)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
//      Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onPause() {
        super.onPause()
        prefManager.unregisterOnSharedPreferenceChangeListener(prefListener)
    }

    override fun onResume() {
        super.onResume()
        prefManager.registerOnSharedPreferenceChangeListener(prefListener)
    }

    private fun signIn() {
//        val signInIntent = googleSignInClient.signInIntent
//        startActivityForResult(signInIntent, RC_SIGN_IN)
        Log.d("ONE_TAP","getting one tap")
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this){ result->
                Log.d("ONE_TAP","get result")
                try {
                    Log.d("ONE_TAP","onSuccess")
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender,RC_SIGN_IN,
                        null,0,0,0,null
                    )
                }catch (e:IntentSender.SendIntentException){
                    Log.d("ONE_TAP","couldn't start one tap")
                }
            }
            .addOnFailureListener(this){
                it.localizedMessage?.let { it1 -> Log.d("ONE_TAP", it1) }
            }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val toast = Toast.makeText(applicationContext, "Signing with google", Toast.LENGTH_LONG)
        toast.show()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user
                    val toast =
                        Toast.makeText(applicationContext, "Signing Fail", Toast.LENGTH_LONG)
                    toast.show()
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }

    }

    private fun setUpLan(lan: String?) {
        locale = if (lan.equals("not-set")) Locale.getDefault()
        else
            Locale(lan.toString())
        Locale.setDefault(locale)
        val config = baseContext.resources.configuration
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isConnected(): Boolean {
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    private fun loginSuccess(): Unit {
        val toast = Toast.makeText(this,"Login succeed!  ",Toast.LENGTH_LONG)
        toast.show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun loginFail(): Unit {
        val toast = Toast.makeText(this, "username or password not found", Toast.LENGTH_LONG)
        toast.show()
    }
}

