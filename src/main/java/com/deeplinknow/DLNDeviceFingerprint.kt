package com.deeplinknow

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.WindowManager
import java.util.*

data class DLNDeviceFingerprint(
    val deviceModel: String,
    val systemVersion: String,
    val screenResolution: String,
    val timezone: String,
    val language: String,
    val carrier: String?,
    val ipAddress: String?,
    val advertisingId: String?
) {
    companion object {
        fun generate(context: Context): DLNDeviceFingerprint {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)

            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            return DLNDeviceFingerprint(
                deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
                systemVersion = Build.VERSION.RELEASE,
                screenResolution = "${metrics.widthPixels}x${metrics.heightPixels}",
                timezone = TimeZone.getDefault().id,
                language = Locale.getDefault().language,
                carrier = telephonyManager.networkOperatorName,
                ipAddress = getIPAddress(),
                advertisingId = getAdvertisingId(context)
            )
        }

        private fun getIPAddress(): String? {
            // Implementation for getting IP address
            return null
        }

        private fun getAdvertisingId(context: Context): String? {
            return Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "device_model" to deviceModel,
        "system_version" to systemVersion,
        "screen_resolution" to screenResolution,
        "timezone" to timezone,
        "language" to language,
        "carrier" to carrier,
        "ip_address" to ipAddress,
        "advertising_id" to advertisingId
    )
} 