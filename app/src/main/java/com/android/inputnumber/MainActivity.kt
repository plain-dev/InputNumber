package com.android.inputnumber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.inputnumber.lib.InputNumberView
import com.android.inputnumber.lib.callback.OnInputNumberCallback
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {

    private lateinit var inputNumberView: InputNumberView
    private lateinit var switchEnable: SwitchMaterial
    private lateinit var slideNumber: Slider
    private lateinit var slideMax: Slider
    private lateinit var slideMin: Slider
    private lateinit var slideStep: Slider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inputNumberView = findViewById(R.id.inputNumberView)
        switchEnable = findViewById(R.id.switchEnable)
        slideNumber = findViewById(R.id.slideNumber)
        slideMax = findViewById(R.id.slideMax)
        slideMin = findViewById(R.id.slideMin)
        slideStep = findViewById(R.id.slideStep)

        switchEnable.setOnCheckedChangeListener { _, isChecked ->
            inputNumberView.isEnabledInput = isChecked
        }

        slideNumber.addOnChangeListener { slider, value, fromUser ->
            inputNumberView.number = value.toInt()
        }

        slideMax.addOnChangeListener { slider, value, fromUser ->
            inputNumberView.maximum = value.toInt()
        }

        slideMin.addOnChangeListener { slider, value, fromUser ->
            inputNumberView.minimum = value.toInt()
        }

        slideStep.addOnChangeListener { slider, value, fromUser ->
            inputNumberView.step = value.toInt()
        }

        initInputNumberView(inputNumberView)
    }

    /**
     * Verify the predicted minimum
     *
     * number = 11
     * minimum  = 2
     * maximum = 9999
     * step = 10
     *
     * Verify the predicted maximum
     *
     * number = 9990
     * minimum  = 2
     * maximum = 9999
     * step = 10
     */
    private fun initInputNumberView(inputNumberView: InputNumberView) {
        with(inputNumberView) {
            isEdit = true
            number = 0
            minimum = 0
            maximum = 99
            step = 100
            onInputNumberCallback = object : OnInputNumberCallback {
                override fun onUpdateNeedConfirmation(
                    view: InputNumberView,
                    number: Int,
                    confirmBlock: () -> Unit,
                    failedBlock: () -> Unit,
                    obj: Any?
                ) {
                    confirmBlock()
                }
            }
        }
    }

}