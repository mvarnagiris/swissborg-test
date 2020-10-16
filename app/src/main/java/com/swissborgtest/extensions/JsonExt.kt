package com.swissborgtest.extensions

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull

fun JsonObject.getString(key: String): String? {
    val element = get(key)
    return if (element != null && element is JsonPrimitive && element.isString) element.content else null
}

fun JsonObject.getInt(key: String): Int? {
    val element = get(key)
    return if (element != null && element is JsonPrimitive) element.intOrNull else null
}