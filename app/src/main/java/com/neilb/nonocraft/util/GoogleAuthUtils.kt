package com.neilb.nonocraft.common

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

fun buildGoogleCredentialRequest(): GetCredentialRequest {
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(Constants.GOOGLE_CLIENT_ID)
        .build()

    return GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()
}

suspend fun signInWithGoogle(
    context: Context,
    credentialManager: CredentialManager,
    request: GetCredentialRequest,
): Result<String> {
    return try {
        val result = credentialManager.getCredential(request = request, context = context)
        val credential = result.credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            Result.success(googleIdTokenCredential.idToken)
        } else {
            Result.failure(IllegalStateException("Bilinmeyen bir kimlik bilgisi türü döndü"))
        }
    } catch (e: GoogleIdTokenParsingException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }
}