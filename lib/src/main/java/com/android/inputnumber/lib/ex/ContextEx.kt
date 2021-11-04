package com.android.inputnumber.lib.ex

import android.app.Application
import android.app.Service
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

val View.supportFragmentManager: FragmentManager?
    get() {
        return context.findFragmentActivity()?.supportFragmentManager
    }

fun Context?.findFragmentActivity(): FragmentActivity? {
    val context = this ?: return null
    if (context is FragmentActivity) {
        return context
    }
    if (context is Application || context is Service) {
        return null
    }
    var tempContext: Context? = context
    while (tempContext != null) {
        if (tempContext is ContextWrapper) {
            tempContext = tempContext.baseContext
            if (tempContext is FragmentActivity) {
                return tempContext
            }
        } else {
            return null
        }
    }
    return null
}