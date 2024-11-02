package com.devhub.devhubapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.devhub.devhubapp.R
import com.devhub.devhubapp.api.AuthAPI
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.ChangePasswordRequest
import com.devhub.devhubapp.dataClasses.ErrorResponse
import com.devhub.devhubapp.dataClasses.PasswordVerificationRequest
import com.devhub.devhubapp.dataClasses.VerifyEmailRequest
import com.devhub.devhubapp.databinding.ActivityForgotPasswordBinding
import com.devhub.devhubapp.databinding.FragmentLoginContainerBinding
import com.devhub.devhubapp.fragment.ErrorFragment
import com.devhub.devhubapp.fragment.InputFragment
import com.devhub.devhubapp.fragment.InputTextListener
import com.devhub.devhubapp.fragment.PrimaryButtonFragment
import com.devhub.devhubapp.fragment.TitleFragment
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private lateinit var authAPI: AuthAPI
    private lateinit var binding : ActivityForgotPasswordBinding

    private var emailInput: String = ""
    private var codeInput: String = ""
    private var passwordInput: String = ""
    private var repeatPasswordInput: String = ""

    val emailError = ErrorFragment()
    val verificationCodeError = ErrorFragment()
    val message = ErrorFragment()
    val passwordError = ErrorFragment()
    val repeatPasswordError = ErrorFragment()

    val emailInputFragment = InputFragment()
    val codeInputFragment = InputFragment()
    val passwordInputFragment = InputFragment()
    val repeatPasswordInputFragment = InputFragment()

    val primaryButtonFragment = PrimaryButtonFragment()
    val title = TitleFragment()

    private var isPasswordVerificationCode = true
    private var isVerifyCode = false
    private var isChangePassword = false

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        authAPI = RetrofitClient.getInstance(this).getRetrofit().create(AuthAPI::class.java)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forgot_password)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpPasswordVerificationCodeFragments()

        primaryButtonFragment.setButtonAction { onPrimaryButtonClick()}
    }

    private fun passwordVerificationCode(){
        val passwordVerificationCode = PasswordVerificationRequest(
            email = emailInput
        )

        authAPI.passwordVerificationCode(passwordVerificationCode).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val message = response.body()

                    if(message != null){

                        isPasswordVerificationCode = false
                        isVerifyCode = true

                        setUpVerifyEmailFragments()
                        Log.i("PasswordVerificationRequest", "PasswordVerificationRequest Successful")
                        Log.d("Response", "Response received: ${response.code()}")
                    }

                } else {
                    handleErrors(response.errorBody())
                    Log.e("PasswordVerificationRequest", "PasswordVerificationRequest Failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("PasswordVerificationRequest", "Failed: ${t.message}")
            }
        })
    }

    private fun verifyEmail(){

        val verifyEmailRequest = VerifyEmailRequest(
            email = emailInput,
            activationCode = codeInput
        )

        Log.i("VerifyEmail", "${emailInput}")
        Log.i("VerifyEmail", "${codeInput}")
        authAPI.verifyEmail(verifyEmailRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val verifyEmailResponse = response.body()

                    if(verifyEmailResponse != null){

                        isVerifyCode = false
                        isChangePassword = true
                        setUpChangePasswordFragments()

                        Log.i("VerifyEmail", "VerifyEmail Successful")
                        Log.d("Response", "Response received: ${response.code()}")
                    }

                } else {
                    handleErrors(response.errorBody())
                    Log.e("VerifyEmail", "VerifyEmail Failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("VerifyEmail", "Failed: ${t.message}")
            }
        })
    }

    private fun changePassword(){
        val changePassword = ChangePasswordRequest(
            email = emailInput,
            password = passwordInput,
            repeatPassword = repeatPasswordInput
        )

        authAPI.changePassword(changePassword).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val message = response.body()

                    if(message != null){

                        onSuccess()

                        Log.i("ChangePassword", "changePassword Successful")
                        Log.d("Response", "Response received: ${response.code()}")
                    }

                } else {
                    handleErrors(response.errorBody())
                    Log.e("changePassword", "changePassword Failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onWrong()
                Log.e("changePassword", "Failed: ${t.message}")
            }
        })
    }

    private fun onPrimaryButtonClick(){
        if(isPasswordVerificationCode){
            passwordVerificationCode()
        }
        else if(isVerifyCode){
            verifyEmail()
        }
        else {
            changePassword()
        }
    }

    private fun setUpPasswordVerificationCodeFragments(){

        binding.emailErrorTextView.visibility = View.GONE
        binding.verificationCodeErrorTextView.visibility = View.GONE
        binding.passwordErrorTextView.visibility = View.GONE
        binding.repeatPasswordErrorTextView.visibility = View.GONE
        binding.message.visibility = View.GONE
        binding.codeInputContainer.visibility = View.GONE
        binding.passwordInputContainer.visibility = View.GONE
        binding.repeatPasswordInputContainer.visibility = View.GONE

        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()

        title.setShowBackArrow(true)
        title.setTitleText("Forgot password")
        fragmentTransaction.add(R.id.title, title)

        emailInputFragment.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        emailInputFragment.setInputHint("Enter your email")
        fragmentTransaction.add(R.id.emailInputContainer, emailInputFragment)
        emailInputFragment.setTextWatcher(object : InputTextListener {
            override fun onTextInputChanged(text: String) {
                emailInput = text
            }
        })

        codeInputFragment.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        codeInputFragment.setInputHint("Enter code from email")
        fragmentTransaction.add(R.id.codeInputContainer, codeInputFragment)
        codeInputFragment.setTextWatcher(object : InputTextListener {
            override fun onTextInputChanged(text: String) {
                codeInput = text
            }
        })

        passwordInputFragment.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
        passwordInputFragment.setInputHint("Enter new password")
        fragmentTransaction.add(R.id.passwordInputContainer, passwordInputFragment)
        passwordInputFragment.setTextWatcher(object : InputTextListener {
            override fun onTextInputChanged(text: String) {
                passwordInput = text
            }
        })

        repeatPasswordInputFragment.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
        repeatPasswordInputFragment.setInputHint("Repeat new password")
        fragmentTransaction.add(R.id.repeatPasswordInputContainer, repeatPasswordInputFragment)
        repeatPasswordInputFragment.setTextWatcher(object : InputTextListener {
            override fun onTextInputChanged(text: String) {
                repeatPasswordInput = text
            }
        })

        fragmentTransaction.add(R.id.emailErrorTextView, emailError)
        fragmentTransaction.add(R.id.verificationCodeErrorTextView, verificationCodeError)
        fragmentTransaction.add(R.id.message, message)
        fragmentTransaction.add(R.id.passwordErrorTextView, passwordError)
        fragmentTransaction.add(R.id.repeatPasswordErrorTextView, repeatPasswordError)

        primaryButtonFragment.setButtonText("Next")
        fragmentTransaction.add(R.id.primary_button_container, primaryButtonFragment)

        fragmentTransaction.commit()
    }

    private fun setUpVerifyEmailFragments(){
        binding.emailErrorTextView.visibility = View.GONE
        binding.verificationCodeErrorTextView.visibility = View.GONE
        binding.tvSubtitle.visibility = View.GONE
        binding.message.visibility = View.GONE
        binding.emailInputContainer.visibility = View.GONE
        binding.codeInputContainer.visibility = View.VISIBLE
        binding.repeatPasswordInputContainer.visibility = View.GONE

        val layoutParams = binding.primaryButtonContainer.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = 20
        binding.primaryButtonContainer.layoutParams = layoutParams
    }

    private fun setUpChangePasswordFragments(){
        binding.emailErrorTextView.visibility = View.GONE
        binding.verificationCodeErrorTextView.visibility = View.GONE
        binding.tvSubtitle.visibility = View.GONE
        binding.message.visibility = View.GONE
        binding.emailInputContainer.visibility = View.GONE
        binding.codeInputContainer.visibility = View.GONE
        binding.passwordInputContainer.visibility = View.VISIBLE
        binding.repeatPasswordInputContainer.visibility = View.VISIBLE

        val layoutParams = binding.primaryButtonContainer.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = 20
        binding.primaryButtonContainer.layoutParams = layoutParams

        primaryButtonFragment.setButtonText("Confirm")
    }

    private fun onSuccess(){
        binding.emailErrorTextView.visibility = View.GONE
        binding.verificationCodeErrorTextView.visibility = View.GONE
        binding.tvSubtitle.visibility = View.GONE
        binding.message.visibility = View.GONE
        binding.emailInputContainer.visibility = View.GONE
        binding.codeInputContainer.visibility = View.GONE
        binding.passwordInputContainer.visibility = View.GONE
        binding.repeatPasswordInputContainer.visibility = View.GONE
        binding.emailErrorTextView.visibility = View.GONE
        binding.verificationCodeErrorTextView.visibility = View.GONE
        binding.passwordErrorTextView.visibility = View.GONE
        binding.repeatPasswordErrorTextView.visibility = View.GONE

        val layoutParams = binding.primaryButtonContainer.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = 20
        binding.primaryButtonContainer.layoutParams = layoutParams

        val color = ContextCompat.getColor(this, R.color.success)
        title.setTextColour(color)
        title.setTitleText("Success")

        primaryButtonFragment.setButtonText("Back to log in")
        primaryButtonFragment.setButtonAction {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onWrong(){
        binding.emailErrorTextView.visibility = View.GONE
        binding.verificationCodeErrorTextView.visibility = View.GONE
        binding.tvSubtitle.visibility = View.GONE
        binding.message.visibility = View.GONE
        binding.emailInputContainer.visibility = View.GONE
        binding.codeInputContainer.visibility = View.GONE
        binding.passwordInputContainer.visibility = View.GONE
        binding.repeatPasswordInputContainer.visibility = View.GONE
        binding.emailErrorTextView.visibility = View.GONE
        binding.verificationCodeErrorTextView.visibility = View.GONE
        binding.passwordErrorTextView.visibility = View.GONE
        binding.repeatPasswordErrorTextView.visibility = View.GONE

        val layoutParams = binding.primaryButtonContainer.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = 20
        binding.primaryButtonContainer.layoutParams = layoutParams

        val color = ContextCompat.getColor(this, R.color.wrong)
        title.setTextColour(color)
        title.setTitleText("Wrong")

        primaryButtonFragment.setButtonText("Back to log in")
        primaryButtonFragment.setButtonAction {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleErrors(errorBody: ResponseBody?) {
        emailError.setErrorText("")
        binding.emailErrorTextView.visibility = View.GONE

        verificationCodeError.setErrorText("")
        binding.verificationCodeErrorTextView.visibility = View.GONE

        message.setErrorText("")
        binding.message.visibility = View.GONE

        passwordError.setErrorText("")
        binding.passwordErrorTextView.visibility = View.GONE

        repeatPasswordError.setErrorText("")
        binding.repeatPasswordErrorTextView.visibility = View.GONE

        errorBody?.let {
            try {
                val errorJson = it.string()
                val gson = Gson()
                val errorResponse = gson.fromJson(errorJson, ErrorResponse::class.java)

                errorResponse.errors?.let { errors ->
                    errors.VerificationError?.let { verificationErrors ->
                        emailError.setErrorText(verificationErrors.joinToString("\n"))
                        binding.emailErrorTextView.visibility = View.VISIBLE
                    }

                    errors.Email?.let { emailErrors ->
                        emailError.setErrorText(emailErrors.joinToString("\n"))
                        binding.emailErrorTextView.visibility = View.VISIBLE
                    }

                    errors.ActivationCode?.let { activationCodeErrors ->
                        message.setErrorText(activationCodeErrors.joinToString("\n"))
                        binding.message.visibility = View.VISIBLE
                    }

                    errors.Password?.let { passwordErrors ->
                        passwordError.setErrorText(passwordErrors.joinToString("\n"))
                        binding.passwordErrorTextView.visibility = View.VISIBLE
                    }

                    errors.RepeatPassword?.let { repeatPasswordErrors ->
                        repeatPasswordError.setErrorText(repeatPasswordErrors.joinToString("\n"))
                        binding.repeatPasswordErrorTextView.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.e("Error Parsing", "Error parsing error response: ${e.message}")
            }
        }
    }
}