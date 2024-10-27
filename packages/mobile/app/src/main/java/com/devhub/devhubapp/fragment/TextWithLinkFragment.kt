package com.devhub.devhubapp.fragment

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.devhub.devhubapp.R

class TextWithLinkFragment : Fragment() {

    private lateinit var textView: TextView
    private lateinit var linkTextView: TextView
    private var text: String? = null
    private var linkText: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_text_with_link, container, false)

        textView = view.findViewById(R.id.text)
        linkTextView = view.findViewById(R.id.linkText)

        if (!text.isNullOrEmpty()) {
            textView.text = text
            textView.visibility = View.VISIBLE
        } else {
            textView.visibility = View.GONE
        }

        if (!linkText.isNullOrEmpty()) {
            linkTextView.text = linkText
            linkTextView.paintFlags = linkTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            linkTextView.visibility = View.VISIBLE
        } else {
            linkTextView.visibility = View.GONE
        }

        return view
    }

    fun setTextAndLinkText(text: String?, linkText: String?) {
        this.text = text
        this.linkText = linkText
    }
}