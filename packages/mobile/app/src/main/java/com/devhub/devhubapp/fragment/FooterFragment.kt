package com.devhub.devhubapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.devhub.devhubapp.R
import com.devhub.devhubapp.activity.FollowersActivity
import com.devhub.devhubapp.activity.MainActivity

class FooterFragment : Fragment() {

    companion object {
        private const val ARG_SELECTED_ICON = "selected_icon"

        fun newInstance(selectedIcon: String): FooterFragment {
            val fragment = FooterFragment()
            val args = Bundle()
            args.putString(ARG_SELECTED_ICON, selectedIcon)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_footer, container, false)

        val homeIcon: ImageView = view.findViewById(R.id.home_icon)
        val followersIcon: ImageView = view.findViewById(R.id.group_icon)

        val selectedIcon = arguments?.getString(ARG_SELECTED_ICON)

        when (selectedIcon) {
            "home" -> {
                changeIcon(homeIcon, R.drawable.ic_home_active)
                changeIcon(followersIcon, R.drawable.ic_friends)
            }
            "followers" -> {
                changeIcon(homeIcon, R.drawable.ic_home)
                changeIcon(followersIcon, R.drawable.ic_friends_active)
            }
        }

        homeIcon.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        followersIcon.setOnClickListener {
            val intent = Intent(requireContext(), FollowersActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun changeIcon(icon: ImageView, iconResId: Int) {
        icon.setImageResource(iconResId)
    }
}
