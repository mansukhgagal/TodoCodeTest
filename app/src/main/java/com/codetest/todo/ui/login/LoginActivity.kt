package com.codetest.todo.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.codetest.todo.BuildConfig
import com.codetest.todo.R
import com.codetest.todo.databinding.ActivityLoginBinding
import com.codetest.todo.network.Resource
import com.codetest.todo.ui.main.MainActivity
import com.codetest.todo.utils.SecurePreference
import com.codetest.todo.utils.SnackBarHelper
import com.codetest.todo.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initControls()
    }

    private fun initControls() {
        binding.buttonLogin.setOnClickListener {
            doLogin()
        }
        if(BuildConfig.DEBUG) { //for debug only
            binding.etEmail.setText(getString(R.string.test_email))
            binding.etPassword.setText(getString(R.string.test_password))
        }
        subscribeUI()
    }

    private fun getEmail(): String = binding.etEmail.text.toString()
    private fun getPassword(): String = binding.etPassword.text.toString()

    private fun doLogin() {
        val email = getEmail()
        val password = getPassword()
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        if (UserData.isEmailEmpty(email)) {
            binding.tilEmail.error = getString(R.string.error_empty_email)
            binding.etEmail.requestFocus()
        } else if (!UserData.isValidEmailPattern(email)) {
            binding.tilEmail.error = getString(R.string.error_invalid_email)
            binding.etEmail.requestFocus()
        } else if (UserData.isPasswordEmpty(password)) {
            binding.tilPassword.error = getString(R.string.error_empty_password)
            binding.etPassword.requestFocus()
        } else if (!UserData.isValidPasswordLength(password)) {
            binding.tilPassword.error = getString(R.string.error_invalid_password)
            binding.etPassword.requestFocus()
        } else {
            val userData = UserData(email = email, password = password)
            hideKeyboard()
            viewModel.doLogin(userData)
        }
    }

    private fun subscribeUI() {
        viewModel.liveDataLogin.observe(this, {
            when (it) {
                is Resource.Loading -> {
                    updateUIState(false)
                }
                is Resource.Success -> {
                    val apiResponse = it.data
                    apiResponse?.let { api ->
                        if (api.result != null && api.result is UserData) {
                            val userData = api.result as UserData
                            userData.email = getEmail()
                            SecurePreference.storeUserData(userData)
                            loginSuccess()
                        } else {
                            updateUIState(true)
                            api.error?.let { error ->
                                val errorMessage = error.errorMessage
                                    ?: getString(R.string.error_something_went_wrong)
                                SnackBarHelper.infoSnackBar(binding.root, errorMessage, null, null)
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    updateUIState(true)
                    it.message?.let { error ->
                        SnackBarHelper.infoSnackBar(binding.root, error, null, null)
                    }
                }
            }
        })
    }

    private fun loginSuccess() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    private fun updateUIState(state: Boolean) {
        if (state)
            binding.progressHorizontal.hide()
        else
            binding.progressHorizontal.show()
        binding.etEmail.isEnabled = state
        binding.etPassword.isEnabled = state
        binding.buttonLogin.isEnabled = state
    }
}