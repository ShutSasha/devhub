package com.devhub.devhubapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.devhub.devhubapp.R


class TitleFragment : Fragment() {

    private var showBackArrow: Boolean = false
    private var titleText: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_title, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backArrow: ImageButton = view.findViewById(R.id.back_arrow)

        backArrow.visibility = if (showBackArrow) View.VISIBLE else View.GONE

        val title: TextView = view.findViewById(R.id.title)
        title.text = titleText

        backArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    fun setShowBackArrow(show: Boolean) {
        showBackArrow = show
    }

    fun setTitleText(text: String) {
        titleText = text
    }
    
}