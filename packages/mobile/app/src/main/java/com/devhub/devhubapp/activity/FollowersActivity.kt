package com.devhub.devhubapp.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.devhub.devhubapp.R
import com.devhub.devhubapp.databinding.ActivityFollowersBinding
import com.devhub.devhubapp.databinding.ActivityLogInBinding
import com.devhub.devhubapp.fragment.FollowerFragment
import com.devhub.devhubapp.fragment.FooterFragment
import com.devhub.devhubapp.fragment.HeaderFragment
import com.devhub.devhubapp.fragment.UnderlinedTextFragment

class FollowersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFollowersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_followers)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.followersContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fragmentManager = supportFragmentManager
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                .replace(R.id.header_container, HeaderFragment())
                .replace(R.id.footer_container, FooterFragment())
                .commit()
        }

        binding = ActivityFollowersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpFragments()
    }

    private fun setUpFragments(){
        binding.noFollowings.visibility = View.GONE

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val followersFragment = UnderlinedTextFragment()
        val followedFragment = UnderlinedTextFragment()

        followersFragment.apply {
            setText("Followers")
            isUnderlined(true)
            setAction {
                this.isUnderlined(true)
                followedFragment.isUnderlined(false)
                binding.scrollView1.visibility = View.VISIBLE
                binding.scrollView2.visibility = View.GONE
            }
        }
        fragmentTransaction.add(R.id.tvFollowers, followersFragment)

        followedFragment.apply {
            setText("Followed")
            isUnderlined(false)
            setAction {
                this.isUnderlined(true)
                followersFragment.isUnderlined(false)
                binding.scrollView1.visibility = View.GONE
                binding.scrollView2.visibility = View.VISIBLE
            }
        }
        fragmentTransaction.add(R.id.tvFollowed, followedFragment)

        if(followersFragment.isUnderlined){
            binding.scrollView1.visibility = View.VISIBLE
            binding.scrollView2.visibility = View.GONE
        } else {
            binding.scrollView1.visibility = View.GONE
            binding.scrollView2.visibility = View.VISIBLE
        }

        val follower1Fragment = FollowerFragment.newInstance(
            username = "cdidk",
            avatarUrl = "https://mydevhubimagebucket.s3.eu-west-3.amazonaws.com/user_icons/6728c15d0d65067987f66267/13:37:12о-движении-земли.jpg",
            userId = "6728c15d0d65067987f66267"
        )
        follower1Fragment.isFollowed(true)
        fragmentTransaction.add(R.id.follower_container, follower1Fragment)

        val follower2Fragment = FollowerFragment.newInstance(
            username = "guntersteam",
            avatarUrl = "https://mydevhubimagebucket.s3.eu-west-3.amazonaws.com/user_icons/6728c53f8f78ebab7b3a5528/20:45:55main-avatar.jpg",
            userId = "6728c53f8f78ebab7b3a5528"
        )
        follower2Fragment.isFollowed(false)
        fragmentTransaction.add(R.id.followed_container, follower2Fragment)

        fragmentTransaction.commit()
    }
}