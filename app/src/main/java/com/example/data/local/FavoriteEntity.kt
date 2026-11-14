package com.example.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.FavoriteItem
import com.example.model.MediaType

@Entity(tableName = "favorite_items")
data class FavoriteEntity(
  @PrimaryKey
  @ColumnInfo(name = "id")
  val id: String,

  @ColumnInfo(name = "media_type")
  val mediaType: MediaType,

  @ColumnInfo(name = "title")
  val title: String,

  @ColumnInfo(name = "subtitle")
  val subtitle: String,

  @ColumnInfo(name = "image_url")
  val imageUrl: String,

  @ColumnInfo(name = "badge")
  val badge: String,

  @ColumnInfo(name = "rating")
  val rating: Float? = null,

  @ColumnInfo(name = "saved_at")
  val savedAt: Long = System.currentTimeMillis()
)

fun FavoriteEntity.toFavoriteItem(): FavoriteItem {
  return FavoriteItem(
    id = id,
    mediaType = mediaType,
    title = title,
    subtitle = subtitle,
    imageUrl = imageUrl,
    badge = badge,
    rating = rating
  )
}

fun FavoriteItem.toEntity(savedAt: Long = System.currentTimeMillis()): FavoriteEntity {
  return FavoriteEntity(
    id = id,
    mediaType = mediaType,
    title = title,
    subtitle = subtitle,
    imageUrl = imageUrl,
    badge = badge,
    rating = rating,
    savedAt = savedAt
  )
}
