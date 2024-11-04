package com.devhub.devhubapp.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R
import com.devhub.devhubapp.api.PostAPI
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.Post
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import android.provider.OpenableColumns
import androidx.core.content.FileProvider

import android.widget.Toast
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class CreatePostActivity : AppCompatActivity() {
    private lateinit var postAPI: PostAPI
    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        postAPI = RetrofitClient.getInstance(this).getRetrofit().create(PostAPI::class.java)

        val userAvatar = findViewById<ImageView>(R.id.user_avatar)
        val username = findViewById<TextView>(R.id.username)
        val createPostButton = findViewById<TextView>(R.id.create_post_button)
        val postTitle = findViewById<EditText>(R.id.title_input)
        val postTags = findViewById<EditText>(R.id.tags_input)
        val postContent = findViewById<EditText>(R.id.content_input)
        val addBackgroundButton = findViewById<FrameLayout>(R.id.add_background_button)

        val usernameText = intent.getStringExtra("USERNAME") ?: "@username"
        val userAvatarUrl = intent.getStringExtra("USER_AVATAR")

        username.text = usernameText

        if (!userAvatarUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(userAvatarUrl)
                .into(userAvatar)
        }

        createPostButton.setOnClickListener {
            createNewPost(
                userId = encryptedPreferencesManager.getUserData()?.id ?: "",
                title = postTitle.text.toString(),
                content = postContent.text.toString(),
                tags = postTags.text.toString().split(",").map { it.trim() },
                imageUri = selectedImageUri // Make sure to declare this variable globally
            )
        }


        addBackgroundButton.isClickable = true
        addBackgroundButton.setOnClickListener {
            Toast.makeText(this, "Background button clicked", Toast.LENGTH_SHORT).show() // Сообщение на экране
            openImagePicker()
        }
    }

    private fun createNewPost(userId: String, title: String, content: String, tags: List<String>, imageUri: Uri?) {
        val userIdBody = userId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val titleBody = title.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val contentBody = content.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val tagsBody = tags.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        var imagePart: MultipartBody.Part? = null
        if (imageUri != null) {
            val imageFile = getFileFromUri(imageUri)
            if (imageFile != null) {
                val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                imagePart = MultipartBody.Part.createFormData("headerImage", imageFile.name, requestFile)
            }
        }

        postAPI.createPost(userIdBody, titleBody, contentBody, tagsBody, imagePart).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    val intent = Intent(this@CreatePostActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("UPDATE_POSTS", true)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("CreatePostActivity", "Failed to create post: ${response.message()}")
                    response.errorBody()?.let { errorBody ->
                        Log.e("CreatePostActivity", "Error body: ${errorBody.string()}")
                    }
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.e("CreatePostActivity", "Error creating post: ${t.message}", t)
            }
        })
    }



    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    private var selectedImageUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            if (selectedImageUri != null) {
                val backgroundButton = findViewById<FrameLayout>(R.id.add_background_button)
                val selectedImageView = findViewById<ImageView>(R.id.selected_background_image)

                backgroundButton.visibility = View.GONE
                selectedImageView.visibility = View.VISIBLE

                Glide.with(this)
                    .load(selectedImageUri)
                    .into(selectedImageView)
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        val filePath: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && it.moveToFirst()) {
                val displayName = it.getString(nameIndex)
                val inputStream = contentResolver.openInputStream(uri)
                val file = File(cacheDir, displayName)
                file.outputStream().use { output ->
                    inputStream?.copyTo(output)
                }
                return file
            }
        }
        return null
    }



}
