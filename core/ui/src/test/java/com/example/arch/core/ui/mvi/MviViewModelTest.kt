package com.example.arch.core.ui.mvi

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MviViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    // Minimal concrete ViewModel for testing the base contract
    data class CounterState(val count: Int = 0) : UiState
    sealed interface CounterIntent : UiIntent {
        data object Increment : CounterIntent
        data object Decrement : CounterIntent
    }
    sealed interface CounterEffect : UiEffect {
        data class ShowToast(val msg: String) : CounterEffect
    }

    private inner class CounterViewModel : MviViewModel<CounterState, CounterIntent, CounterEffect>() {
        override fun createInitialState() = CounterState()
        override fun handleIntent(intent: CounterIntent) = when (intent) {
            CounterIntent.Increment -> {
                setState { copy(count = count + 1) }
                if (currentState.count == 5) sendEffect(CounterEffect.ShowToast("Reached 5!"))
            }
            CounterIntent.Decrement -> setState { copy(count = count - 1) }
        }
    }

    @Before fun setUp()    { Dispatchers.setMain(testDispatcher) }
    @After  fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `initial state has count zero`() = runTest {
        val vm = CounterViewModel()
        assertThat(vm.uiState.value.count).isEqualTo(0)
    }

    @Test
    fun `Increment intent increases count`() = runTest {
        val vm = CounterViewModel()
        vm.onIntent(CounterIntent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(vm.uiState.value.count).isEqualTo(1)
    }

    @Test
    fun `effect emitted when count reaches 5`() = runTest {
        val vm = CounterViewModel()
        vm.effect.test {
            repeat(5) { vm.onIntent(CounterIntent.Increment) }
            testDispatcher.scheduler.advanceUntilIdle()
            val effect = awaitItem()
            assertThat(effect).isInstanceOf(CounterEffect.ShowToast::class.java)
        }
    }
}
