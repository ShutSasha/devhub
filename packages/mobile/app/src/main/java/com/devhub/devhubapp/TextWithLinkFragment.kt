package com.devhub.devhubapp

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


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

        textView.text = text

        linkTextView.text = linkText
        linkTextView.paintFlags = linkTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        return view
    }

    fun setTextAndLinkText(text: String, linkText: String) {
        this.text = text
        this.linkText = linkText
    }
}
