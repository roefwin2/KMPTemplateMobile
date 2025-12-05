package com.jetbrains.kmpapp.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions

private var firebaseInitialized = false

actual fun initializeFirebase(appContext: Any?) {
    if (firebaseInitialized) return
    // Replace the placeholder values with the configuration of your Firebase project.
    Firebase.initialize(
        options = FirebaseOptions(
            applicationId = "1:1234567890:ios:placeholder",
            apiKey = "demo-api-key",
            projectId = "demo-project",
            databaseUrl = "https://demo-project.firebaseio.com",
            storageBucket = "demo-project.appspot.com",
            gcmSenderId = "1234567890",
        ),
    )
    firebaseInitialized = true
}
