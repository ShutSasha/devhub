package com.devhub.devhubapp.fragment

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.devhub.devhubapp.R
import com.devhub.devhubapp.activity.WelcomeActivity
import com.devhub.devhubapp.api.AuthAPI
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.ErrorResponse
import com.devhub.devhubapp.dataClasses.VerifyEmailRequest
import com.devhub.devhubapp.databinding.FragmentConfirmEmailContainerBinding
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ConfirmEmailContainerFragment : Fragment() {

    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private lateinit var authAPI: AuthAPI
    private lateinit var binding: FragmentConfirmEmailContainerBinding
    private var codeInput: String = ""
    val title = TitleFragment()
    val activationError = ErrorFragment()
    val primaryButtonFragment = PrimaryButtonFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        encryptedPreferencesManager = EncryptedPreferencesManager(requireContext())
        authAPI = RetrofitClient.getInstance(requireContext()).getRetrofit().create(AuthAPI::class.java)
        binding =  FragmentConfirmEmailContainerBinding.inflate(layoutInflater, container, false)

        setUpFragment()

        return binding.root
    }

    private fun setUpFragment(){
        binding.activationError.visibility = View.GONE
        binding.result.visibility = View.GONE

        val fragmentManager : FragmentManager = childFragmentManager
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
        fragmentTransaction.add(R.id.primary_button_container, primaryButtonFragment)
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
                onError()
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

    private fun onSuccess() {

        binding.codeInputContainer.visibility = View.GONE
        binding.tvSubtitle.visibility = View.GONE
        binding.activationError.visibility = View.GONE

        val layoutParams = binding.title.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = 0
        binding.title.layoutParams = layoutParams

        val color = ContextCompat.getColor(requireContext(), R.color.success)
        title.setTextColour(color)

        binding.result.setTextColor(color)
        binding.result.text = "Your email was successfully confirmed!"
        binding.result.visibility = View.VISIBLE

        primaryButtonFragment.setButtonText("Confirm")
        primaryButtonFragment.setButtonAction {
            val intent = Intent(requireContext(), WelcomeActivity::class.java)
            startActivity(intent)
        }

    }

    private fun onError() {

        binding.codeInputContainer.visibility = View.GONE
        binding.tvSubtitle.visibility = View.GONE
        binding.activationError.visibility = View.GONE

        val layoutParams = binding.title.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = 0
        binding.title.layoutParams = layoutParams

        val color = ContextCompat.getColor(requireContext(), R.color.wrong)
        title.setTextColour(color)

        binding.result.setTextColor(color)
        binding.result.text = "Something went wrong, please try again"
        binding.result.visibility = View.VISIBLE

        primaryButtonFragment.setButtonText("Back")
        primaryButtonFragment.setButtonAction {
            val intent = Intent(requireContext(), WelcomeActivity::class.java)
            startActivity(intent)
        }

    }

}