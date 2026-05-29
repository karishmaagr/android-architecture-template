package com.example.arch.services.sync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.arch.core.common.result.Result
import com.example.arch.core.data.sync.Syncable
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SyncWorkerTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val syncable = mockk<Syncable>()

    private fun buildWorker(): SyncWorker =
        TestListenableWorkerBuilder<SyncWorker>(context)
            .setWorkerFactory(
                object : androidx.work.WorkerFactory() {
                    override fun createWorker(
                        appContext: Context,
                        workerClassName: String,
                        workerParameters: androidx.work.WorkerParameters,
                    ) = SyncWorker(appContext, workerParameters, syncable)
                }
            )
            .build()

    @Test
    fun `returns success when sync succeeds`() = runTest {
        coEvery { syncable.sync() } returns Result.Success(Unit)
        val result = buildWorker().doWork()
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `returns retry when sync returns error on first attempt`() = runTest {
        coEvery { syncable.sync() } returns Result.Error(RuntimeException("Network"))
        val result = buildWorker().doWork()
        assertEquals(ListenableWorker.Result.retry(), result)
    }
}
