package com.example.arch.core.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}

class DefaultDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher       = Dispatchers.Main
    override val io: CoroutineDispatcher         = Dispatchers.IO
    override val default: CoroutineDispatcher    = Dispatchers.Default
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}

// Test implementation injected in unit tests
class TestDispatcherProvider(
    private val testDispatcher: CoroutineDispatcher,
) : DispatcherProvider {
    override val main: CoroutineDispatcher       = testDispatcher
    override val io: CoroutineDispatcher         = testDispatcher
    override val default: CoroutineDispatcher    = testDispatcher
    override val unconfined: CoroutineDispatcher = testDispatcher
}
