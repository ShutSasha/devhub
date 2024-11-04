package com.devhub.devhubapp.activity

import android.annotation.SuppressLint
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
import com.devhub.devhubapp.dataClasses.RegistrationRequest
import com.devhub.devhubapp.dataClasses.RegistrationResponse
import com.devhub.devhubapp.databinding.ActivityRegistrationBinding
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

class RegistrationActivity : AppCompatActivity() {

    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private lateinit var authAPI: AuthAPI
    private lateinit var binding: ActivityRegistrationBinding

    val usernameError = ErrorFragment()
    val emailError = ErrorFragment()
    val passwordError = ErrorFragment()
    val repeatPasswordError = ErrorFragment()
    val registrationError = ErrorFragment()

    private var usernameInput: String = ""
    private var emailInput: String = ""
    private var passwordInput: String = ""
    private var repeatPasswordInput: String = ""

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        authAPI = RetrofitClient.getInstance(this).getRetrofit().create(AuthAPI::class.java)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registration_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpFragments()

    }

    private fun setUpFragments(){
        binding.emailErrorTextView.visibility = View.GONE
        binding.usernameErrorTextView.visibility = View.GONE
        binding.passwordErrorTextView.visibility = View.GONE
        binding.repeatPasswordErrorTextView.visibility = View.GONE
        binding.registrationErrorTextView.visibility = View.GONE

        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()

        val title = TitleFragment()
        title.setShowBackArrow(true)
        title.setTitleText("Registration")
        fragmentTransaction.add(R.id.title, title)

        val usernameInputFragment = createInputFragment("username", InputType.TYPE_CLASS_TEXT, "Enter your username")
        fragmentTransaction.add(R.id.usernameInputContainer, usernameInputFragment)

        fragmentTransaction.add(R.id.usernameErrorTextView, usernameError)

        val emailInputFragment = createInputFragment("email", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, "Enter your email")
        fragmentTransaction.add(R.id.emailInputContainer, emailInputFragment)

        fragmentTransaction.add(R.id.emailErrorTextView, emailError)

        val passwordInputFragment = createInputFragment("password", InputType.TYPE_TEXT_VARIATION_PASSWORD, "Enter your password")
        fragmentTransaction.add(R.id.passwordInputContainer, passwordInputFragment)

        fragmentTransaction.add(R.id.passwordErrorTextView, passwordError)

        val repeatPasswordInputFragment = createInputFragment("repeat_password", InputType.TYPE_TEXT_VARIATION_PASSWORD, "Repeat your password")
        fragmentTransaction.add(R.id.repeatPasswordInputContainer, repeatPasswordInputFragment)

        fragmentTransaction.add(R.id.repeatPasswordErrorTextView, repeatPasswordError)
        fragmentTransaction.add(R.id.registrationErrorTextView, registrationError)

        val primaryButtonFragment = PrimaryButtonFragment()
        primaryButtonFragment.setButtonText("Next")
        primaryButtonFragment.setButtonAction { Register()}
        fragmentTransaction.add(R.id.primaryButtonContainer, primaryButtonFragment)

        val lineFragment = LineFragment()
        fragmentTransaction.add(R.id.lineContainer, lineFragment)

        val textAndLinkFragment = TextWithLinkFragment()
        textAndLinkFragment.setTextAndLinkText(
            getString(R.string.already_have_an_account),
            getString(R.string.log_in),
            View.OnClickListener {
                val intent = Intent(this, LogInActivity::class.java)
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
            "email" -> emailInput = text
            "password" -> passwordInput = text
            "repeat_password" -> repeatPasswordInput = text
        }
    }

    private fun Register() {
        val user = RegistrationRequest(
            username = usernameInput,
            password = passwordInput,
            repeatPassword = repeatPasswordInput,
            email = emailInput
        )

        authAPI.register(user).enqueue(object : Callback<RegistrationResponse> {
            override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
                if (response.isSuccessful) {
                    val registrationResponse = response.body()

                    if(registrationResponse != null){
                        encryptedPreferencesManager.saveData("email", registrationResponse.email)
                        val intent = Intent(this@RegistrationActivity, ConfirmEmailActivity::class.java)
                        startActivity(intent)

                        Log.i("Registration", "Registration Successful")
                        Log.d("Response", "Response received: ${response.code()}")
                    }

                } else {
                    handleErrors(response.errorBody())
                    Log.e("Registration", "Registration Failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                Log.e("Registration", "Failed: ${t.message}")
            }
        })
    }

    private fun handleErrors(errorBody: ResponseBody?) {
        val errorFragments = mapOf(
            "email" to emailError,
            "username" to usernameError,
            "password" to passwordError,
            "repeatPassword" to repeatPasswordError,
            "registration" to registrationError
        )

        val errorViews = mapOf(
            "email" to binding.emailErrorTextView,
            "username" to binding.usernameErrorTextView,
            "password" to binding.passwordErrorTextView,
            "repeatPassword" to binding.repeatPasswordErrorTextView,
            "registration" to binding.registrationErrorTextView
        )

        ErrorHandler.handleErrors(errorBody, errorFragments, errorViews)
    }
}