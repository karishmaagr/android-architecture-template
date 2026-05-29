package com.example.arch.core.data.auth

import com.example.arch.core.common.result.Result

interface LogoutService {
    suspend fun logout(): Result<Unit>
}
