package com.example.arch.feature.home.presentation

import com.example.arch.core.ui.mvi.UiEffect
import com.example.arch.core.ui.mvi.UiIntent
import com.example.arch.core.ui.mvi.UiState
import com.example.arch.feature.home.domain.model.Post

data class HomeState(
    val posts: List<Post>     = emptyList(),
    val isLoading: Boolean    = false,
    val isRefreshing: Boolean = false,
    val error: String?        = null,
) : UiState

sealed interface HomeIntent : UiIntent {
    data object LoadPosts         : HomeIntent
    data object RefreshPosts      : HomeIntent
    data class PostClicked(val post: Post) : HomeIntent
    data object NavigateToProfile : HomeIntent
    data object NavigateToSettings: HomeIntent
}

sealed interface HomeEffect : UiEffect {
    data class NavigateToPostDetail(val postId: String) : HomeEffect
    data object NavigateToProfile                       : HomeEffect
    data object NavigateToSettings                      : HomeEffect
    data class ShowSnackbar(val message: String)        : HomeEffect
}
