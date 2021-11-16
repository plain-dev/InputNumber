package com.android.inputnumber.lib.ex

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun EditText?.delayedShowSoftKeyboard(
    context: Context,
    delayMillis: Long = 500,
    doneBlock: (() -> Unit)? = null
) {
    this?.apply {
        runOnUIThreadDelayed(context, {
            requestFocus()
            (this as? EditText)?.moveCursorPosition()
            showDialogSoftInput(context)
            doneBlock?.invoke()
        }, delayMillis)
    }
}

/**
 * Move the [EditText] cursor to the specified position [position] at the end of the default
 */
fun EditText?.moveCursorPosition(position: Int = this?.text?.length ?: 0) {
    this?.apply { if (position <= text.length) setSelection(position) }
}

fun EditText?.showDialogSoftInput(context: Context) {
    if (this != null) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun EditText?.closeDialogSoftInput(context: Context) {
    if (this != null) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }
}