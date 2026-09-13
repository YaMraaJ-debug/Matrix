package com.example.ghostdownloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ghostdownloader.ui.GhostDownloaderApp
import com.example.ghostdownloader.ui.MainViewModel
import com.example.ghostdownloader.ui.theme.GhostDownloaderTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GhostDownloaderTheme {
                GhostDownloaderApp(viewModel = viewModel)
            }
        }
    }
}

