package com.devhub.devhubapp.fragment

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.devhub.devhubapp.dataClasses.ErrorResponse
import com.devhub.devhubapp.R
import com.devhub.devhubapp.activity.ConfirmEmailActivity
import com.devhub.devhubapp.dataClasses.User
import com.devhub.devhubapp.api.AuthAPI
import com.devhub.devhubapp.dataClasses.RegistrationRequest
import com.devhub.devhubapp.activity.LogInActivity
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.RegistrationResponse
import com.devhub.devhubapp.databinding.FragmentRegistrationContainerBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody

class RegistrationContainerFragment : Fragment(){
    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private lateinit var authAPI: AuthAPI

    private lateinit var binding: FragmentRegistrationContainerBinding

    val usernameError = ErrorFragment()
    val emailError = ErrorFragment()
    val passwordError = ErrorFragment()
    val repeatPasswordError = ErrorFragment()
    val registrationError = ErrorFragment()

    private var usernameInput: String = ""
    private var emailInput: String = ""
    private var passwordInput: String = ""
    private var repeatPasswordInput: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        encryptedPreferencesManager = EncryptedPreferencesManager(requireContext())
        authAPI = RetrofitClient.getInstance(requireContext()).getRetrofit().create(AuthAPI::class.java)
        binding = FragmentRegistrationContainerBinding.inflate(layoutInflater, container, false)

        setUpFragments()

        return binding.root
    }

    private fun setUpFragments(){
        binding.emailErrorTextView.visibility = View.GONE
        binding.usernameErrorTextView.visibility = View.GONE
        binding.passwordErrorTextView.visibility = View.GONE
        binding.repeatPasswordErrorTextView.visibility = View.GONE
        binding.registrationErrorTextView.visibility = View.GONE

        val fragmentManager : FragmentManager = childFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()

        val title = TitleFragment()
        title.setShowBackArrow(true)
        title.setTitleText("Registration")
        fragmentTransaction.add(R.id.title, title)

        val usernameInputFragment = createInputFragment("username", InputType.TYPE_CLASS_TEXT, "Enter your username")
        fragmentTransaction.add(R.id.username_input_container, usernameInputFragment)

        fragmentTransaction.add(R.id.usernameErrorTextView, usernameError)

        val emailInputFragment = createInputFragment("email", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, "Enter your email")
        fragmentTransaction.add(R.id.email_input_container, emailInputFragment)

        fragmentTransaction.add(R.id.emailErrorTextView, emailError)

        val passwordInputFragment = createInputFragment("password", InputType.TYPE_TEXT_VARIATION_PASSWORD, "Enter your password")
        fragmentTransaction.add(R.id.password_input_container, passwordInputFragment)

        fragmentTransaction.add(R.id.passwordErrorTextView, passwordError)

        val repeatPasswordInputFragment = createInputFragment("repeat_password", InputType.TYPE_TEXT_VARIATION_PASSWORD, "Repeat your password")
        fragmentTransaction.add(R.id.repeat_password_input_container, repeatPasswordInputFragment)

        fragmentTransaction.add(R.id.repeatPasswordErrorTextView, repeatPasswordError)
        fragmentTransaction.add(R.id.registrationErrorTextView, registrationError)

        val primaryButtonFragment = PrimaryButtonFragment()
        primaryButtonFragment.setButtonText("Next")
        primaryButtonFragment.setButtonAction { Register()}
        fragmentTransaction.add(R.id.primary_button_container, primaryButtonFragment)

        val lineFragment = LineFragment()
        fragmentTransaction.add(R.id.line, lineFragment)

        val textAndLinkFragment = TextWithLinkFragment()
        textAndLinkFragment.setTextAndLinkText(
            getString(R.string.already_have_an_account),
            getString(R.string.log_in),
            View.OnClickListener {
                val intent = Intent(requireContext(), LogInActivity::class.java)
                startActivity(intent)
            }
        )
        fragmentTransaction.add(R.id.textAndLinkText, textAndLinkFragment)

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
                        val intent = Intent(requireContext(), ConfirmEmailActivity::class.java)
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
                Toast.makeText(requireContext(), "Request Failed: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("Registration", "Failed: ${t.message}")
            }
        })
    }

    private fun handleErrors(errorBody: ResponseBody?) {

        emailError.setErrorText("")
        usernameError.setErrorText("")
        passwordError.setErrorText("")
        repeatPasswordError.setErrorText("")

        binding.emailErrorTextView.visibility = View.GONE
        binding.usernameErrorTextView.visibility = View.GONE
        binding.passwordErrorTextView.visibility = View.GONE
        binding.repeatPasswordErrorTextView.visibility = View.GONE

        errorBody?.let {
            try {
                val errorJson = it.string()
                val gson = Gson()
                val errorResponse = gson.fromJson(errorJson, ErrorResponse::class.java)

                errorResponse.errors?.let { errors ->
                    errors.Email?.let { emailErrors ->
                        emailError.setErrorText(emailErrors.joinToString("\n"))
                        binding.emailErrorTextView.visibility = View.VISIBLE
                    }

                    errors.Username?.let { usernameErrors ->
                        usernameError.setErrorText(usernameErrors.joinToString("\n"))
                        binding.usernameErrorTextView.visibility = View.VISIBLE
                    }

                    errors.Password?.let { passwordErrors ->
                        passwordError.setErrorText(passwordErrors.joinToString("\n"))
                        binding.passwordErrorTextView.visibility = View.VISIBLE
                    }

                    errors.RepeatPassword?.let { repeatPasswordErrors ->
                        repeatPasswordError.setErrorText(repeatPasswordErrors.joinToString("\n"))
                        binding.repeatPasswordErrorTextView.visibility = View.VISIBLE
                    }
                    errors.RegistrationError?.let { registrationErrors ->
                        registrationError.setErrorText(registrationErrors.joinToString("\n"))
                        binding.registrationErrorTextView.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.e("Error Parsing", "Error parsing error response: ${e.message}")
            }
        }
    }
}


