package com.devhub.devhubapp.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.devhub.devhubapp.R
import com.devhub.devhubapp.api.AuthAPI
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.ErrorHandler
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.LoginRequest
import com.devhub.devhubapp.dataClasses.LoginResponse
import com.devhub.devhubapp.dataClasses.User
import com.devhub.devhubapp.databinding.ActivityLogInBinding
import com.devhub.devhubapp.fragment.ErrorFragment
import com.devhub.devhubapp.fragment.InputFragment
import com.devhub.devhubapp.fragment.InputTextListener
import com.devhub.devhubapp.fragment.LineFragment
import com.devhub.devhubapp.fragment.PrimaryButtonFragment
import com.devhub.devhubapp.fragment.TextWithLinkFragment
import com.devhub.devhubapp.fragment.TitleFragment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class LogInActivity : AppCompatActivity() {

    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private lateinit var authAPI: AuthAPI
    private lateinit var binding: ActivityLogInBinding

    val usernameError = ErrorFragment()
    val passwordError = ErrorFragment()
    val loginError = ErrorFragment()

    private var usernameInput: String = ""
    private var passwordInput: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        authAPI = RetrofitClient.getInstance(this).getRetrofit().create(AuthAPI::class.java)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpFragments()

    }

    private fun setUpFragments(){

        binding.usernameErrorTextView.visibility = View.GONE
        binding.passwordErrorTextView.visibility = View.GONE
        binding.loginErrorTextView.visibility = View.GONE

        binding.forgotPasswordTextview.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val title = TitleFragment()
        title.setShowBackArrow(true)
        title.setTitleText("Log In")
        fragmentTransaction.add(R.id.titleContainer, title)

        val usernameOrEmailInputFragment = createInputFragment("username", InputType.TYPE_CLASS_TEXT, "Enter your username")
        fragmentTransaction.add(R.id.usernameInputContainer, usernameOrEmailInputFragment)

        fragmentTransaction.add(R.id.usernameErrorTextView, usernameError)

        val passwordInputFragment = createInputFragment("password", InputType.TYPE_TEXT_VARIATION_PASSWORD, "Enter your password")
        fragmentTransaction.add(R.id.passwordInputContainer, passwordInputFragment)

        fragmentTransaction.add(R.id.passwordErrorTextView, passwordError)

        fragmentTransaction.add(R.id.loginErrorTextView, loginError)

        val primaryButtonFragment = PrimaryButtonFragment()
        primaryButtonFragment.setButtonText("Next")
        primaryButtonFragment.setButtonAction { login()}
        fragmentTransaction.add(R.id.primaryButtonContainer, primaryButtonFragment)

        val lineFragment = LineFragment()
        fragmentTransaction.add(R.id.lineContainer, lineFragment)

        val textAndLinkFragment = TextWithLinkFragment()
        textAndLinkFragment.setTextAndLinkText(
            getString(R.string.dont_have_an_account),
            getString(R.string.sign_up),
            View.OnClickListener {
                val intent = Intent(this@LogInActivity, RegistrationActivity::class.java)
                startActivity(intent)
            }
        )
        fragmentTransaction.add(R.id.textAndLinkTextContainer, textAndLinkFragment)

        fragmentTransaction.commit()

    }

    private fun createInputFragment(field: String, inputType: Int, hint: String): InputFragment {
        val inputFragment = InputFragment()
        inputFragment.setInputType(inputType)
        inputFragment.setInputHint(hint)

        inputFragment.setTextWatcher(object : InputTextListener {
            override fun onTextInputChanged(text: String) {
                updateInput(field, text)
            }
        })

        return inputFragment
    }

    fun updateInput(field: String, text: String) {
        when (field) {
            "username" -> usernameInput = text
            "password" -> passwordInput = text
        }
    }

    private fun login() {
        val user = LoginRequest(
            username = usernameInput,
            password = passwordInput,
        )
        authAPI.login(user).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    if (loginResponse != null) {
                        val userResponse = loginResponse.user
                        val accessToken = loginResponse.accessToken
                        val refreshToken = loginResponse.refreshToken

                        val user = User(
                            id = userResponse.id,
                            name = userResponse.name ?: "",
                            username = userResponse.username,
                            avatar = userResponse.avatar,
                            email = userResponse.email,
                            createdAt = userResponse.createdAt.toDate(),
                            devPoints = userResponse.devPoints,
                            activationCode = userResponse.activationCode?: "",
                            isActivated = userResponse.isActivated,
                            roles = userResponse.userRole.map { it.toString() }.toTypedArray()
                        )

                        encryptedPreferencesManager.saveUserData(user)
                        encryptedPreferencesManager.saveTokens(accessToken, refreshToken)

                        val intent = Intent(this@LogInActivity, WelcomeActivity::class.java)
                        startActivity(intent)

                        Log.i("Login", "Login Successful")
                        Log.d("Response", "Response received: ${response.code()}")
                        Log.d("Tokens", " ${encryptedPreferencesManager.getAccessToken()}")
                        Log.d("Tokens", " ${encryptedPreferencesManager.getUserData()}")

                    }
                } else {
                    handleErrors(response.errorBody())
                    Log.e("Login", "Login Failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("Login", "Failed: ${t.message}")
            }
        })
    }

    private fun String.toDate(): Date {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.parse(this) ?: Date()
    }

    private fun handleErrors(errorBody: ResponseBody?) {
        val errorFragments = mapOf(
            "username" to usernameError,
            "password" to passwordError,
            "login" to loginError,
        )

        val errorViews = mapOf(
            "username" to binding.usernameErrorTextView,
            "password" to binding.passwordErrorTextView,
            "login" to binding.loginErrorTextView
        )

        ErrorHandler.handleErrors(errorBody, errorFragments, errorViews)
    }

}