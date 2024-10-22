package com.devhub.devhubapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class PrimaryButtonFragment : Fragment() {

    private lateinit var button: Button
    private var text: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_primary_button, container, false)

        button = view.findViewById(R.id.primarybtn)
        button.text = this.text

        return view
    }

    fun setButtonText(text: String) {
        this.text = text
    }
}
