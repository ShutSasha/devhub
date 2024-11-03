package com.devhub.devhubapp.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.devhub.devhubapp.R
import com.devhub.devhubapp.databinding.FragmentInputBinding


class InputFragment : Fragment() {

    private var inputType: Int = InputType.TYPE_CLASS_TEXT
    private var hintText: String? = null

    private lateinit var binding: FragmentInputBinding
    private var listener: InputTextListener? = null

    fun setTextWatcher(listener: InputTextListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputBinding.inflate(inflater, container, false)

        binding.editText.inputType = inputType
        binding.editText.hint = hintText

        if(binding.editText.inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD){
            binding.editText.transformationMethod = PasswordTransformationMethod.getInstance()
        }

        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                listener?.onTextInputChanged(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        return binding.root
    }

    fun setInputType(type: Int) {
        inputType = type
    }

    fun setInputHint(text: String) {
        hintText = text
    }

    fun clearInputText() {
        binding.editText.text.clear()
    }

    fun setInputHintByBinding(text: String) {
        binding.editText.hint = text
    }
}

interface InputTextListener {
    fun onTextInputChanged(text: String)
}