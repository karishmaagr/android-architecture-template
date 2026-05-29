package com.example.arch.feature.home.domain.usecase

import com.example.arch.core.common.coroutines.DispatcherProvider
import com.example.arch.core.domain.usecase.FlowUseCase
import com.example.arch.feature.home.domain.model.Post
import com.example.arch.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val repository: HomeRepository,
    dispatchers: DispatcherProvider,
) : FlowUseCase<Unit, List<Post>>(dispatchers) {

    override fun execute(params: Unit): Flow<List<Post>> = repository.getPosts()
}
