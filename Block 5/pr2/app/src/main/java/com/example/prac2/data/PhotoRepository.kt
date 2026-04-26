package com.example.prac2.data

import android.content.Context
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PhotoRepository(private val context: Context) {

    private val dir: File =
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

    fun loadPhotos(): List<PhotoItem> {
        return dir.listFiles()?.sortedByDescending { it.lastModified() }
            ?.map { PhotoItem(it) } ?: emptyList()
    }

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())

        val fileName = "IMG_${timeStamp}.jpg"

        return File(dir, fileName)
    }
}