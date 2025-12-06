package com.jetbrains.kmpapp

import androidx.compose.ui.window.ComposeUIViewController
import com.jetbrains.kmpapp.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}
