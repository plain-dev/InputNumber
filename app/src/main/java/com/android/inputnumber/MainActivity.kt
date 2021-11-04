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

    private fun initInputNumberView(inputNumberView: InputNumberView) {
        with(inputNumberView) {
            number = 10
            minimum  = 2
            maximum = 20
            step = 2
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