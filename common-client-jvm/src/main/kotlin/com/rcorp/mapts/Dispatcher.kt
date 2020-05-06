package com.rcorp.mapts

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val ApplicationDispatcher: CoroutineDispatcher = Dispatchers.IO

internal actual val UIDispatcher: CoroutineDispatcher = Dispatchers.Main