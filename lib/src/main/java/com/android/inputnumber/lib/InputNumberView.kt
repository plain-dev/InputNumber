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
            field = value
            initState()
        }

    /**
     * Set the maximum value that can be entered, default: [Int.MAX_VALUE]
     */
    var maximum: Int = Int.MAX_VALUE
        set(value) {
            field = value
            initState()
        }

    /**
     * Set the step size for each input, default: `1`
     */
    var step: Int = 1

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

    init {
        orientation = HORIZONTAL
        gravity = Gravity.END
        inflate(context, R.layout.layout_input_number, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        showNumberChangeState()
        setClickListener()
    }

    private fun initState() {
        when (number) {
            minimum -> changeState(NUMBER_STATE_MIN)
            maximum -> changeState(NUMBER_STATE_MAX)
            else -> changeState(NUMBER_STATE_NORMAL)
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
                onResult = {
                    updateCallBack(it)
                }
            }.show(fm, "CAEDialog-${UUID.randomUUID()}")
        }
    }

    private fun checkUpdateNumber(number: Int): Boolean {
        return number in minimum..maximum
    }

    /**
     * Calculate the number
     */
    private fun calNumber(thisNumber: Int, isAddition: Boolean, isOverride: Boolean = false) {
        val tempNumber = if (isOverride) { // Override
            thisNumber
        } else {
            if (isAddition) {
                number + thisNumber
            } else {
                number - thisNumber
            }
        }
        if (isOverride) {
            if (tempNumber in 1..maximum) {
                updateCallBack(tempNumber)
            } else {
                "请重新指定商品数量".toast(context)
            }
        } else {
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
     * Restore number to minimum
     */
    fun restoreNumber2Minimum() {
        updateCallBack(minimum)
    }

}