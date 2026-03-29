package com.example.ex8

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.delay

class CompressWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        for (i in 0..100 step 10) {
            delay(200)
            setProgress(workDataOf("progress" to i))
        }

        val compressedPath = "compressed_photo.jpg"

        return Result.success(
            workDataOf("filePath" to compressedPath)
        )
    }
}