package com.deeplinknow

class DLNCustomParameters {
    private val parameters = mutableMapOf<String, Any>()

    operator fun get(key: String): Any? = parameters[key]
    operator fun set(key: String, value: Any?) {
        if (value != null) {
            parameters[key] = value
        } else {
            parameters.remove(key)
        }
    }

    fun string(key: String): String? = parameters[key] as? String
    fun int(key: String): Int? = parameters[key] as? Int
    fun boolean(key: String): Boolean? = parameters[key] as? Boolean
    fun dictionary(key: String): Map<String, Any>? = parameters[key] as? Map<String, Any>

    internal fun toMap(): Map<String, Any> = parameters.toMap()
} 