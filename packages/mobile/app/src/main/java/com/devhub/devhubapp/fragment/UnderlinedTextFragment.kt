package com.devhub.devhubapp.fragment

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.devhub.devhubapp.R
import com.devhub.devhubapp.databinding.FragmentUnderlinedTextBinding

class UnderlinedTextFragment : Fragment() {

    private lateinit var binding: FragmentUnderlinedTextBinding
    public var isUnderlined: Boolean = false
    private val text = MutableLiveData<String>()
    private var action: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUnderlinedTextBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text.observe(viewLifecycleOwner, Observer { newText ->
            binding.text.text = newText
        })

        binding.text.setOnClickListener {
            action?.invoke()
            updateUnderline()
        }

        updateUnderline()
    }

    fun setText(text: String) {
        this.text.value = text
    }

    fun isUnderlined(underline: Boolean) {
        isUnderlined = underline
        updateUnderline()
    }

    fun setAction(action: () -> Unit) {
        this.action = action
    }

    private fun updateUnderline() {
        if (::binding.isInitialized) {
            if (isUnderlined) {
                binding.text.paintFlags = binding.text.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            } else {
                binding.text.paintFlags = binding.text.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            }
        }
    }
}
