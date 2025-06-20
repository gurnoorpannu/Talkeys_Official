package com.example.talkeys_new.screens.authentication

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException

class GoogleAuthClient(context: Context, clientId: String) {

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(clientId)
        .requestEmail()
        .build()

    private val signInClient = GoogleSignIn.getClient(context, gso)

    fun getSignInIntent(): Intent = signInClient.signInIntent

    fun getIdTokenFromIntent(data: Intent?): String? {
        return try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                .getResult(ApiException::class.java)
            account?.idToken
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun signOut() = signInClient.signOut()
}