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
import com.devhub.devhubapp.R
import com.devhub.devhubapp.activity.ConfirmEmailActivity
import com.devhub.devhubapp.activity.WelcomeActivity
import com.devhub.devhubapp.api.AuthAPI
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.ErrorResponse
import com.devhub.devhubapp.dataClasses.User
import com.devhub.devhubapp.dataClasses.UserRegistrationRequest
import com.devhub.devhubapp.dataClasses.VerifyEmailRequest
import com.devhub.devhubapp.databinding.FragmentConfirmEmailContainerBinding
import com.devhub.devhubapp.databinding.FragmentLoginContainerBinding
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ConfirmEmailContainerFragment : Fragment() {

    private lateinit var authAPI: AuthAPI
    private lateinit var binding: FragmentConfirmEmailContainerBinding
    private var codeInput: String = ""
    val activationError = ErrorFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authAPI = RetrofitClient.getInstance(requireContext()).getRetrofit().create(AuthAPI::class.java)
        binding =  FragmentConfirmEmailContainerBinding.inflate(layoutInflater, container, false)

        setUpFragment()

        return binding.root
    }

    private fun setUpFragment(){
        binding.activationError.visibility = View.GONE

        val fragmentManager : FragmentManager = childFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()

        val title = TitleFragment()
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
        fragmentTransaction.add(R.id.code_input_container, codeInputFragment)

        fragmentTransaction.add(R.id.activationError, activationError)

        val primaryButtonFragment = PrimaryButtonFragment()
        primaryButtonFragment.setButtonText("Next")
        fragmentTransaction.add(R.id.primary_button_container, primaryButtonFragment)
        primaryButtonFragment.setButtonAction { ConfirmEmail()}

        fragmentTransaction.commit()
    }

    private fun ConfirmEmail(){
        val email = arguments?.getString("email")
        if (email.isNullOrEmpty()) {
            Log.e("ConfirmEmail", "Email is null or empty")
            return
        }

        val verifyEmailRequest = VerifyEmailRequest(
            email = email,
            code = codeInput
        )

        authAPI.verifyEmail(verifyEmailRequest).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val verifyEmailResponse = response.body()

                    if(verifyEmailResponse != null){

                        Log.i("VerifyEmail", "VerifyEmail Successful")
                        Log.d("Response", "Response received: ${response.code()}")

                        val intent = Intent(requireContext(), WelcomeActivity::class.java)
                        startActivity(intent)
                    }

                } else {
                    handleErrors(response.errorBody())
                    Log.e("VerifyEmail", "VerifyEmail Failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("VerifyEmail", "Failed: ${t.message}")
            }
        })
    }

    private fun handleErrors(errorBody: ResponseBody?) {

        activationError.setErrorText("")
        binding.activationError.visibility = View.GONE

        errorBody?.let {
            try {
                val errorJson = it.string()
                val gson = Gson()
                val errorResponse = gson.fromJson(errorJson, ErrorResponse::class.java)

                errorResponse.errors?.let { errors ->
                    errors.ActivationCode?.let { activationCode ->
                        activationError.setErrorText(activationCode.joinToString("\n"))
                        binding.activationError.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.e("Error Parsing", "Error parsing error response: ${e.message}")
            }
        }
    }

}