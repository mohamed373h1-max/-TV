package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.FavoriteDao
import com.example.data.local.JokerDatabase
import com.example.model.FavoriteItem
import com.example.model.MediaType
import com.example.model.ServerInfo
import com.example.model.SportsChannel
import com.example.model.UserComment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class UserPreferencesRepository(
  context: Context,
  favoriteDao: FavoriteDao = JokerDatabase.getInstance(context).favoriteDao()
) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("joker_tv_prefs", Context.MODE_PRIVATE)

  val favoritesRepository = FavoritesRepository(favoriteDao)

  val favoriteItems: Flow<List<FavoriteItem>> = favoritesRepository.allFavorites
  val favoriteSportsChannels: Flow<List<FavoriteItem>> = favoritesRepository.favoriteSportsChannels
  val favoriteMovies: Flow<List<FavoriteItem>> = favoritesRepository.favoriteMovies

  private val _userComments = MutableStateFlow<List<UserComment>>(emptyList())
  val userComments: StateFlow<List<UserComment>> = _userComments.asStateFlow()

  private val _watchHistory = MutableStateFlow<List<com.example.model.WatchHistoryItem>>(emptyList())
  val watchHistory: StateFlow<List<com.example.model.WatchHistoryItem>> = _watchHistory.asStateFlow()

  private val _customChannels = MutableStateFlow<List<SportsChannel>>(emptyList())
  val customChannels: StateFlow<List<SportsChannel>> = _customChannels.asStateFlow()

  init {
    loadComments()
    loadWatchHistory()
    loadCustomChannels()
  }

  suspend fun initializeFavorites() {
    val alreadySeeded = prefs.getBoolean("favorites_seeded_room", false)
    if (alreadySeeded) return

    val legacyJson = prefs.getString("favorites_json", null)
    val itemsToSeed = if (!legacyJson.isNullOrEmpty()) {
      parseLegacyFavorites(legacyJson)
    } else {
      defaultFavorites()
    }

    if (itemsToSeed.isNotEmpty()) {
      favoritesRepository.seedFavoritesIfNeeded(itemsToSeed)
    }
    prefs.edit().putBoolean("favorites_seeded_room", true).apply()
  }

  private fun defaultFavorites(): List<FavoriteItem> {
    return listOf(
      FavoriteItem(
        id = "bein_1",
        mediaType = MediaType.CHANNEL,
        title = "beIN SPORTS 1 HD",
        subtitle = "قمة الدوري الإنجليزي ودوري أبطال أوروبا",
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
  }

  private fun parseLegacyFavorites(jsonString: String): List<FavoriteItem> {
    return try {
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
      list
    } catch (e: Exception) {
      emptyList()
    }
  }

  suspend fun toggleFavorite(item: FavoriteItem): Boolean {
    return favoritesRepository.toggleFavorite(item)
  }

  suspend fun addFavorite(item: FavoriteItem) {
    favoritesRepository.addFavorite(item)
  }

  suspend fun removeFavorite(itemId: String) {
    favoritesRepository.removeFavorite(itemId)
  }

  suspend fun clearFavorites() {
    favoritesRepository.clearAll()
  }

  suspend fun isFavorite(itemId: String): Boolean {
    return favoritesRepository.isFavorite(itemId)
  }

  fun observeIsFavorite(itemId: String): Flow<Boolean> {
    return favoritesRepository.observeIsFavorite(itemId)
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

  private fun loadCustomChannels() {
    val jsonString = prefs.getString("custom_channels_json", null) ?: return
    try {
      val jsonArray = JSONArray(jsonString)
      val list = mutableListOf<SportsChannel>()
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        val id = obj.getString("id")
        val name = obj.getString("name")
        val category = obj.optString("category", "قنواتك الخاصة")
        val logoUrl = obj.optString("logoUrl", "")
        val streamUrl = obj.getString("streamUrl")
        val qualityBadge = obj.optString("qualityBadge", "IPTV")
        val fallbackServers = JokerTvDataProvider.createDefaultServers(
          contentId = id,
          streamIndexOffset = i,
          mediaType = MediaType.CHANNEL
        )
        val userServer = ServerInfo(
          id = "${id}_user_primary",
          name = "رابط البث المضاف ($name)",
          quality = qualityBadge,
          streamUrl = streamUrl,
          pingMs = 14,
          isVip = true,
          isOnline = true
        )
        list.add(
          SportsChannel(
            id = id,
            name = name,
            category = category,
            logoUrl = logoUrl,
            isLive = true,
            currentEvent = obj.optString("currentEvent", "بث مباشر مضاف بواسطة المستخدم"),
            qualityBadge = qualityBadge,
            servers = listOf(userServer) + fallbackServers.take(3)
          )
        )
      }
      _customChannels.value = list
    } catch (_: Exception) {
      _customChannels.value = emptyList()
    }
  }

  private fun saveCustomChannels(list: List<SportsChannel>) {
    val jsonArray = JSONArray()
    for (ch in list) {
      val primaryUrl = ch.servers.firstOrNull()?.streamUrl ?: continue
      val obj = JSONObject().apply {
        put("id", ch.id)
        put("name", ch.name)
        put("category", ch.category)
        put("logoUrl", ch.logoUrl)
        put("streamUrl", primaryUrl)
        put("qualityBadge", ch.qualityBadge)
        put("currentEvent", ch.currentEvent)
      }
      jsonArray.put(obj)
    }
    prefs.edit().putString("custom_channels_json", jsonArray.toString()).apply()
  }

  fun addCustomChannels(newChannels: List<SportsChannel>) {
    if (newChannels.isEmpty()) return
    val updated = (newChannels + _customChannels.value).distinctBy {
      it.servers.firstOrNull()?.streamUrl ?: it.id
    }.take(100)
    _customChannels.value = updated
    saveCustomChannels(updated)
  }

  fun removeCustomChannel(channelId: String) {
    val updated = _customChannels.value.filterNot { it.id == channelId }
    _customChannels.value = updated
    saveCustomChannels(updated)
  }

  fun clearCustomChannels() {
    _customChannels.value = emptyList()
    prefs.edit().remove("custom_channels_json").apply()
  }
}
