package com.jetbrains.kmpapp

import androidx.compose.ui.window.ComposeUIViewController
import com.jetbrains.kmpapp.auth.initializeFirebase
import com.jetbrains.kmpapp.di.initKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initializeFirebase()
    initKoin()
    return ComposeUIViewController { App() }
}
