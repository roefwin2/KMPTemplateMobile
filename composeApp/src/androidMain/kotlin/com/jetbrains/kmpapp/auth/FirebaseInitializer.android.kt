package com.jetbrains.kmpapp.auth

import android.content.Context
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions

private var firebaseInitialized = false

actual fun initializeFirebase(appContext: Any?) {
    if (firebaseInitialized) return
    val context = appContext as? Context ?: return

    // Replace the placeholder values with the configuration of your Firebase project or
    // rely on the google-services plugin to inject them at build time.
    Firebase.initialize(
        context = context,
        options = FirebaseOptions(
            applicationId = "1:1234567890:android:placeholder",
            apiKey = "demo-api-key",
            projectId = "demo-project",
            databaseUrl = "https://demo-project.firebaseio.com",
            storageBucket = "demo-project.appspot.com",
            gcmSenderId = "1234567890",
        ),
    )
    firebaseInitialized = true
}
