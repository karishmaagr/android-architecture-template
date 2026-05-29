package com.example.arch.feature.home.domain.usecase

import com.example.arch.core.common.coroutines.DispatcherProvider
import com.example.arch.core.domain.usecase.NoParamUseCase
import com.example.arch.feature.home.domain.repository.HomeRepository
import com.example.arch.core.common.result.Result
import javax.inject.Inject

class RefreshPostsUseCase @Inject constructor(
    private val repository: HomeRepository,
    dispatchers: DispatcherProvider,
) : NoParamUseCase<Unit>(dispatchers) {

    override suspend fun execute() {
        val result = repository.refreshPosts()
        if (result is Result.Error) throw result.exception
    }
}
