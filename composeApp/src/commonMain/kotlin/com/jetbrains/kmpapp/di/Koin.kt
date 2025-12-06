package com.jetbrains.kmpapp.di

import com.jetbrains.kmpapp.auth.AuthRepository
import com.jetbrains.kmpapp.auth.AuthViewModel
import com.jetbrains.kmpapp.auth.InMemoryAuthRepository
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val authModule = module {
    single<AuthRepository> { InMemoryAuthRepository() }
}

val viewModelModule = module {
    factoryOf(::AuthViewModel)
}

fun initKoin() {
    if (GlobalContext.getOrNull() != null) return

    startKoin {
        modules(
            authModule,
            viewModelModule,
        )
    }
}
