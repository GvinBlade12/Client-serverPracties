package com.example.ex8

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.delay

class UploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val finalPath = inputData.getString("filePath")
            ?: return Result.failure()

        for (i in 0..100 step 10) {
            delay(200)
            setProgress(workDataOf("progress" to i))
        }

        return Result.success(
            workDataOf("result" to "Готово! Фото загружено: $finalPath")
        )
    }
}