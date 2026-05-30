package com.example.mod6z1
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mod6z1.data.api.PhotoApiService
import com.example.mod6z1.data.repository.PhotoRepositoryImpl
import com.example.mod6z1.domain.usecase.GetPhotosUseCase
import com.example.mod6z1.presentation.photodetail.PhotoDetailScreen
import com.example.mod6z1.presentation.photolist.PhotoListScreen
import com.example.mod6z1.presentation.photolist.PhotoListViewModel
import com.example.mod6z1.ui.theme.Mod6z1Theme
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://picsum.photos/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val apiService = retrofit.create(PhotoApiService::class.java)

    private val photoRepository = PhotoRepositoryImpl(apiService)

    private val getPhotosUseCase = GetPhotosUseCase(photoRepository)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Mod6z1Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val viewModel: PhotoListViewModel = viewModel {
                        PhotoListViewModel(getPhotosUseCase)
                    }

                    NavHost(navController = navController, startDestination = "photo_list") {
                        composable("photo_list") {
                            PhotoListScreen(navController = navController, viewModel = viewModel)
                        }
                        composable("photo_detail/{photoId}") { backStackEntry ->
                            val photoId = backStackEntry.arguments?.getString("photoId")
                            val photo = viewModel.getPhotoById(photoId ?: "")
                            PhotoDetailScreen(navController = navController, photo = photo)
                        }
                    }
                }
            }
        }
    }
}