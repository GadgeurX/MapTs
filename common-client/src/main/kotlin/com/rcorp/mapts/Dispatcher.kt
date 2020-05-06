package com.rcorp.mapts

import kotlinx.coroutines.CoroutineDispatcher

internal expect val ApplicationDispatcher: CoroutineDispatcher

internal expect val UIDispatcher: CoroutineDispatcher