package com.mhirrr.videophotoeditor.data.local.converters

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EditedPhotosConverter {

    @TypeConverter
    fun fromList(value: List<Float>) = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<Float>>(value)

}