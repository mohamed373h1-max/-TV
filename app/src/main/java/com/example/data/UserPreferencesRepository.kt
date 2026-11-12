package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.FavoriteItem
import com.example.model.MediaType
import com.example.model.UserComment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class UserPreferencesRepository(context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("joker_tv_prefs", Context.MODE_PRIVATE)

  private val _favoriteItems = MutableStateFlow<List<FavoriteItem>>(emptyList())
  val favoriteItems: StateFlow<List<FavoriteItem>> = _favoriteItems.asStateFlow()

  private val _userComments = MutableStateFlow<List<UserComment>>(emptyList())
  val userComments: StateFlow<List<UserComment>> = _userComments.asStateFlow()

  private val _watchHistory = MutableStateFlow<List<com.example.model.WatchHistoryItem>>(emptyList())
  val watchHistory: StateFlow<List<com.example.model.WatchHistoryItem>> = _watchHistory.asStateFlow()

  init {
    loadFavorites()
    loadComments()
    loadWatchHistory()
  }

  private fun loadFavorites() {
    val jsonString = prefs.getString("favorites_json", null)
    if (jsonString.isNullOrEmpty()) {
      // Default initial favorites so the screen is immediately populated with popular picks
      val defaults = listOf(
        FavoriteItem(
          id = "bein_1",
          mediaType = MediaType.CHANNEL,
          title = "beIN SPORTS 1 HD",
          subtitle = "قمة الدوري الإنجليزي",
          imageUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=400&auto=format&fit=crop&q=80",
          badge = "مباشر 4K",
          rating = 4.9f
        ),
        FavoriteItem(
          id = "series_hashashin",
          mediaType = MediaType.SERIES,
          title = "الحشاشين",
          subtitle = "كريم عبد العزيز - 30 حلقة",
          imageUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=500&auto=format&fit=crop&q=80",
          badge = "رمضان 2024",
          rating = 4.9f
        ),
        FavoriteItem(
          id = "mov_welad_rizk_3",
          mediaType = MediaType.MOVIE,
          title = "ولاد رزق 3: القاضية",
          subtitle = "أحمد عز وعمرو يوسف",
          imageUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500&auto=format&fit=crop&q=80",
          badge = "سينما 2024",
          rating = 4.9f
        )
      )
      _favoriteItems.value = defaults
      saveFavorites(defaults)
      return
    }

    try {
      val jsonArray = JSONArray(jsonString)
      val list = mutableListOf<FavoriteItem>()
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          FavoriteItem(
            id = obj.getString("id"),
            mediaType = MediaType.valueOf(obj.getString("mediaType")),
            title = obj.getString("title"),
            subtitle = obj.optString("subtitle", ""),
            imageUrl = obj.optString("imageUrl", ""),
            badge = obj.optString("badge", ""),
            rating = if (obj.has("rating")) obj.getDouble("rating").toFloat() else null
          )
        )
      }
      _favoriteItems.value = list
    } catch (e: Exception) {
      _favoriteItems.value = emptyList()
    }
  }

  private fun saveFavorites(items: List<FavoriteItem>) {
    val jsonArray = JSONArray()
    for (item in items) {
      val obj = JSONObject().apply {
        put("id", item.id)
        put("mediaType", item.mediaType.name)
        put("title", item.title)
        put("subtitle", item.subtitle)
        put("imageUrl", item.imageUrl)
        put("badge", item.badge)
        item.rating?.let { put("rating", it.toDouble()) }
      }
      jsonArray.put(obj)
    }
    prefs.edit().putString("favorites_json", jsonArray.toString()).apply()
  }

  fun toggleFavorite(item: FavoriteItem): Boolean {
    val current = _favoriteItems.value.toMutableList()
    val existsIndex = current.indexOfFirst { it.id == item.id }
    val isNowFavorite: Boolean
    if (existsIndex >= 0) {
      current.removeAt(existsIndex)
      isNowFavorite = false
    } else {
      current.add(0, item)
      isNowFavorite = true
    }
    _favoriteItems.value = current
    saveFavorites(current)
    return isNowFavorite
  }

  fun isFavorite(itemId: String): Boolean {
    return _favoriteItems.value.any { it.id == itemId }
  }

  private fun loadComments() {
    val initial = JokerTvDataProvider.sampleComments.toMutableList()
    val customJson = prefs.getString("user_comments_json", null)
    if (!customJson.isNullOrEmpty()) {
      try {
        val jsonArray = JSONArray(customJson)
        for (i in 0 until jsonArray.length()) {
          val obj = jsonArray.getJSONObject(i)
          initial.add(
            0,
            UserComment(
              id = obj.getString("id"),
              itemId = obj.getString("itemId"),
              authorName = obj.getString("authorName"),
              rating = obj.getInt("rating"),
              commentText = obj.getString("commentText"),
              timestampText = obj.optString("timestampText", "الآن"),
              likes = obj.optInt("likes", 1)
            )
          )
        }
      } catch (e: Exception) {
        // ignore parse error
      }
    }
    _userComments.value = initial
  }

  fun addComment(itemId: String, authorName: String, rating: Int, commentText: String) {
    val newComment = UserComment(
      id = "c_${System.currentTimeMillis()}",
      itemId = itemId,
      authorName = authorName.ifBlank { "مشاهد جوكر TV" },
      rating = rating.coerceIn(1, 5),
      commentText = commentText,
      timestampText = "الآن",
      likes = 1
    )

    val updated = listOf(newComment) + _userComments.value
    _userComments.value = updated

    // Persist custom user comments
    val customComments = updated.filter { it.id.startsWith("c_") && !it.id.contains("c_1") && !it.id.contains("c_2") && !it.id.contains("c_3") && !it.id.contains("c_4") && !it.id.contains("c_5") && !it.id.contains("c_6") }
    val jsonArray = JSONArray()
    for (c in customComments) {
      val obj = JSONObject().apply {
        put("id", c.id)
        put("itemId", c.itemId)
        put("authorName", c.authorName)
        put("rating", c.rating)
        put("commentText", c.commentText)
        put("timestampText", c.timestampText)
        put("likes", c.likes)
      }
      jsonArray.put(obj)
    }
    prefs.edit().putString("user_comments_json", jsonArray.toString()).apply()
  }

  private fun loadWatchHistory() {
    val jsonString = prefs.getString("watch_history_json", null)
    if (jsonString.isNullOrEmpty()) {
      // Default sample items to showcase the Continue Watching shelf immediately
      val defaults = listOf(
        com.example.model.WatchHistoryItem(
          id = "series_hashashin_ep_4",
          contentId = "series_hashashin",
          mediaType = MediaType.SERIES,
          title = "مسلسل الحشاشين",
          subtitle = "الحلقة 4 • قلعة ألموت",
          imageUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=500&auto=format&fit=crop&q=80",
          episodeNumber = 4,
          lastPositionMs = 1115000L, // 18m 35s
          totalDurationMs = 2700000L, // 45m
          watchedAtTimestamp = System.currentTimeMillis() - 3600000L
        ),
        com.example.model.WatchHistoryItem(
          id = "mov_welad_rizk_3_ep_0",
          contentId = "mov_welad_rizk_3",
          mediaType = MediaType.MOVIE,
          title = "ولاد رزق 3: القاضية",
          subtitle = "أحمد عز وعمرو يوسف",
          imageUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500&auto=format&fit=crop&q=80",
          episodeNumber = null,
          lastPositionMs = 2890000L, // 48m 10s
          totalDurationMs = 6900000L, // 115m
          watchedAtTimestamp = System.currentTimeMillis() - 7200000L
        ),
        com.example.model.WatchHistoryItem(
          id = "series_atawla_ep_2",
          contentId = "series_atawla",
          mediaType = MediaType.SERIES,
          title = "مسلسل العتاولة",
          subtitle = "الحلقة 2 • نصار وخضر",
          imageUrl = "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?w=500&auto=format&fit=crop&q=80",
          episodeNumber = 2,
          lastPositionMs = 650000L, // 10m 50s
          totalDurationMs = 2700000L, // 45m
          watchedAtTimestamp = System.currentTimeMillis() - 14400000L
        )
      )
      _watchHistory.value = defaults
      saveWatchHistory(defaults)
      return
    }

    try {
      val jsonArray = JSONArray(jsonString)
      val list = mutableListOf<com.example.model.WatchHistoryItem>()
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          com.example.model.WatchHistoryItem(
            id = obj.getString("id"),
            contentId = obj.getString("contentId"),
            mediaType = MediaType.valueOf(obj.getString("mediaType")),
            title = obj.getString("title"),
            subtitle = obj.optString("subtitle", ""),
            imageUrl = obj.optString("imageUrl", ""),
            episodeNumber = if (obj.has("episodeNumber") && !obj.isNull("episodeNumber")) obj.getInt("episodeNumber") else null,
            lastPositionMs = obj.optLong("lastPositionMs", 0L),
            totalDurationMs = obj.optLong("totalDurationMs", 0L),
            watchedAtTimestamp = obj.optLong("watchedAtTimestamp", System.currentTimeMillis())
          )
        )
      }
      _watchHistory.value = list
    } catch (e: Exception) {
      _watchHistory.value = emptyList()
    }
  }

  private fun saveWatchHistory(items: List<com.example.model.WatchHistoryItem>) {
    val jsonArray = JSONArray()
    for (item in items) {
      val obj = JSONObject().apply {
        put("id", item.id)
        put("contentId", item.contentId)
        put("mediaType", item.mediaType.name)
        put("title", item.title)
        put("subtitle", item.subtitle)
        put("imageUrl", item.imageUrl)
        item.episodeNumber?.let { put("episodeNumber", it) }
        put("lastPositionMs", item.lastPositionMs)
        put("totalDurationMs", item.totalDurationMs)
        put("watchedAtTimestamp", item.watchedAtTimestamp)
      }
      jsonArray.put(obj)
    }
    prefs.edit().putString("watch_history_json", jsonArray.toString()).apply()
  }

  fun recordWatchProgress(item: com.example.model.WatchHistoryItem) {
    val current = _watchHistory.value.toMutableList()
    val existingIndex = current.indexOfFirst {
      it.contentId == item.contentId && it.episodeNumber == item.episodeNumber
    }

    val updatedItem = item.copy(
      watchedAtTimestamp = System.currentTimeMillis()
    )

    if (existingIndex >= 0) {
      current.removeAt(existingIndex)
    }
    current.add(0, updatedItem)

    // Keep up to 50 items
    val limited = current.take(50)
    _watchHistory.value = limited
    saveWatchHistory(limited)
  }

  fun getResumePosition(contentId: String, episodeNumber: Int?): Long {
    val found = _watchHistory.value.find {
      it.contentId == contentId && it.episodeNumber == episodeNumber
    }
    return found?.lastPositionMs ?: 0L
  }

  fun removeWatchHistoryItem(id: String) {
    val updated = _watchHistory.value.filterNot { it.id == id }
    _watchHistory.value = updated
    saveWatchHistory(updated)
  }

  fun clearWatchHistory() {
    _watchHistory.value = emptyList()
    prefs.edit().remove("watch_history_json").apply()
  }

  fun getCommentsForItem(itemId: String): List<UserComment> {
    return _userComments.value.filter { it.itemId == itemId }
  }
}
