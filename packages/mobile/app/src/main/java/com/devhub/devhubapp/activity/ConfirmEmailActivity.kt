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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.devhub.devhubapp.R
import com.devhub.devhubapp.api.AuthAPI
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.ErrorHandler
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.VerifyEmailRequest
import com.devhub.devhubapp.databinding.ActivityConfirmEmailBinding
import com.devhub.devhubapp.fragment.ErrorFragment
import com.devhub.devhubapp.fragment.InputFragment
import com.devhub.devhubapp.fragment.InputTextListener
import com.devhub.devhubapp.fragment.PrimaryButtonFragment
import com.devhub.devhubapp.fragment.TitleFragment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmEmailActivity : AppCompatActivity() {

    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private lateinit var authAPI: AuthAPI
    private lateinit var binding: ActivityConfirmEmailBinding
    private var codeInput: String = ""
    val title = TitleFragment()
    val activationError = ErrorFragment()
    val primaryButtonFragment = PrimaryButtonFragment()

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        authAPI = RetrofitClient.getInstance(this).getRetrofit().create(AuthAPI::class.java)
        binding = ActivityConfirmEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.confirm_email)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpFragment()

    }

    private fun setUpFragment(){
        binding.activationError.visibility = View.GONE
        binding.result.visibility = View.GONE

        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()

        title.setShowBackArrow(true)
        title.setTitleText("Confirming email")
        fragmentTransaction.add(R.id.title, title)

        val codeInputFragment = InputFragment().apply {
            setInputType(InputType.TYPE_CLASS_NUMBER)
            setInputHint("Enter 6-digit code from email")
            setTextWatcher(object : InputTextListener {
                override fun onTextInputChanged(text: String) {
                    codeInput = text
                }
            })
        }
        fragmentTransaction.add(R.id.codeInputContainer, codeInputFragment)

        fragmentTransaction.add(R.id.activationError, activationError)

        primaryButtonFragment.setButtonText("Next")
        fragmentTransaction.add(R.id.primaryButtonContainer, primaryButtonFragment)
        primaryButtonFragment.setButtonAction { VerifyEmail()}

        fragmentTransaction.commit()
    }

    private fun VerifyEmail(){

        val verifyEmailRequest = VerifyEmailRequest(
            email = encryptedPreferencesManager.getData("email") ?: "",
            activationCode = codeInput
        )

        authAPI.verifyEmail(verifyEmailRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val verifyEmailResponse = response.body()

                    if(verifyEmailResponse != null){

                        Log.i("VerifyEmail", "VerifyEmail Successful")
                        Log.d("Response", "Response received: ${response.code()}")

                        onSuccess()
                    }

                } else {
                    handleErrors(response.errorBody())
                    Log.e("VerifyEmail", "VerifyEmail Failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("VerifyEmail", "Failed: ${t.message}")
                onWrong()
            }
        })
    }

    private fun handleErrors(errorBody: ResponseBody?) {
        val errorFragments = mapOf(
            "activationCode" to activationError
        )

        val errorViews = mapOf(
            "activationCode" to binding.activationError
        )

        ErrorHandler.handleErrors(errorBody, errorFragments, errorViews)
    }

    private fun onSuccess() {

        binding.codeInputContainer.visibility = View.GONE
        binding.subtitleTextView.visibility = View.GONE
        binding.activationError.visibility = View.GONE

        val layoutParams = binding.title.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = 0
        binding.title.layoutParams = layoutParams

        val color = ContextCompat.getColor(this, R.color.success)
        title.setTextColour(color)
        title.setTitleText("Success")

        binding.result.setTextColor(color)
        binding.result.text = "Your email was successfully confirmed!"
        binding.result.visibility = View.VISIBLE

        primaryButtonFragment.setButtonText("Confirm")
        primaryButtonFragment.setButtonAction {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }

    }

    private fun onWrong() {

        binding.codeInputContainer.visibility = View.GONE
        binding.subtitleTextView.visibility = View.GONE
        binding.activationError.visibility = View.GONE

        val layoutParams = binding.title.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = 0
        binding.title.layoutParams = layoutParams

        val color = ContextCompat.getColor(this, R.color.wrong)
        title.setTextColour(color)
        title.setTitleText("Wrong")

        binding.result.setTextColor(color)
        binding.result.text = "Something went wrong, please try again"
        binding.result.visibility = View.VISIBLE

        primaryButtonFragment.setButtonText("Back")
        primaryButtonFragment.setButtonAction {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }

    }

}