package com.example.arch.feature.home.presentation

import app.cash.turbine.test
import com.example.arch.core.common.coroutines.TestDispatcherProvider
import com.example.arch.core.common.result.Result
import com.example.arch.feature.home.domain.model.Post
import com.example.arch.feature.home.domain.usecase.GetPostsUseCase
import com.example.arch.feature.home.domain.usecase.RefreshPostsUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher   = StandardTestDispatcher()
    private val getPostsUseCase  = mockk<GetPostsUseCase>()
    private val refreshUseCase   = mockk<RefreshPostsUseCase>()

    private val samplePosts = listOf(
        Post(id = "1", title = "Post 1", body = "Body 1", userId = "u1", createdAt = 0L),
        Post(id = "2", title = "Post 2", body = "Body 2", userId = "u1", createdAt = 0L),
    )

    @Before
    fun setUp() { Dispatchers.setMain(testDispatcher) }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `posts are loaded on init and state reflects success`() = runTest(testDispatcher) {
        every { getPostsUseCase(Unit) } returns flowOf(Result.Success(samplePosts))
        val viewModel = HomeViewModel(getPostsUseCase, refreshUseCase)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.posts).hasSize(2)
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
    }

    @Test
    fun `error from use case sets error state`() = runTest(testDispatcher) {
        every { getPostsUseCase(Unit) } returns flowOf(
            Result.Error(RuntimeException("Network error"), "Network error"),
        )
        val viewModel = HomeViewModel(getPostsUseCase, refreshUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.error).isNotNull()
        assertThat(viewModel.uiState.value.posts).isEmpty()
    }

    @Test
    fun `PostClicked emits NavigateToPostDetail effect`() = runTest(testDispatcher) {
        every { getPostsUseCase(Unit) } returns flowOf(Result.Success(samplePosts))
        val viewModel = HomeViewModel(getPostsUseCase, refreshUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onIntent(HomeIntent.PostClicked(samplePosts.first()))
            val effect = awaitItem()
            assertThat(effect).isInstanceOf(HomeEffect.NavigateToPostDetail::class.java)
            assertThat((effect as HomeEffect.NavigateToPostDetail).postId).isEqualTo("1")
        }
    }
}
