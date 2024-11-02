package com.devhub.devhubapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.devhub.devhubapp.R
import com.devhub.devhubapp.databinding.FragmentTitleBinding


class TitleFragment : Fragment() {

    private var showBackArrow: Boolean = false
    private var titleText: String? = null

    private lateinit var binding: FragmentTitleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTitleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backArrow.visibility = if (showBackArrow) View.VISIBLE else View.GONE
        binding.title.text = titleText

        binding.backArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    fun setShowBackArrow(show: Boolean) {
        showBackArrow = show
    }

    fun setTitleText(text: String) {
        titleText = text
    }

    fun setTextColour(color: Int) {
        binding.title.setTextColor(color)
    }
    
}