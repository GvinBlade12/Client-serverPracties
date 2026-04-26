package com.example.prac2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prac2.data.PhotoRepository
import com.example.prac2.ui.screens.GalleryScreen
import com.example.prac2.ui.theme.Prac2Theme
import com.example.prac2.viewmodel.PhotoViewModel
import com.example.prac2.viewmodel.PhotoViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = PhotoRepository(applicationContext)

        setContent {
            Prac2Theme {

                val viewModel: PhotoViewModel = viewModel(
                    factory = PhotoViewModelFactory(repository)
                )

                GalleryScreen(viewModel)
            }
        }
    }
}