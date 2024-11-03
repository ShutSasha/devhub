package com.devhub.devhubapp.classes

import android.view.View
import android.util.Log
import com.google.gson.Gson
import okhttp3.ResponseBody
import com.devhub.devhubapp.dataClasses.ErrorResponse
import com.devhub.devhubapp.fragment.ErrorFragment
import com.google.gson.annotations.SerializedName

object ErrorHandler {

    fun handleErrors(
        errorBody: ResponseBody?,
        errorFragments: Map<String, ErrorFragment>,
        errorViews: Map<String, View>
    ) {
        errorFragments.forEach { (_, fragment) ->
            fragment.setErrorText("")
        }
        errorViews.forEach { (_, view) ->
            view.visibility = View.GONE
        }

        errorBody?.let {
            try {
                val errorJson = it.string()
                val gson = Gson()
                val errorResponse = gson.fromJson(errorJson, ErrorResponse::class.java)

                errorResponse.errors?.let { errors ->
                    errors.Email?.let { emailErrors ->
                        errorFragments["email"]?.setErrorText(emailErrors.joinToString("\n"))
                        errorViews["email"]?.visibility = View.VISIBLE
                    }
                    errors.Username?.let { usernameErrors ->
                        errorFragments["username"]?.setErrorText(usernameErrors.joinToString("\n"))
                        errorViews["username"]?.visibility = View.VISIBLE
                    }
                    errors.Password?.let { passwordErrors ->
                        errorFragments["password"]?.setErrorText(passwordErrors.joinToString("\n"))
                        errorViews["password"]?.visibility = View.VISIBLE
                    }
                    errors.RepeatPassword?.let { repeatPasswordErrors ->
                        errorFragments["repeatPassword"]?.setErrorText(repeatPasswordErrors.joinToString("\n"))
                        errorViews["repeatPassword"]?.visibility = View.VISIBLE
                    }
                    errors.RegistrationError?.let { registrationErrors ->
                        errorFragments["registration"]?.setErrorText(registrationErrors.joinToString("\n"))
                        errorViews["registration"]?.visibility = View.VISIBLE
                    }
                    errors.LoginError?.let { loginErrors ->
                        errorFragments["login"]?.setErrorText(loginErrors.joinToString("\n"))
                        errorViews["login"]?.visibility = View.VISIBLE
                    }
                    errors.VerificationError?.let { verificationErrors ->
                        errorFragments["verification"]?.setErrorText(verificationErrors.joinToString("\n"))
                        errorViews["verification"]?.visibility = View.VISIBLE
                    }
                    errors.ActivationCode?.let { activationCodeErrors ->
                        errorFragments["activationCode"]?.setErrorText(activationCodeErrors.joinToString("\n"))
                        errorViews["activationCode"]?.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.e("Error Parsing", "Error parsing error response: ${e.message}")
            }
        }
    }
}
