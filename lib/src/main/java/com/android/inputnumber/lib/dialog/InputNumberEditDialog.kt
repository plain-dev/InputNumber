package com.android.inputnumber.lib.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.android.inputnumber.lib.R
import com.android.inputnumber.lib.ex.delayedShowSoftKeyboard
import com.android.inputnumber.lib.ex.toast
import kotlinx.android.synthetic.main.dialog_input_number_edit.*

internal class InputNumberEditDialog : DialogFragment() {

    companion object {

        /**
         * - [minimum] Enter the minimum value
         * - [maximum] Enter the maximum value
         * - Default `-1` no limit
         */
        fun newInstance(minimum: Int = -1, maximum: Int = -1) = InputNumberEditDialog().apply {
            arguments = Bundle().apply {
                putInt("minimum", minimum)
                putInt("maximum", maximum)
            }
        }
    }

    private val minimum by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getInt("minimum") ?: -1
    }

    private val maximum by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getInt("maximum") ?: -1
    }

    var onResult: ((count: Int) -> Unit)? = null

    private var currentInputCount = -1

    private var completeShowPopupSoftKeyboard = false

    override fun onStart() {
        super.onStart()
        setDialogUI()
    }

    private fun setDialogUI() {
        dialog?.window?.apply {
            attributes = attributes.apply {
                gravity = Gravity.BOTTOM // Always above the soft keyboard
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            decorView.setPadding(0, 0, 0, 0)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_input_number_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.decorView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { _, insets ->
                val isShowIME = insets.isVisible(WindowInsetsCompat.Type.ime())
                if (completeShowPopupSoftKeyboard && !isShowIME) { // Close the dialog while closing the soft keyboard
                    dismissAllowingStateLoss()
                }
                insets
            }
        }
        etCount?.apply {
            delayedShowSoftKeyboard(context, 500) {
                completeShowPopupSoftKeyboard = true
            }
            addTextChangedListener(onTextChanged = { text, _, _, _ ->
                currentInputCount = text?.toString()?.toIntOrNull() ?: -1
            })
            setImeActionLabel( // Depends on the support of the soft keyboard
                resources.getString(R.string.input_confirm),
                EditorInfo.IME_ACTION_DONE
            )
            setOnEditorActionListener { _, a, _ ->
                if (a == EditorInfo.IME_ACTION_DONE) doCallback()
                true
            }
        }
        btnConfirm?.setOnClickListener { doCallback() }
    }

    private fun doCallback() {
        if (currentInputCount > 0) {
            if (minimum != -1 && currentInputCount < minimum) {
                "Minimum input $minimum".toast(requireContext())
                return
            }
            if (maximum != -1 && currentInputCount > maximum) {
                "Maximum input $maximum".toast(requireContext())
                return
            }
            onResult?.invoke(currentInputCount)
            dismissAllowingStateLoss()
        } else {
            "Please check the input".toast(requireContext())
        }
    }

}