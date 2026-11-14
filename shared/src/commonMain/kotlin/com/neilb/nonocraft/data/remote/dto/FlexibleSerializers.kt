package com.neilb.nonocraft.data.remote.dto

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer

/** The API sends `paletteHex` either as an array or as a JSON-encoded string of that array. */
object PaletteHexSerializer : JsonTransformingSerializer<List<String>>(ListSerializer(String.serializer())) {
    override fun transformDeserialize(element: JsonElement): JsonElement = when {
        element is JsonPrimitive && element.isString ->
            runCatching { Json.parseToJsonElement(element.content) }.getOrDefault(JsonArray(emptyList()))
        else -> element
    }
}

/** The API sends `grid` either as a matrix or, in list endpoints, as `{ width, height, palette, cells }`. */
object GridSerializer : JsonTransformingSerializer<List<List<Int>>>(ListSerializer(ListSerializer(Int.serializer()))) {
    override fun transformDeserialize(element: JsonElement): JsonElement = when (element) {
        is JsonObject -> element["cells"] ?: JsonArray(emptyList())
        else -> element
    }
}
