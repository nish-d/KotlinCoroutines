package com.raywenderlich.kotlin.coroutines.contextProvider

import kotlin.coroutines.CoroutineContext

/**
 *Created by nishita.dutta on 2020-03-23, 10:37.
 */
interface CoroutineContextProvider {

    fun context(): CoroutineContext
}