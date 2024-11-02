package com.devhub.devhubapp.fragment

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.devhub.devhubapp.R
import com.devhub.devhubapp.activity.ForgotPasswordActivity
import com.devhub.devhubapp.activity.RegistrationActivity
import com.devhub.devhubapp.activity.WelcomeActivity
import com.devhub.devhubapp.api.AuthAPI
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.ErrorResponse
import com.devhub.devhubapp.dataClasses.LoginResponse
import com.devhub.devhubapp.dataClasses.User
import com.devhub.devhubapp.dataClasses.LoginRequest
import com.devhub.devhubapp.databinding.FragmentLoginContainerBinding
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class LoginContainerFragment : Fragment() {

    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private lateinit var authAPI: AuthAPI
    private lateinit var binding: FragmentLoginContainerBinding

    val usernameError = ErrorFragment()
    val passwordError = ErrorFragment()
    val loginError = ErrorFragment()

    private var usernameInput: String = ""
    private var passwordInput: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        encryptedPreferencesManager = EncryptedPreferencesManager(requireContext())
        authAPI = RetrofitClient.getInstance(requireContext()).getRetrofit().create(AuthAPI::class.java)
        binding = FragmentLoginContainerBinding.inflate(layoutInflater, container, false)

        setUpFragments()

        return binding.root
    }

    private fun setUpFragments(){

        binding.usernameErrorTextView.visibility = View.GONE
        binding.passwordErrorTextView.visibility = View.GONE
        binding.loginErrorTextView.visibility = View.GONE

        binding.forgotPasswordTextview.setOnClickListener {
            val intent = Intent(requireContext(), ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        val fragmentManager: FragmentManager = childFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val title = TitleFragment()
        title.setShowBackArrow(true)
        title.setTitleText("Log In")
        fragmentTransaction.add(R.id.title_container, title)

        val usernameOrEmailInputFragment = createInputFragment("username", InputType.TYPE_CLASS_TEXT, "Enter your email or username")
        fragmentTransaction.add(R.id.username_and_email_input_container, usernameOrEmailInputFragment)

        fragmentTransaction.add(R.id.usernameErrorTextView, usernameError)

        val passwordInputFragment = createInputFragment("password", InputType.TYPE_TEXT_VARIATION_PASSWORD, "Enter your password")
        fragmentTransaction.add(R.id.password_input_container, passwordInputFragment)

        fragmentTransaction.add(R.id.passwordErrorTextView, passwordError)

        fragmentTransaction.add(R.id.loginErrorTextView, loginError)

        val primaryButtonFragment = PrimaryButtonFragment()
        primaryButtonFragment.setButtonText("Next")
        primaryButtonFragment.setButtonAction { Login()}
        fragmentTransaction.add(R.id.primary_button_container, primaryButtonFragment)

        val lineFragment = LineFragment()
        fragmentTransaction.add(R.id.line_container, lineFragment)

        val textAndLinkFragment = TextWithLinkFragment()
        textAndLinkFragment.setTextAndLinkText(
            getString(R.string.dont_have_an_account),
            getString(R.string.sign_up),
            View.OnClickListener {
                val intent = Intent(requireContext(), RegistrationActivity::class.java)
                startActivity(intent)
            }
        )
        fragmentTransaction.add(R.id.text_and_linkText_container, textAndLinkFragment)

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

    private fun Login() {
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
                            userName = userResponse.userName,
                            avatar = userResponse.avatar,
                            email = userResponse.email,
                            createdAt = userResponse.createdAt.toDate(),
                            devPoints = userResponse.devPoints,
                            activationCode = userResponse.activationCode,
                            isActivated = userResponse.isActivated,
                            roles = userResponse.userRole.map { it.toString() }.toTypedArray()
                        )

                        encryptedPreferencesManager.saveUserData(user)
                        encryptedPreferencesManager.saveTokens(accessToken, refreshToken)

                        val intent = Intent(requireContext(), WelcomeActivity::class.java)
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

        usernameError.setErrorText("")
        passwordError.setErrorText("")
        loginError.setErrorText("")

        binding.usernameErrorTextView.visibility = View.GONE
        binding.passwordErrorTextView.visibility = View.GONE
        binding.loginErrorTextView.visibility = View.GONE

        errorBody?.let {
            try {
                val errorJson = it.string()
                val gson = Gson()
                val errorResponse = gson.fromJson(errorJson, ErrorResponse::class.java)

                errorResponse.errors?.let { errors ->
                    errors.LoginError?.let { loginErrors ->
                        loginError.setErrorText(loginErrors.joinToString("\n"))
                        binding.loginErrorTextView.visibility = View.VISIBLE
                    }

                    errors.Username?.let { usernameErrors ->
                        usernameError.setErrorText(usernameErrors.joinToString("\n"))
                        binding.usernameErrorTextView.visibility = View.VISIBLE
                    }

                    errors.Password?.let { passwordErrors ->
                        passwordError.setErrorText(passwordErrors.joinToString("\n"))
                        binding.passwordErrorTextView.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.e("Error Parsing", "Error parsing error response: ${e.message}")
            }
        }
    }

}