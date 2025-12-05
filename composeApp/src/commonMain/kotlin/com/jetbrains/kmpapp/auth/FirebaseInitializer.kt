package com.jetbrains.kmpapp.auth

/**
 * Platform-specific Firebase initialization that keeps the GitLive SDK ready for authentication.
 */
expect fun initializeFirebase(appContext: Any? = null)
