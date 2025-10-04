package com.talkeys.shared

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}! Welcome to Talkeys KMP!"
    }
}
