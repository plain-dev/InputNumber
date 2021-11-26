package com.android.inputnumber.lib.ex

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.lang.ref.WeakReference

private val HANDLER = Handler(Looper.getMainLooper())

@JvmOverloads
fun runOnUIThread(context: Context? = null, runnable: Runnable) {
    addLifecycleObserver(context)
    if (Looper.myLooper() == Looper.getMainLooper()) {
        runnable.run()
    } else {
        HANDLER.post(runnable)
    }
}

fun runOnUIThreadDelayed(context: Context? = null, runnable: Runnable, delayMillis: Long) {
    addLifecycleObserver(context)
    HANDLER.postDelayed(runnable, delayMillis)
}

private fun addLifecycleObserver(context: Context?) {
    context.findFragmentActivity()?.let {
        it.lifecycle.addObserver(UIThreadLifecycleObserver(it))
    }
}

/**
 * Lifecycle Observer
 */
private class UIThreadLifecycleObserver(activity: FragmentActivity?) : LifecycleObserver {

    private var weakReferenceActivity: WeakReference<FragmentActivity?> = WeakReference(activity)

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        HANDLER.removeCallbacksAndMessages(null)
        weakReferenceActivity.apply {
            get()?.lifecycle?.removeObserver(this@UIThreadLifecycleObserver)
            clear()
        }
    }

}