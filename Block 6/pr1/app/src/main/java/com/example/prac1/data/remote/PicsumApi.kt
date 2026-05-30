package com.example.prac1.data.remote

import retrofit2.http.GET

interface PicsumApi {
    @GET("v2/list")
    suspend fun getPhotos(): List<PhotoDto>
}