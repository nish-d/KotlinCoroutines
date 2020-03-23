package com.raywenderlich.kotlin.coroutines.utils

import android.util.Log
import okhttp3.internal.waitMillis
import kotlin.coroutines.CoroutineContext

/**
 *Created by nishita.dutta on 2020-03-23, 11:27.
 */
fun logCoroutine(methodName: String, coroutineContext: CoroutineContext){
    Log.d("TestCoroutine", "Current method: $methodName + Current Thread: ${Thread.currentThread().name}, + Context : $coroutineContext" )
}