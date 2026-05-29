package com.example.arch.core.ui.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// S = UI State, I = User Intent/Event, E = One-shot Side Effect
abstract class MviViewModel<S : UiState, I : UiIntent, E : UiEffect> : ViewModel() {

    private val initialState: S by lazy { createInitialState() }

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _effect = Channel<E>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    val currentState: S get() = _uiState.value

    abstract fun createInitialState(): S

    fun onIntent(intent: I) { handleIntent(intent) }

    protected abstract fun handleIntent(intent: I)

    protected fun setState(reduce: S.() -> S) { _uiState.update { it.reduce() } }

    protected fun sendEffect(effect: E) {
        viewModelScope.launch { _effect.send(effect) }
    }
}

interface UiState
interface UiIntent
interface UiEffect
