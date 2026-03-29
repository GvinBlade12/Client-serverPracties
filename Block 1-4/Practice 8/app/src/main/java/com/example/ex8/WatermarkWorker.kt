package com.example.ex8

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.delay

class WatermarkWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val inputPath = inputData.getString("filePath")
            ?: return Result.failure()

        for (i in 0..100 step 10) {
            delay(200)
            setProgress(workDataOf("progress" to i))
        }

        val resultPath = "watermarked_$inputPath"

        return Result.success(
            workDataOf("filePath" to resultPath)
        )
    }
}