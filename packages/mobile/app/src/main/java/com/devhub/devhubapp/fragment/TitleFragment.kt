package com.devhub.devhubapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.devhub.devhubapp.R
import com.devhub.devhubapp.databinding.FragmentTitleBinding

class TitleFragment : Fragment() {

    private var showBackArrow: Boolean = false
    private val titleText = MutableLiveData<String>()

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

        titleText.observe(viewLifecycleOwner, Observer { newText ->
            binding.title.text = newText
        })

        binding.backArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    fun setShowBackArrow(show: Boolean) {
        showBackArrow = show
    }

    fun setTitleText(text: String) {
        titleText.value = text
    }

    fun setTextColour(color: Int) {
        binding.title.setTextColor(color)
    }
}
