@file:Suppress("unused", "UNCHECKED_CAST")

package com.android.inputnumber.lib

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import com.android.inputnumber.lib.callback.OnInputNumberCallback
import com.android.inputnumber.lib.dialog.InputNumberEditDialog
import com.android.inputnumber.lib.ex.supportFragmentManager
import com.android.inputnumber.lib.ex.toast
import kotlinx.android.synthetic.main.layout_input_number.view.*
import java.util.*

/**
 * InputNumber
 *
 * > Input numerical values with a customizable range.
 */
class InputNumberView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        const val NUMBER_STATE_NORMAL = 0x901
        const val NUMBER_STATE_MIN = 0x902
        const val NUMBER_STATE_MAX = 0x903
    }

    var onInputNumberCallback: OnInputNumberCallback? = null

    /**
     * Set the minimum value that can be entered, default: `1`
     */
    var minimum: Int = 1
        set(value) {
            if (value >= maximum) return
            field = value
            initState()
        }

    /**
     * Set the maximum value that can be entered, default: [Int.MAX_VALUE]
     */
    var maximum: Int = Int.MAX_VALUE
        set(value) {
            if (value <= minimum) return
            field = value
            initState()
        }

    /**
     * Set the step size for each input, default: `1`
     */
    var step: Int = 1
        set(value) {
            if (step < 1) return
            field = value
        }

    /**
     * Set current number, default: `1`
     */
    var number: Int = 1
        set(value) {
            if (checkUpdateNumber(value)) {
                field = value
                showNumberChangeState()
            }
        }

    /**
     * Set whether to allow editing input
     */
    var isEdit: Boolean = false
        set(value) {
            field = value
            changeEditClick(value)
        }

    /**
     * Set input on or off
     */
    var isEnabledInput: Boolean = true
        set(value) {
            field = value
            changeEnableInput(value)
        }

    init {
        orientation = HORIZONTAL
        /*
         * - 对齐方式中必须存在 `Gravity.CENTER_VERTICAL`
         * - `Gravity.CENTER_VERTICAL` must be present in the alignment
         * - 否则在某些情况下 (e.g. `DialogFragment` 重新执行显示动画，但控件父布局中未设置此对齐方式) 会出现控件错位的问题
         * - Otherwise, in some cases (e.g. `DialogFragment` re-executes the display animation, but this alignment is not set in the control parent layout), there will be a control misalignment problem
         * - 和在 XML 设置 `android:gravity="end|center_vertical"` 等价
         * - Equivalent to setting `android:gravity="end|center_vertical"` in XML
         */
        gravity = Gravity.END.or(Gravity.CENTER_VERTICAL)
        inflate(context, R.layout.layout_input_number, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        showNumberChangeState()
        setClickListener()
    }

    private fun initState() {
        if (isEnabledInput) {
            when (number) {
                minimum -> changeState(NUMBER_STATE_MIN)
                maximum -> changeState(NUMBER_STATE_MAX)
                else -> changeState(NUMBER_STATE_NORMAL)
            }
        }
    }

    private fun setClickListener() {
        btnNumberLeft?.setOnClickListener {
            calNumber(step, false)  // Reduce
        }
        btnNumberRight?.setOnClickListener {
            calNumber(step, true) // Plus
        }
        changeEditClick(isEdit)
    }

    private fun changeEditClick(isEdit: Boolean) {
        if (isEdit) {
            btnNumberCenter?.setOnClickListener {
                showEditDialog()
            }
        } else {
            btnNumberCenter?.isClickable = false
            btnNumberCenter?.setOnClickListener(null)
        }
    }

    private fun showEditDialog() {
        supportFragmentManager?.let { fm ->
            InputNumberEditDialog.newInstance(
                minimum = minimum,
                maximum = maximum
            ).apply {
                onResult = { // Double check
                    if (checkUpdateNumber(it)) updateCallBack(it)
                }
            }.show(fm, "INEDialog-${UUID.randomUUID()}")
        }
    }

    /**
     * Check if the number is within the maximum and minimum range
     */
    private fun checkUpdateNumber(number: Int): Boolean {
        return number in minimum..maximum
    }

    /**
     * Calculate the number
     */
    private fun calNumber(thisNumber: Int, isAddition: Boolean) {
        val tempNumber = if (isAddition) {
            number + thisNumber
        } else {
            number - thisNumber
        }
        if (isAddition) {
            if (tempNumber > maximum) {  // Maybe a callback can be better
                "无法加入更多".toast(context)
                changeState(NUMBER_STATE_MAX)
            } else {
                updateCallBack(tempNumber)
            }
        } else {
            if (tempNumber < minimum) {  // Maybe a callback can be better
                "数量不能在减少了".toast(context)
                changeState(NUMBER_STATE_MIN)
            } else {
                updateCallBack(tempNumber)
            }
        }
    }

    /**
     * Update button state
     */
    private fun changeState(state: Int) {
        when (state) {
            NUMBER_STATE_MAX -> {
                btnNumberLeft?.isEnabled = true
                btnNumberRight?.isEnabled = false
            }
            NUMBER_STATE_MIN -> {
                btnNumberLeft?.isEnabled = false
                btnNumberRight?.isEnabled = true
            }
            NUMBER_STATE_NORMAL -> {
                btnNumberLeft?.isEnabled = true
                btnNumberRight?.isEnabled = true
            }
        }
    }

    /**
     * Update number and call back
     */
    private fun updateCallBack(newNumber: Int) {
        val updateBlock = {
            update(newNumber)
        }

        val failedBlock = {
            // Update number failed
        }

        onInputNumberCallback?.onUpdateNeedConfirmation(
            view = this,
            number = newNumber,
            confirmBlock = updateBlock,
            failedBlock = failedBlock
        ) ?: updateBlock()
    }

    /**
     * The calculation and check pass results are updated by this method,
     * If the check fails or other conditions are not met, do not call this method
     */
    private fun update(newNumber: Int) {
        if (checkUpdateNumber(newNumber)) {
            number = newNumber
            showNumberChangeState()
        }
    }

    /**
     * Display quantity and update status
     */
    private fun showNumberChangeState() {
        showNumber()
        initState()
    }

    /**
     * Display number to UI
     */
    private fun showNumber() {
        btnNumberCenter?.text = "$number"
    }

    /**
     * Turn input on or off
     */
    private fun changeEnableInput(isEnabled: Boolean) {
        btnNumberLeft?.isEnabled = isEnabled
        btnNumberCenter?.isEnabled = isEnabled
        btnNumberRight?.isEnabled = isEnabled
        initState()
    }

    /**
     * Restore number to minimum
     */
    fun restoreNumber2Minimum() {
        updateCallBack(minimum)
    }

}