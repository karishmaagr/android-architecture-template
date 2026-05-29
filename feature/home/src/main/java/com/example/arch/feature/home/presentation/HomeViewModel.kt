package com.example.arch.feature.home.presentation

import androidx.lifecycle.viewModelScope
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

    init {
        onIntent(HomeIntent.LoadPosts)
    }

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
                    is Result.Loading -> setState { copy(isLoading = true, error = null) }
                    is Result.Success -> setState { copy(isLoading = false, posts = result.data) }
                    is Result.Error   -> setState {
                        copy(isLoading = false, error = result.message ?: "Failed to load posts")
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun refreshPosts() {
        viewModelScope.launch {
            setState { copy(isRefreshing = true) }
            when (val result = refreshPostsUseCase()) {
                is Result.Success -> setState { copy(isRefreshing = false) }
                is Result.Error   -> {
                    setState { copy(isRefreshing = false) }
                    sendEffect(HomeEffect.ShowSnackbar(result.message ?: "Refresh failed"))
                }
                is Result.Loading -> Unit
            }
        }
    }
}
