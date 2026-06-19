package com.example.arch.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.arch.core.ui.component.EmptyView
import com.example.arch.core.ui.component.ErrorView
import com.example.arch.core.ui.component.LoadingView
import com.example.arch.feature.home.presentation.components.PostItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onSessionExpired: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToProfile    -> onNavigateToProfile()
                is HomeEffect.NavigateToSettings   -> onNavigateToSettings()
                is HomeEffect.SessionExpired       -> onSessionExpired()
                is HomeEffect.NavigateToPostDetail -> { /* TODO: implement post detail */ }
                is HomeEffect.ShowSnackbar         -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    IconButton(onClick = { viewModel.onIntent(HomeIntent.NavigateToProfile) }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                    IconButton(onClick = { viewModel.onIntent(HomeIntent.NavigateToSettings) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isOffline && state.posts.isNotEmpty()) {
                OfflineBanner()
            }

            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh    = { viewModel.onIntent(HomeIntent.RefreshPosts) },
                modifier     = Modifier.fillMaxSize(),
            ) {
                when {
                    state.isLoading ->
                        LoadingView()

                    state.isOffline && state.posts.isEmpty() ->
                        ErrorView(
                            message = "You're offline and there's no cached data.\nConnect and pull to refresh.",
                            onRetry = { viewModel.onIntent(HomeIntent.RefreshPosts) },
                        )

                    state.error != null ->
                        ErrorView(
                            message = state.error!!,
                            onRetry = { viewModel.onIntent(HomeIntent.LoadPosts) },
                        )

                    state.posts.isEmpty() ->
                        EmptyView()

                    else ->
                        LazyColumn(
                            contentPadding      = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(state.posts, key = { it.id }) { post ->
                                PostItem(
                                    post    = post,
                                    onClick = { viewModel.onIntent(HomeIntent.PostClicked(it)) },
                                )
                            }
                        }
                }
            }
        }
    }
}

@Composable
private fun OfflineBanner() {
    Surface(
        color    = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier              = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text  = "You're offline — showing cached data",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
    }
}
