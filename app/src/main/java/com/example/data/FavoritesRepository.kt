package com.example.data

import com.example.data.local.FavoriteDao
import com.example.data.local.toEntity
import com.example.data.local.toFavoriteItem
import com.example.model.FavoriteItem
import com.example.model.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepository(private val favoriteDao: FavoriteDao) {

  val allFavorites: Flow<List<FavoriteItem>> =
    favoriteDao.getAllFavorites().map { entities -> entities.map { it.toFavoriteItem() } }

  val favoriteSportsChannels: Flow<List<FavoriteItem>> =
    favoriteDao.getFavoriteSportsChannels().map { entities -> entities.map { it.toFavoriteItem() } }

  val favoriteMovies: Flow<List<FavoriteItem>> =
    favoriteDao.getFavoriteMovies().map { entities -> entities.map { it.toFavoriteItem() } }

  val favoriteSeries: Flow<List<FavoriteItem>> =
    favoriteDao.getFavoriteSeries().map { entities -> entities.map { it.toFavoriteItem() } }

  val favoritesCount: Flow<Int> = favoriteDao.observeFavoritesCount()

  fun getFavoritesByType(mediaType: MediaType): Flow<List<FavoriteItem>> =
    favoriteDao.getFavoritesByType(mediaType).map { entities -> entities.map { it.toFavoriteItem() } }

  fun observeIsFavorite(id: String): Flow<Boolean> = favoriteDao.observeIsFavorite(id)

  suspend fun isFavorite(id: String): Boolean = favoriteDao.isFavorite(id)

  suspend fun addFavorite(item: FavoriteItem) {
    favoriteDao.insertFavorite(item.toEntity())
  }

  suspend fun removeFavorite(id: String) {
    favoriteDao.deleteFavoriteById(id)
  }

  suspend fun toggleFavorite(item: FavoriteItem): Boolean {
    val existing = favoriteDao.getFavoriteById(item.id)
    return if (existing != null) {
      favoriteDao.deleteFavoriteById(item.id)
      false
    } else {
      favoriteDao.insertFavorite(item.toEntity())
      true
    }
  }

  suspend fun seedFavoritesIfNeeded(defaults: List<FavoriteItem>) {
    val now = System.currentTimeMillis()
    val entities = defaults.mapIndexed { index, item ->
      item.toEntity(savedAt = now - index)
    }
    favoriteDao.insertFavorites(entities)
  }

  suspend fun clearAll() {
    favoriteDao.clearAllFavorites()
  }
}
