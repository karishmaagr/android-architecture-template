package com.example.arch.feature.home.presentation

import androidx.lifecycle.viewModelScope
import com.example.arch.core.common.result.ApiException
import com.example.arch.core.common.result.Result
import com.example.arch.core.ui.mvi.MviViewModel
import com.example.arch.feature.home.domain.usecase.GetPostsUseCase
import com.example.arch.feature.home.domain.usecase.RefreshPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase,
    private val refreshPostsUseCase: RefreshPostsUseCase,
) : MviViewModel<HomeState, HomeIntent, HomeEffect>() {

    override fun createInitialState() = HomeState()

    init { onIntent(HomeIntent.LoadPosts) }

    override fun handleIntent(intent: HomeIntent) = when (intent) {
        is HomeIntent.LoadPosts          -> loadPosts()
        is HomeIntent.RefreshPosts       -> refreshPosts()
        is HomeIntent.PostClicked        -> sendEffect(HomeEffect.NavigateToPostDetail(intent.post.id))
        is HomeIntent.NavigateToProfile  -> sendEffect(HomeEffect.NavigateToProfile)
        is HomeIntent.NavigateToSettings -> sendEffect(HomeEffect.NavigateToSettings)
    }

    private fun loadPosts() {
        getPostsUseCase(Unit)
            .onEach { result ->
                when (result) {
                    is Result.Loading -> setState { copy(isLoading = true, error = null, isOffline = false) }
                    is Result.Success -> setState { copy(isLoading = false, posts = result.data, error = null, isOffline = false) }
                    is Result.Error   -> handleLoadError(result.exception)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun refreshPosts() {
        viewModelScope.launch {
            setState { copy(isRefreshing = true) }
            val result = refreshPostsUseCase()
            when (result) {
                is Result.Success -> setState { copy(isRefreshing = false, isOffline = false) }
                is Result.Error   -> handleRefreshError(result.exception)
                is Result.Loading -> Unit
            }
        }
    }

    private fun handleLoadError(exception: Throwable) {
        when (exception) {
            is ApiException.Unauthorized -> sendEffect(HomeEffect.SessionExpired)
            is ApiException.NetworkError -> setState { copy(isLoading = false, isOffline = true, error = null) }
            is ApiException.ServerError  -> setState { copy(isLoading = false, error = "Server error. Pull down to refresh.") }
            else                         -> setState { copy(isLoading = false, error = exception.message ?: "Failed to load posts.") }
        }
    }

    private fun handleRefreshError(exception: Throwable) {
        setState { copy(isRefreshing = false) }
        when (exception) {
            is ApiException.Unauthorized    -> sendEffect(HomeEffect.SessionExpired)
            is ApiException.NetworkError    -> sendEffect(HomeEffect.ShowSnackbar("No internet connection. Showing cached posts."))
            is ApiException.ServerError     -> sendEffect(HomeEffect.ShowSnackbar("Server error. Please try again."))
            is ApiException.TooManyRequests -> sendEffect(HomeEffect.ShowSnackbar("Too many requests. Please wait before refreshing."))
            else                            -> sendEffect(HomeEffect.ShowSnackbar(exception.message ?: "Refresh failed."))
        }
    }
}
