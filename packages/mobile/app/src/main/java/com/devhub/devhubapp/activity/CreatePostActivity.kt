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
        private const val MAX_TAGS = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        postAPI = RetrofitClient.getInstance(this).getRetrofit().create(PostAPI::class.java)

        val userAvatarView = findViewById<ImageView>(R.id.user_avatar)
        val usernameView = findViewById<TextView>(R.id.username)
        val createPostButton = findViewById<TextView>(R.id.create_post_button)
        val postTitle = findViewById<EditText>(R.id.title_input)
        val postTags = findViewById<EditText>(R.id.tags_input)
        val postContent = findViewById<EditText>(R.id.content_input)
        val addBackgroundButton = findViewById<FrameLayout>(R.id.add_background_button)
        val titleErrorView = findViewById<TextView>(R.id.title_error)
        val contentErrorView = findViewById<TextView>(R.id.content_error)
        val tagsErrorView = findViewById<TextView>(R.id.tags_error)

        val user = encryptedPreferencesManager.getUserData()
        usernameView.text = user.username
        if (user.avatar.isNotEmpty()) {
            Glide.with(this)
                .load(user.avatar)
                .into(userAvatarView)
        }

        createPostButton.setOnClickListener {
            val titleText = postTitle.text.toString().trim()
            val contentText = postContent.text.toString().trim()
            val tagsText = postTags.text.toString().trim()
            val tagsList = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }

            titleErrorView.visibility = View.GONE
            contentErrorView.visibility = View.GONE
            tagsErrorView.visibility = View.GONE

            var isValid = true

            if (titleText.isEmpty()) {
                titleErrorView.visibility = View.VISIBLE
                titleErrorView.text = "Please enter a title"
                isValid = false
            }

            if (contentText.isEmpty()) {
                contentErrorView.visibility = View.VISIBLE
                contentErrorView.text = "Please enter content"
                isValid = false
            }

            if (tagsList.size > MAX_TAGS) {
                tagsErrorView.visibility = View.VISIBLE
                tagsErrorView.text = "Too many tags! Please use $MAX_TAGS or fewer."
                isValid = false
            }

            if (isValid) {
                createNewPost(
                    userId = user._id,
                    title = titleText,
                    content = contentText,
                    tags = tagsList,
                    imageUri = selectedImageUri
                )
            }
        }

        addBackgroundButton.isClickable = true
        addBackgroundButton.setOnClickListener {
            Toast.makeText(this, "Background button clicked", Toast.LENGTH_SHORT).show()
            openImagePicker()
        }
    }

    private fun createNewPost(
        userId: String,
        title: String,
        content: String,
        tags: List<String>,
        imageUri: Uri?
    ) {
        val userIdBody = userId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val titleBody = title.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val contentBody = content.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val tagsBody = tags.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        var imagePart: MultipartBody.Part? = null
        if (imageUri != null) {
            val imageFile = getFileFromUri(imageUri)
            if (imageFile != null) {
                val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                imagePart = MultipartBody.Part.createFormData(
                    "headerImage",
                    imageFile.name,
                    requestFile
                )
            }
        }

        postAPI.createPost(userIdBody, titleBody, contentBody, tagsBody, imagePart)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (response.isSuccessful) {
                        val intent = Intent(this@CreatePostActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra("UPDATE_POSTS", true)
                        startActivity(intent)
                        finish()
                    } else {
                        response.errorBody()?.string()?.let { errorBody ->
                            if (errorBody.contains("Field validation for 'Title' failed")) {
                                findViewById<TextView>(R.id.title_error).apply {
                                    text = "Title is required"
                                    visibility = View.VISIBLE
                                }
                            }
                            if (errorBody.contains("Field validation for 'Content' failed")) {
                                findViewById<TextView>(R.id.content_error).apply {
                                    text = "Content is required"
                                    visibility = View.VISIBLE
                                }
                            }
                            if (errorBody.contains("too many tags")) {
                                findViewById<TextView>(R.id.tags_error).apply {
                                    text = "Too many tags! Please use $MAX_TAGS or fewer."
                                    visibility = View.VISIBLE
                                }
                            }
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
