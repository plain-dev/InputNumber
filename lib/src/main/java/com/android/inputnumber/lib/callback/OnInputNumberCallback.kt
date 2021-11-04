package com.android.inputnumber.lib.callback

import com.android.inputnumber.lib.InputNumberView

interface OnInputNumberCallback {

    /**
     * Number of callbacks (requires confirmation to take effect)
     */
    fun onUpdateNeedConfirmation(
        view: InputNumberView,
        number: Int,
        confirmBlock: () -> Unit,
        failedBlock: () -> Unit,
        obj: Any? = null
    ) {
        confirmBlock()
    }

}