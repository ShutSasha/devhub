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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import android.provider.OpenableColumns
import android.widget.Button
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
        postAPI = RetrofitClient.getInstance(this).postAPI
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

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        val changeBackgroundButton = findViewById<Button>(R.id.change_background_button)
        changeBackgroundButton.setOnClickListener {
            openImagePicker()
        }

        updateBackgroundControls(showChangeButton = false)

        createPostButton.setOnClickListener {
            val titleText = postTitle.text.toString().trim()
            val contentText = postContent.text.toString().trim()
            val tagsText = postTags.text.toString().trim()
            val tagsList = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }

            titleErrorView.visibility = View.GONE
            contentErrorView.visibility = View.GONE
            tagsErrorView.visibility = View.GONE

            var isValid = validateInputs(titleText, contentText, tagsText)

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
                val tagsList =
                    tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }.take(MAX_TAGS)
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

        val userIdPart = userId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val titlePart = title.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val contentPart = content.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val tagsPart = tags.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

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

        val call = postAPI.createPost(
            userId = userIdPart,
            title = titlePart,
            content = contentPart,
            tags = tagsPart,
            headerImage = imagePart
        )
        call.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    val intent = Intent()
                    intent.putExtra("UPDATE_POSTS", true)
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    Log.e(
                        "CreatePostActivity",
                        "Ошибка создания поста: ${response.errorBody()?.string()}"
                    )
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

    private fun updateBackgroundControls(showChangeButton: Boolean) {
        val addBackgroundButton = findViewById<FrameLayout>(R.id.add_background_button)
        val selectedImageView = findViewById<ImageView>(R.id.selected_background_image)
        val changeBackgroundButton = findViewById<Button>(R.id.change_background_button)

        if (selectedImageUri != null) {
            addBackgroundButton.visibility = View.GONE
            selectedImageView.visibility = View.VISIBLE
            changeBackgroundButton.visibility = View.VISIBLE
        } else {
            addBackgroundButton.visibility = View.VISIBLE
            selectedImageView.visibility = View.GONE
            changeBackgroundButton.visibility = View.INVISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            if (selectedImageUri != null) {
                val selectedImageView = findViewById<ImageView>(R.id.selected_background_image)

                Glide.with(this)
                    .load(selectedImageUri)
                    .into(selectedImageView)
                updateBackgroundControls(showChangeButton = true)
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

    private fun validateInputs(title: String, content: String, tags: String): Boolean {
        var isValid = true
        val titleErrorView = findViewById<TextView>(R.id.title_error)
        val contentErrorView = findViewById<TextView>(R.id.content_error)
        val tagsErrorView = findViewById<TextView>(R.id.tags_error)
        if (title.isEmpty()) {
            titleErrorView.visibility = View.VISIBLE
            isValid = false
        } else {
            titleErrorView.visibility = View.GONE
        }

        if (content.isEmpty()) {
            contentErrorView.visibility = View.VISIBLE
            isValid = false
        } else {
            contentErrorView.visibility = View.GONE
        }

        if (tags.split(",").any { it.trim().isEmpty() }) {
            tagsErrorView.visibility = View.VISIBLE
            isValid = false
        } else {
            tagsErrorView.visibility = View.GONE
        }

        return isValid
    }
}