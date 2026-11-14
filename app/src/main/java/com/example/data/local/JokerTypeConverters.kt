package com.example.data.local

import androidx.room.TypeConverter
import com.example.model.MediaType

class JokerTypeConverters {

  @TypeConverter
  fun fromMediaType(value: MediaType): String = value.name

  @TypeConverter
  fun toMediaType(value: String): MediaType {
    return runCatching { MediaType.valueOf(value) }.getOrDefault(MediaType.CHANNEL)
  }
}
