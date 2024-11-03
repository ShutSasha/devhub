package com.devhub.devhubapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.devhub.devhubapp.R


class PrimaryButtonFragment : Fragment() {

    private lateinit var button: Button
    private var text: String? = null
    private var buttonAction: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_primary_button, container, false)

        button = view.findViewById(R.id.primarybtn)
        button.text = this.text

        button.setOnClickListener {
            buttonAction?.invoke()
        }

        return view
    }

    fun setButtonText(text: String) {
        this.text = text
        if (::button.isInitialized) {
            button.text = text
        }
    }

    fun setButtonAction(action: () -> Unit) {
        this.buttonAction = action
    }
}
