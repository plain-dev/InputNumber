package com.android.inputnumber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.inputnumber.lib.InputNumberView
import com.android.inputnumber.lib.callback.OnInputNumberCallback

class MainActivity : AppCompatActivity() {

    private lateinit var inputNumberView: InputNumberView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inputNumberView = findViewById(R.id.inputNumberView)

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
            number = 11
            minimum = 2
            maximum = 9999
            step = 10
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