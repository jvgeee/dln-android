package com.deeplinknow

import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeepLinkNow private constructor(
    private val config: DLNConfig,
    private val context: Context
) {
    companion object {
        private var instance: DeepLinkNow? = null

        @JvmStatic
        fun initialize(context: Context, apiKey: String) {
            if (instance == null) {
                instance = DeepLinkNow(
                    config = DLNConfig(apiKey),
                    context = context.applicationContext
                )
            }
        }

        @JvmStatic
        fun getInstance(): DeepLinkNow {
            return instance ?: throw DLNError.NotInitialized
        }
    }

    suspend fun checkDeferredDeepLink(
        callback: (Uri?, DLNAttribution?) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            val fingerprint = DLNDeviceFingerprint.generate(context)
            val response = makeApiRequest(
                endpoint = "deferred_deeplink",
                method = "POST",
                body = mapOf(
                    "fingerprint" to fingerprint.toMap()
                )
            )
            
            withContext(Dispatchers.Main) {
                callback(
                    response.deepLink?.let { Uri.parse(it) },
                    response.attribution
                )
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                callback(null, null)
            }
        }
    }

    fun checkClipboard(): String? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return clipboard.primaryClip?.getItemAt(0)?.text?.toString()
    }

    fun createDeepLink(
        path: String,
        customParameters: DLNCustomParameters? = null
    ): Uri {
        return Uri.Builder()
            .scheme("deeplinknow")
            .authority("app")
            .path(path)
            .apply {
                customParameters?.toMap()?.forEach { (key, value) ->
                    appendQueryParameter(key, value.toString())
                }
            }
            .build()
    }

    fun parseDeepLink(uri: Uri): DeepLinkData {
        val params = DLNCustomParameters()
        uri.queryParameterNames.forEach { key ->
            params[key] = uri.getQueryParameter(key)
        }
        
        return DeepLinkData(
            path = uri.path ?: "",
            parameters = params
        )
    }

    private suspend fun makeApiRequest(
        endpoint: String,
        method: String = "GET",
        body: Map<String, Any>? = null
    ): DeferredDeepLinkResponse {
        // Implementation using Retrofit
        TODO("Implement API request")
    }
} 