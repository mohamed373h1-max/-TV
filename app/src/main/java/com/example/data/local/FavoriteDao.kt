package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.MediaType
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

  @Query("SELECT * FROM favorite_items ORDER BY saved_at DESC")
  fun getAllFavorites(): Flow<List<FavoriteEntity>>

  @Query("SELECT * FROM favorite_items WHERE media_type = :mediaType ORDER BY saved_at DESC")
  fun getFavoritesByType(mediaType: MediaType): Flow<List<FavoriteEntity>>

  @Query("SELECT * FROM favorite_items WHERE media_type IN ('CHANNEL', 'MATCH') ORDER BY saved_at DESC")
  fun getFavoriteSportsChannels(): Flow<List<FavoriteEntity>>

  @Query("SELECT * FROM favorite_items WHERE media_type = 'MOVIE' ORDER BY saved_at DESC")
  fun getFavoriteMovies(): Flow<List<FavoriteEntity>>

  @Query("SELECT * FROM favorite_items WHERE media_type = 'SERIES' ORDER BY saved_at DESC")
  fun getFavoriteSeries(): Flow<List<FavoriteEntity>>

  @Query("SELECT EXISTS(SELECT 1 FROM favorite_items WHERE id = :id)")
  fun observeIsFavorite(id: String): Flow<Boolean>

  @Query("SELECT COUNT(*) FROM favorite_items")
  fun observeFavoritesCount(): Flow<Int>

  @Query("SELECT * FROM favorite_items WHERE id = :id LIMIT 1")
  suspend fun getFavoriteById(id: String): FavoriteEntity?

  @Query("SELECT EXISTS(SELECT 1 FROM favorite_items WHERE id = :id)")
  suspend fun isFavorite(id: String): Boolean

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFavorite(favorite: FavoriteEntity)

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertFavorites(favorites: List<FavoriteEntity>)

  @Delete
  suspend fun deleteFavorite(favorite: FavoriteEntity)

  @Query("DELETE FROM favorite_items WHERE id = :id")
  suspend fun deleteFavoriteById(id: String)

  @Query("DELETE FROM favorite_items WHERE media_type = :mediaType")
  suspend fun deleteFavoritesByType(mediaType: MediaType)

  @Query("DELETE FROM favorite_items")
  suspend fun clearAllFavorites()
}
