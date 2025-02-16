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
    private fun log(message: String, vararg args: Any?) {
        if (config.enableLogs) {
            println("[DeepLinkNow] $message ${args.joinToString()}")
        }
    }

    companion object {
        private var instance: DeepLinkNow? = null

        @JvmStatic
        @JvmOverloads
        fun initialize(
            context: Context, 
            apiKey: String,
            config: Map<String, Any> = emptyMap()
        ) {
            if (instance == null) {
                val enableLogs = config["enableLogs"] as? Boolean ?: false
                instance = DeepLinkNow(
                    config = DLNConfig(
                        apiKey = apiKey,
                        enableLogs = enableLogs
                    ),
                    context = context.applicationContext
                )
                instance?.log("Initialized with config:", "apiKey" to apiKey, "config" to config)
            }
        }

        @JvmStatic
        fun getInstance(): DeepLinkNow {
            return instance ?: throw DLNError.NotInitialized
        }
    }

    fun checkClipboard(): String? {
        log("Checking clipboard")
        return try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val content = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
            log("Clipboard content:", content ?: "null")
            content
        } catch (e: Exception) {
            log("Failed to check clipboard:", e)
            null
        }
    }

    suspend fun checkDeferredDeepLink(
        callback: (Uri?, DLNAttribution?) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            log("Checking deferred deep link")
            val fingerprint = DLNDeviceFingerprint.generate(context)
            val response = makeApiRequest(
                endpoint = "deferred_deeplink",
                method = "POST",
                body = mapOf(
                    "fingerprint" to fingerprint.toMap()
                )
            )
            
            log("Deferred deep link response:", response)
            withContext(Dispatchers.Main) {
                callback(
                    response.deepLink?.let { Uri.parse(it) },
                    response.attribution
                )
            }
        } catch (e: Exception) {
            log("Failed to check deferred deep link:", e)
            withContext(Dispatchers.Main) {
                callback(null, null)
            }
        }
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