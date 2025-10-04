package com.talkeys.shared

import com.talkeys.shared.di.sharedModule
import com.talkeys.shared.di.platformModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(sharedModule, platformModule)
}

// Called by Android
fun initKoin() = initKoin {}
