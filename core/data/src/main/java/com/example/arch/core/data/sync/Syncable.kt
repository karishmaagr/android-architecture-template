package com.example.arch.core.data.sync

import com.example.arch.core.common.result.Result

interface Syncable {
    suspend fun sync(): Result<Unit>
}
