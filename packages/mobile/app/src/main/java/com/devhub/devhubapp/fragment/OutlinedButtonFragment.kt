package com.devhub.devhubapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.devhub.devhubapp.databinding.FragmentOutlinedButtonBinding

class OutlinedButtonFragment : Fragment() {

    private lateinit var button: Button
    private var text: String? = null
    private var buttonAction: (() -> Unit)? = null
    private lateinit var binding: FragmentOutlinedButtonBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOutlinedButtonBinding.inflate(inflater, container, false)

        button = binding.outlinedbtn
        button.text = this.text

        button.setOnClickListener {
            buttonAction?.invoke()
        }

        return binding.root
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

