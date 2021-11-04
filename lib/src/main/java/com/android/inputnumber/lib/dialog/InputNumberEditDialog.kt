package com.android.inputnumber.lib.dialog

import android.os.Bundle
import androidx.fragment.app.DialogFragment

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

}