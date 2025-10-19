package com.talkeys.shared.di

import com.talkeys.shared.network.ApiClient
import com.talkeys.shared.network.PaymentApiService
import com.talkeys.shared.data.payment.PaymentRepository
import com.talkeys.shared.auth.AuthRepository
import com.talkeys.shared.auth.createGoogleSignInProvider
import com.talkeys.shared.auth.createTokenStorage
import org.koin.dsl.module

val sharedModule = module {
    single { ApiClient() }
    single { PaymentApiService(get()) }
    single { PaymentRepository(get()) }
    single { createGoogleSignInProvider() }
    single { createTokenStorage() }
    single { 
        AuthRepository(
            httpClient = get<ApiClient>().httpClient,
            googleSignInProvider = get(),
            tokenStorage = get()
        ) 
    }
}

// Platform-specific modules should be provided by each platform
expect val platformModule: org.koin.core.module.Module
