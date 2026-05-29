package com.example.arch.services.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.arch.core.common.result.Result
import com.example.arch.core.data.sync.Syncable
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncable: Syncable,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return when (syncable.sync()) {
            is com.example.arch.core.common.result.Result.Success -> Result.success()
            is com.example.arch.core.common.result.Result.Error   -> {
                if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()
            }
            is com.example.arch.core.common.result.Result.Loading -> Result.retry()
        }
    }

    companion object {
        const val WORK_NAME   = "SyncWorker"
        const val MAX_RETRIES = 3
    }
}
