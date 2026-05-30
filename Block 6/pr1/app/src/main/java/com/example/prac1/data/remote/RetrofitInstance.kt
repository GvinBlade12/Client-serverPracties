package com.example.prac1.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory // Или kotlinx.serialization

object RetrofitInstance {
    val api: PicsumApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://picsum.photos/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PicsumApi::class.java)
    }
}