package com.talkeys.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
