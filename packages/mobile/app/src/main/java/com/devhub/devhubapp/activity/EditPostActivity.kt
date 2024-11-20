package com.devhub.devhubapp.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.devhub.devhubapp.R
import com.devhub.devhubapp.api.PostAPI
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.Post
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditPostActivity : AppCompatActivity() {

    private lateinit var postAPI: PostAPI
    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private var headerImageUri: Uri? = null
    private lateinit var post: Post

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
        const val RESULT_UPDATED_POST = "UPDATED_POST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        postAPI = RetrofitClient.getInstance(this).postAPI

        val postJson = intent.getStringExtra("post")
        post = Gson().fromJson(postJson, Post::class.java)

        val userAvatarView = findViewById<ImageView>(R.id.user_avatar)
        val usernameView = findViewById<TextView>(R.id.username)
        val updatePostButton = findViewById<TextView>(R.id.edit_post_button)
        val postTitle = findViewById<EditText>(R.id.title_input)
        val postTags = findViewById<EditText>(R.id.tags_input)
        val postContent = findViewById<EditText>(R.id.content_input)
        val postImage = findViewById<ImageView>(R.id.selected_background_image)
        val addBackgroundButton = findViewById<FrameLayout>(R.id.add_background_button)
        val changeBackgroundButton = findViewById<Button>(R.id.change_background_button)

        val user = encryptedPreferencesManager.getUserData()
        usernameView.text = user.username
        if (user.avatar.isNotEmpty()) {
            Glide.with(this)
                .load(user.avatar)
                .into(userAvatarView)
        }

        postTitle.setText(post.title)
        postContent.setText(post.content)
        postTags.setText(post.tags?.joinToString(","))

        Log.d("EditPostActivity", "Header Image URL: ${post.headerImage}")
        if (!post.headerImage.isNullOrEmpty()) {
            Glide.with(this)
                .load("https://mydevhubimagebucket.s3.eu-west-3.amazonaws.com/" + post.headerImage)
                .into(postImage)
            postImage.visibility = View.VISIBLE
            addBackgroundButton.visibility = View.GONE
            changeBackgroundButton.visibility = View.VISIBLE
        } else {
            postImage.visibility = View.GONE
            addBackgroundButton.visibility = View.VISIBLE
            changeBackgroundButton.visibility = View.GONE
        }

        addBackgroundButton.setOnClickListener {
            openImagePicker()
        }

        changeBackgroundButton.setOnClickListener {
            openImagePicker()
        }

        updatePostButton.setOnClickListener {
            updatePost(imageUri=headerImageUri)
        }

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            headerImageUri = data?.data

            if (headerImageUri != null) {
                val selectedImageView = findViewById<ImageView>(R.id.selected_background_image)

                Glide.with(this)
                    .load(headerImageUri)
                    .into(selectedImageView)
                updateBackgroundControls(showChangeButton = true)
            }
        }
    }

    private fun updateBackgroundControls(showChangeButton: Boolean) {
        val addBackgroundButton = findViewById<FrameLayout>(R.id.add_background_button)
        val selectedImageView = findViewById<ImageView>(R.id.selected_background_image)
        val changeBackgroundButton = findViewById<Button>(R.id.change_background_button)

        if (headerImageUri != null) {
            addBackgroundButton.visibility = View.GONE
            selectedImageView.visibility = View.VISIBLE
            changeBackgroundButton.visibility = View.VISIBLE
        } else {
            addBackgroundButton.visibility = View.VISIBLE
            selectedImageView.visibility = View.GONE
            changeBackgroundButton.visibility = View.INVISIBLE
        }
    }
    private fun updatePost(imageUri: Uri?) {

        Log.d("EditPostActivity", "Update Post function called")
        val titleInput = findViewById<EditText>(R.id.title_input).text.toString()
        val contentInput = findViewById<EditText>(R.id.content_input).text.toString()
        val tagsInput = findViewById<EditText>(R.id.tags_input).text.toString()

        val titleBody = titleInput.toRequestBody("text/plain".toMediaTypeOrNull())
        val contentBody = contentInput.toRequestBody("text/plain".toMediaTypeOrNull())
        val tagsBody = tagsInput.toRequestBody("text/plain".toMediaTypeOrNull())
        val postId = post._id
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

        val call = postAPI.updatePost(postId, titleBody, contentBody, tagsBody, imagePart)
        call.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    val updatedPost = response.body()
                    updatedPost?.let {
                        fetchUpdatedPost(it._id)
                    }
                } else {
                    Log.e("EditPostActivity", "Update failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.e("EditPostActivity", "Update error: ${t.message}")
            }
        })
    }

    private fun fetchUpdatedPost(postId: String) {
        postAPI.getPostById(postId).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    val updatedPost = response.body()
                    updatedPost?.let {
                        navigateToPost(it)
                    }
                } else {
                    Log.e("EditPostActivity", "Fetch failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.e("EditPostActivity", "Fetch error: ${t.message}")
            }
        })
    }

    private fun navigateToPost(updatedPost: Post) {
        val intent = Intent(this, PostActivity::class.java).apply {
            putExtra("post", Gson().toJson(updatedPost))
        }
        startActivity(intent)
        finish()
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