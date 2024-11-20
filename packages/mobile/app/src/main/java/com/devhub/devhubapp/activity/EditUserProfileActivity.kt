package com.devhub.devhubapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R
import com.devhub.devhubapp.api.UserAPI
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.ErrorHandler
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.EditProfileRequest
import com.devhub.devhubapp.dataClasses.EditProfileResponse
import com.devhub.devhubapp.dataClasses.LoginRequest
import com.devhub.devhubapp.dataClasses.LoginResponse
import com.devhub.devhubapp.dataClasses.User
import com.devhub.devhubapp.databinding.ActivityEditUserProfileBinding
import com.devhub.devhubapp.fragment.ErrorFragment
import com.devhub.devhubapp.fragment.FooterFragment
import com.devhub.devhubapp.fragment.HeaderFragment
import com.devhub.devhubapp.fragment.InputFragment
import com.devhub.devhubapp.fragment.InputTextListener
import com.devhub.devhubapp.fragment.OutlinedButtonFragment
import com.devhub.devhubapp.fragment.PrimaryButtonFragment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class EditUserProfileActivity : AppCompatActivity() {
    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private lateinit var userAPI: UserAPI
    private lateinit var binding: ActivityEditUserProfileBinding
    private var name: String = ""
    private var description: String = ""
    val editUserError = ErrorFragment()
    val updateUserError = ErrorFragment()
    private var selectedImageUri: Uri? = null

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_user_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edit_user_profile_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        userAPI = RetrofitClient.getInstance(this).getRetrofit().create(UserAPI::class.java)
        binding = ActivityEditUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var userId = encryptedPreferencesManager.getData("user_id")

        name = encryptedPreferencesManager.getData("name") ?: ""
        description = intent.getStringExtra("DESCRIPTION") ?: ""


        val fragmentManager = supportFragmentManager
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                .replace(R.id.header_container, HeaderFragment())
                .replace(R.id.footer_container, FooterFragment())
                .commit()
        }

        setUpFragments(userId)
    }

    @SuppressLint("CommitTransaction")
    private fun setUpFragments(
        userId: String?
    ){
        binding.editUserErrorTextView.visibility = View.GONE
        binding.updateUserErrorTextView.visibility = View.GONE

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val nameInputFragment = createInputFragment("name", InputType.TYPE_CLASS_TEXT,
            "", encryptedPreferencesManager.getData("name"))
        fragmentTransaction.add(R.id.nameInputContainer, nameInputFragment)

        val descriptionInputFragment = createInputFragment("description", InputType.TYPE_CLASS_TEXT,
            "", description)
        fragmentTransaction.add(R.id.descriptionInputContainer, descriptionInputFragment)

        fragmentTransaction.add(R.id.editUserErrorTextView, editUserError)

        val outlinedButtonFragment = OutlinedButtonFragment()
        outlinedButtonFragment.setButtonText("Choose file")
        outlinedButtonFragment.setButtonAction {
            openImagePicker()
        }
        fragmentTransaction.add(R.id.btnOutlined, outlinedButtonFragment)

        fragmentTransaction.add(R.id.updateUserErrorTextView, updateUserError)

        val primaryButtonFragment = PrimaryButtonFragment()
        primaryButtonFragment.setButtonText("Save changes")
        primaryButtonFragment.setButtonAction {
            editUserData(userId)
        }
        fragmentTransaction.add(R.id.btnPrimary, primaryButtonFragment)

        fragmentTransaction.commit()

        val avatarPath = encryptedPreferencesManager.getData("avatar")

        if (!avatarPath.isNullOrEmpty()) {
            Glide.with(this)
                .load(avatarPath)
                .circleCrop()
                .into(binding.ivAvatar)
        }
        binding.tvUsername.text = "@${encryptedPreferencesManager.getData("username")}"

    }

    private fun createInputFragment(field: String, inputType: Int, hint: String, text: String?): InputFragment {
        val color = ContextCompat.getColor(this, R.color.text_primary)

        val inputFragment = InputFragment()
        inputFragment.setInputType(inputType)
        inputFragment.setInputHint(hint)
        inputFragment.setInputText(text)
        inputFragment.setTextColor(color)

        inputFragment.setTextWatcher(object : InputTextListener {
            override fun onTextInputChanged(text: String) {
                updateInput(field, text)
            }
        })

        return inputFragment
    }

    fun updateInput(field: String, text: String) {
        when (field) {
            "name" -> name = text
            "description" -> description = text
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            if (selectedImageUri != null) {
                Glide.with(this)
                    .load(selectedImageUri)
                    .circleCrop()
                    .into(binding.ivAvatar)
                binding.tvUploadStatus.text = "File chosen"
                Log.i("ImagePicker", "Selected image URI: $selectedImageUri")
                updatePhoto(encryptedPreferencesManager.getData("user_id"))
            } else {
                binding.tvUploadStatus.text = "No file chosen"
                Log.e("ImagePicker", "No image selected")
            }
        } else {
            Log.e("ImagePicker", "Image picker canceled or failed")
        }
    }

    private fun editUserData(userId: String?){
        val user = EditProfileRequest(
            id = userId,
            name = name,
            bio = description
        )
        userAPI.editUserProfile(user).enqueue(object : Callback<EditProfileResponse> {
            override fun onResponse(call: Call<EditProfileResponse>, response: Response<EditProfileResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    Log.i("EditUserProfile", data.toString())
                    val user = response.body()?.user
                    if (user != null) {

                        encryptedPreferencesManager.saveUserData(user)

                        Log.i("EditUserProfile", user.toString())

                        val intent = Intent(this@EditUserProfileActivity, UserProfileActivity::class.java)
                        intent.putExtra("USER_ID", user._id)
                        startActivity(intent)
                        finish()

                        Log.i("EditUserProfile", "EditUserProfile Successful")
                        Log.d("Response", "Response received: ${response.code()}")

                    }
                } else {
                    handleErrors(response.errorBody())
                    Log.e("EditUserProfile", "EditUserProfile Failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EditProfileResponse>, t: Throwable) {
                Log.e("EditUserProfile", "Failed: ${t.message}")
            }
        })
    }

    private fun updatePhoto(userId: String?) {
        Log.i("UpdatePhoto", "$userId $selectedImageUri")
        if (userId.isNullOrEmpty() || selectedImageUri == null) {
            Log.e("UpdatePhoto", "User ID or selected image is missing")
            return
        }

        val inputStream = contentResolver.openInputStream(selectedImageUri!!)
        val photoBytes = inputStream?.readBytes()
        val base64Photo = android.util.Base64.encodeToString(photoBytes, android.util.Base64.DEFAULT)

        userAPI.updatePhoto(userId, base64Photo).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.i("UpdatePhoto", "Photo updated successfully")
                    val intent = Intent(this@EditUserProfileActivity, UserProfileActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                } else {
                    handleErrors(response.errorBody())
                    Log.e("UpdatePhoto", "Failed to update photo: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("UpdatePhoto", "Error: ${t.message}")
            }
        })
    }

    private fun handleErrors(errorBody: ResponseBody?) {
        val errorFragments = mapOf(
            "editUser" to editUserError,
            "updateUser" to updateUserError
        )

        val errorViews = mapOf(
            "editUser" to binding.editUserErrorTextView,
            "updateUser" to binding.updateUserErrorTextView
        )

        ErrorHandler.handleErrors(errorBody, errorFragments, errorViews)
    }



}