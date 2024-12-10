package com.devhub.devhubapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.devhub.devhubapp.R
import com.devhub.devhubapp.classes.EncryptedPreferencesManager
import com.devhub.devhubapp.classes.RetrofitClient
import com.devhub.devhubapp.dataClasses.Post
import com.devhub.devhubapp.dataClasses.UserReactions
import com.devhub.devhubapp.fragment.FooterFragment
import com.devhub.devhubapp.fragment.HeaderFragment
import com.devhub.devhubapp.fragment.PostFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {
    @SuppressLint("CommitTransaction")
    private var currentPage = 1
    private var isLoading = false
    private lateinit var encryptedPreferencesManager: EncryptedPreferencesManager
    private lateinit var userReactions: UserReactions
    private val existingPostsIds = mutableSetOf<String>()
    private val REQUEST_CODE_CREATE_POST = 101
    private lateinit var searchInput: EditText
    private lateinit var searchButton: Button
    private lateinit var postsContainer: LinearLayout
    private lateinit var filterToggleButton: LinearLayout
    private lateinit var sortButton: LinearLayout
    private lateinit var tagSearchInput: EditText
    private lateinit var applyFiltersButton: Button
    private val selectedTopTags = mutableSetOf<String>()
    private var selectedSortOption: String = "desc"
    private lateinit var addTagButton: Button
    private lateinit var selectedTagsContainer: LinearLayout
    private lateinit var topTagsContainer: LinearLayout
    private val selectedTags = mutableSetOf<String>()
    private lateinit var filterSection: LinearLayout
    private lateinit var sortOptionsContainer: LinearLayout
    private lateinit var sortAscCheckBox: CheckBox
    private lateinit var sortDescCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        val user = encryptedPreferencesManager.getUserData()

        searchInput = findViewById(R.id.search_input)
        searchButton = findViewById(R.id.search_button)
        postsContainer = findViewById(R.id.posts_container)
        addTagButton = findViewById(R.id.add_tag_button)
        filterToggleButton = findViewById(R.id.filter_toggle_button_container)
        sortButton = findViewById(R.id.sort_button_container)
        selectedTagsContainer = findViewById(R.id.selected_tags_container)
        topTagsContainer = findViewById(R.id.top_tags_container)
        filterSection = findViewById(R.id.filter_section)
        tagSearchInput = findViewById(R.id.tag_search_input)
        applyFiltersButton = findViewById(R.id.apply_filters_button)
        sortOptionsContainer = findViewById(R.id.sort_options_container)
        sortAscCheckBox = findViewById(R.id.sort_asc)
        sortDescCheckBox = findViewById(R.id.sort_desc)

        searchButton.setOnClickListener {
            currentPage = 1
            existingPostsIds.clear()
            postsContainer.removeAllViews()
            performSearch()
        }

        filterToggleButton.setOnClickListener {
            if (filterSection.visibility == View.GONE) {
                filterSection.visibility = View.VISIBLE
            } else {
                filterSection.visibility = View.GONE
            }
        }

        addTagButton.setOnClickListener {
            val tag = tagSearchInput.text.toString().trim().toLowerCase()
            if (tag.isNotEmpty()) {
                if (selectedTopTags.contains(tag)) {
                    Toast.makeText(this, "You can already choose this tag", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (selectedTags.size + selectedTopTags.size >= 4) {
                        Toast.makeText(this, "You can select up to 4 tags only", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val isNewTag = selectedTags.add(tag)
                        if (isNewTag) {
                            tagSearchInput.text.clear()
                            displaySelectedTags()
                        } else {
                            Toast.makeText(this, "Tag '$tag' already added", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

        sortButton.setOnClickListener {
            if (sortOptionsContainer.visibility == View.GONE) {
                sortOptionsContainer.visibility = View.VISIBLE
            } else {
                sortOptionsContainer.visibility = View.GONE
            }
        }

        sortAscCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sortDescCheckBox.isChecked = false
                selectedSortOption = "asc"
            }
        }

        sortDescCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sortAscCheckBox.isChecked = false
                selectedSortOption = "desc"
            }
        }

        applyFiltersButton.setOnClickListener {
            applyFilters()
        }

        if (user._id.isEmpty()) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
            return
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            val footerFragment = FooterFragment.newInstance("search")
            supportFragmentManager.beginTransaction()
                .replace(R.id.header_container, HeaderFragment())
                .replace(R.id.footer_container, footerFragment)
                .commit()
        }

        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val view = scrollView.getChildAt(scrollView.childCount - 1)
            val diff = (view.bottom - (scrollView.height + scrollView.scrollY))

            if (diff <= 0 && !isLoading) {
                currentPage++
                fetchPostsAndDisplay(currentPage)
            }
        }

        if (intent.getBooleanExtra("UPDATE_POSTS", false)) {
            refreshPosts()
            intent.removeExtra("UPDATE_POSTS")
        }

        fetchTopTags()
    }

    private fun fetchUserReactions() {
        GlobalScope.launch(Dispatchers.Main) {
            userReactions = fetchUserReactionsInternal() ?: UserReactions(emptyList(), emptyList())
        }
    }

    private suspend fun fetchUserReactionsInternal(): UserReactions? {
        return try {
            withContext(Dispatchers.IO) {
                val userId = encryptedPreferencesManager.getUserData()._id
                val response =
                    RetrofitClient.getInstance(applicationContext).userAPI.getUserReactions(userId)
                        .execute()
                if (response.isSuccessful) response.body() else null
            }
        } catch (e: Exception) {
            Log.e("SearchActivity", "Error fetching user reactions: ${e.message}", e)
            null
        }
    }

    override fun onResume() {
        super.onResume()
        currentPage = 1
        fetchPostsAndDisplay(currentPage)
        refreshPosts()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CREATE_POST && resultCode == RESULT_OK) {
            val updatePosts = data?.getBooleanExtra("UPDATE_POSTS", false) ?: false
            if (updatePosts) {
                refreshPosts()
            }
        }
    }

    fun refreshPosts() {
        currentPage = 1
        isLoading = false
        val fragmentManager = supportFragmentManager
        val fragments = fragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is PostFragment) {
                fragmentManager.beginTransaction().remove(fragment).commit()
            }
        }
        existingPostsIds.clear()

        findViewById<LinearLayout>(R.id.posts_container).removeAllViews()

        fetchPostsAndDisplay(currentPage)
    }

    private fun fetchPostsAndDisplay(page: Int) {
        if (page == 1) {
            findViewById<LinearLayout>(R.id.posts_container).removeAllViews()
        }
        isLoading = true
        GlobalScope.launch(Dispatchers.Main) {
            try {
                if (!this@SearchActivity::userReactions.isInitialized) {
                    userReactions =
                        fetchUserReactionsInternal() ?: UserReactions(emptyList(), emptyList())
                }

                val query = searchInput.text.toString()
                val tags = mutableListOf<String>()
                tags.addAll(selectedTags)
                tags.addAll(selectedTopTags)
                val sort = selectedSortOption

                if (tags.size > 4) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@SearchActivity,
                            "You can select up to 4 tags only",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    isLoading = false
                    return@launch
                }

                val postResponse = withContext(Dispatchers.IO) {
                    RetrofitClient.getInstance(applicationContext).postAPI.searchPosts(
                        query = query.takeIf { it.isNotEmpty() },
                        tags = if (tags.isNotEmpty()) tags else null,
                        sort = sort,
                        page = page,
                        limit = 10
                    )
                }
                val posts = postResponse

                if (posts.isNotEmpty()) {
                    displayPosts(posts, userReactions)
                }
                isLoading = false
            } catch (e: Exception) {
                Log.e("SearchActivity", "Error fetching posts: ${e.message}", e)
                isLoading = false
            }
        }
    }

    private fun fetchTopTags() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val topTags = RetrofitClient.getInstance(this@SearchActivity)
                    .postAPI.getTopTags(limit = 5)
                withContext(Dispatchers.Main) {
                    displayTopTags(topTags)
                }
            } catch (e: Exception) {
                Log.e("SearchActivity", "Error fetching top tags: ${e.message}", e)
            }
        }
    }

    private fun displayTopTags(tags: List<String>) {
        topTagsContainer.removeAllViews()
        for (tag in tags) {
            val checkBox = CheckBox(this).apply {
                text = tag
                textSize = 20f
                setTextColor(resources.getColor(R.color.white, null))
                buttonTintList = resources.getColorStateList(R.color.accent, null)
                setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        if (selectedTopTags.size + selectedTags.size >= 4) {
                            Toast.makeText(
                                this@SearchActivity,
                                "You can select up to 4 tags only",
                                Toast.LENGTH_SHORT
                            ).show()
                            buttonView.setChecked(false)
                        } else {
                            selectedTopTags.add(tag)
                        }
                    } else {
                        selectedTopTags.remove(tag)
                    }
                }
            }
            topTagsContainer.addView(checkBox)
        }
    }

    private fun displaySelectedTags() {
        selectedTagsContainer.removeAllViews()
        for (tag in selectedTags) {
            val checkBox = CheckBox(this).apply {
                text = tag
                textSize = 20f
                setTextColor(resources.getColor(R.color.white, null))
                buttonTintList = resources.getColorStateList(R.color.accent, null)
                isChecked = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (!isChecked) {
                        selectedTags.remove(tag)
                        displaySelectedTags()
                    }
                }
            }
            selectedTagsContainer.addView(checkBox)
        }
    }

    private fun applyFilters() {
        currentPage = 1
        existingPostsIds.clear()
        postsContainer.removeAllViews()
        filterSection.visibility = View.GONE
        sortOptionsContainer.visibility = View.GONE
        performSearch()
    }

    private fun performSearch() {
        val query = searchInput.text.toString()
        isLoading = true
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val reactions = fetchUserReactionsInternal()
                val tags = mutableListOf<String>()
                tags.addAll(selectedTags)
                tags.addAll(selectedTopTags)
                val posts = withContext(Dispatchers.IO) {
                    RetrofitClient.getInstance(this@SearchActivity).postAPI.searchPosts(
                        query = if (query.isNotEmpty()) query else null,
                        tags = if (tags.isNotEmpty()) tags else null,
                        sort = selectedSortOption,
                        page = currentPage,
                        limit = 10
                    )
                }
                displayPosts(posts, reactions)
            } catch (e: Exception) {
                Toast.makeText(this@SearchActivity, "No results", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    private fun displayPosts(posts: List<Post>, reactions: UserReactions?) {
        val fragmentManager = supportFragmentManager
        postsContainer.removeAllViews()

        for (post in posts) {
            val postFragment = PostFragment.newInstance(post, reactions)
            fragmentManager.beginTransaction()
                .add(postsContainer.id, postFragment)
                .commit()
        }
    }
}