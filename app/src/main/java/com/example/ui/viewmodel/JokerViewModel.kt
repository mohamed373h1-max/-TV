package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.JokerTvDataProvider
import com.example.data.M3uPlaylistParser
import com.example.data.UserPreferencesRepository
import com.example.model.CartoonItem
import com.example.model.EgyptianMovie
import com.example.model.FavoriteItem
import com.example.model.FootballMatch
import com.example.model.MediaType
import com.example.model.RamadanSeries
import com.example.model.SportsChannel
import com.example.model.UserComment
import com.example.notification.AppNotification
import com.example.notification.JokerNotificationManager
import com.example.ui.screens.ActiveDetailItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavTab {
  HOME,
  SPORTS,
  RAMADAN,
  MOVIES,
  CARTOONS,
  FAVORITES
}

class JokerViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = UserPreferencesRepository(application)
  private val notificationManager = JokerNotificationManager(application)

  val channels: List<SportsChannel> = JokerTvDataProvider.sportsChannels
  val customChannels: StateFlow<List<SportsChannel>> = repository.customChannels
    .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
  val allChannels: StateFlow<List<SportsChannel>> = repository.customChannels
    .map { custom -> custom + channels }
    .stateIn(viewModelScope, SharingStarted.Lazily, channels)
  val matches: List<FootballMatch> = JokerTvDataProvider.todayMatches
  val seriesList: List<RamadanSeries> = JokerTvDataProvider.ramadanSeries
  val moviesList: List<EgyptianMovie> = JokerTvDataProvider.egyptianMovies
  val cartoonsList: List<CartoonItem> = JokerTvDataProvider.cartoonsList

  val favoriteItems: StateFlow<List<FavoriteItem>> = repository.favoriteItems
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val favoriteSportsChannels: StateFlow<List<FavoriteItem>> = repository.favoriteSportsChannels
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val favoriteMovies: StateFlow<List<FavoriteItem>> = repository.favoriteMovies
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    viewModelScope.launch {
      repository.initializeFavorites()
    }
  }

  val userComments: StateFlow<List<UserComment>> = repository.userComments
    .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  val watchHistory: StateFlow<List<com.example.model.WatchHistoryItem>> = repository.watchHistory
    .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  val notifications: StateFlow<List<AppNotification>> = notificationManager.notifications
    .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  val unreadNotificationsCount: StateFlow<Int> = notifications
    .map { list -> list.count { !it.isRead } }
    .stateIn(viewModelScope, SharingStarted.Lazily, 0)

  val seriesNotifyEnabled: StateFlow<Boolean> = notificationManager.seriesNotifyEnabled
  val moviesNotifyEnabled: StateFlow<Boolean> = notificationManager.moviesNotifyEnabled
  val matchesNotifyEnabled: StateFlow<Boolean> = notificationManager.matchesNotifyEnabled

  private val _currentTab = MutableStateFlow(NavTab.HOME)
  val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

  private val _activeDetail = MutableStateFlow<ActiveDetailItem?>(null)
  val activeDetail: StateFlow<ActiveDetailItem?> = _activeDetail.asStateFlow()

  private val _isSearchOpen = MutableStateFlow(false)
  val isSearchOpen: StateFlow<Boolean> = _isSearchOpen.asStateFlow()

  private val _isNotificationsOpen = MutableStateFlow(false)
  val isNotificationsOpen: StateFlow<Boolean> = _isNotificationsOpen.asStateFlow()

  fun setTab(tab: NavTab) {
    _currentTab.value = tab
    _activeDetail.value = null
  }

  fun openSearch() {
    _isSearchOpen.value = true
    _isNotificationsOpen.value = false
  }

  fun closeSearch() {
    _isSearchOpen.value = false
  }

  fun openNotifications() {
    _isNotificationsOpen.value = true
    _isSearchOpen.value = false
  }

  fun closeNotifications() {
    _isNotificationsOpen.value = false
  }

  fun openDetail(item: ActiveDetailItem) {
    _activeDetail.value = item
    _isSearchOpen.value = false
    _isNotificationsOpen.value = false
  }

  fun closeDetail() {
    _activeDetail.value = null
  }

  fun isFavorite(id: String): Boolean {
    return favoriteItems.value.any { it.id == id }
  }

  fun toggleFavorite(item: Any) {
    val favItem = when (item) {
      is SportsChannel -> FavoriteItem(
        id = item.id,
        mediaType = MediaType.CHANNEL,
        title = item.name,
        subtitle = item.currentEvent,
        imageUrl = item.logoUrl,
        badge = "قناة رياضية",
        rating = 4.9f
      )
      is FootballMatch -> FavoriteItem(
        id = item.id,
        mediaType = MediaType.MATCH,
        title = "${item.homeTeam} ضد ${item.awayTeam}",
        subtitle = "${item.championship} • ${item.commentator}",
        imageUrl = item.homeLogo,
        badge = if (item.isLive) "مباشر" else item.timeText,
        rating = 4.8f
      )
      is RamadanSeries -> FavoriteItem(
        id = item.id,
        mediaType = MediaType.SERIES,
        title = item.title,
        subtitle = "رمضان ${item.seasonYear} • ${item.episodesCount} حلقة",
        imageUrl = item.posterUrl,
        badge = "مسلسل",
        rating = item.rating
      )
      is EgyptianMovie -> FavoriteItem(
        id = item.id,
        mediaType = MediaType.MOVIE,
        title = item.title,
        subtitle = "سينما ${item.releaseYear} • ${item.genre}",
        imageUrl = item.posterUrl,
        badge = "فيلم",
        rating = item.rating
      )
      is CartoonItem -> FavoriteItem(
        id = item.id,
        mediaType = MediaType.CARTOON,
        title = item.title,
        subtitle = "${item.dubbingLanguage} • ${item.dubbingStudio}",
        imageUrl = item.posterUrl,
        badge = item.category,
        rating = item.rating
      )
      is FavoriteItem -> item
      else -> return
    }
    viewModelScope.launch {
      repository.toggleFavorite(favItem)
    }
  }

  fun clearAllFavorites() {
    viewModelScope.launch {
      repository.clearFavorites()
    }
  }

  fun openFavoriteItem(item: FavoriteItem) {
    when (item.mediaType) {
      MediaType.CHANNEL -> {
        val ch = allChannels.value.find { it.id == item.id } ?: allChannels.value.first()
        openDetail(ActiveDetailItem.Channel(ch))
      }
      MediaType.MATCH -> {
        val m = matches.find { it.id == item.id } ?: matches.first()
        openDetail(ActiveDetailItem.Match(m))
      }
      MediaType.SERIES -> {
        val s = seriesList.find { it.id == item.id } ?: seriesList.first()
        openDetail(ActiveDetailItem.Series(s))
      }
      MediaType.MOVIE -> {
        val mov = moviesList.find { it.id == item.id } ?: moviesList.first()
        openDetail(ActiveDetailItem.Movie(mov))
      }
      MediaType.CARTOON -> {
        val c = cartoonsList.find { it.id == item.id } ?: cartoonsList.first()
        openDetail(ActiveDetailItem.Cartoon(c))
      }
    }
  }

  fun onNotificationClick(notification: AppNotification) {
    notificationManager.markAsRead(notification.id)
    _isNotificationsOpen.value = false

    when (notification.targetType) {
      MediaType.SERIES -> {
        val series = seriesList.find { it.id == notification.targetId } ?: seriesList.first()
        openDetail(ActiveDetailItem.Series(series))
      }
      MediaType.MOVIE -> {
        val movie = moviesList.find { it.id == notification.targetId } ?: moviesList.first()
        openDetail(ActiveDetailItem.Movie(movie))
      }
      MediaType.MATCH -> {
        val match = matches.find { it.id == notification.targetId } ?: matches.first()
        openDetail(ActiveDetailItem.Match(match))
      }
      MediaType.CHANNEL -> {
        val channel = channels.find { it.id == notification.targetId } ?: channels.first()
        openDetail(ActiveDetailItem.Channel(channel))
      }
      MediaType.CARTOON -> {
        val cartoon = cartoonsList.find { it.id == notification.targetId } ?: cartoonsList.first()
        openDetail(ActiveDetailItem.Cartoon(cartoon))
      }
    }
  }

  fun markAllNotificationsAsRead() {
    notificationManager.markAllAsRead()
  }

  fun clearAllNotifications() {
    notificationManager.clearAll()
  }

  fun updateNotificationSettings(series: Boolean, movies: Boolean, matches: Boolean) {
    notificationManager.updateSettings(series, movies, matches)
  }

  fun simulateNewRelease(isMovie: Boolean) {
    if (isMovie) {
      val randomMovie = moviesList.random()
      notificationManager.sendReleaseNotification(
        title = "🍿 فيلم حصري جديد: ${randomMovie.title}",
        message = "متوفر الآن في سينما جوكر TV بجودة فائقة 1080p و 4K وسيرفرات سريعة!",
        targetType = MediaType.MOVIE,
        targetId = randomMovie.id,
        tag = "فيلم حصري"
      )
    } else {
      val randomSeries = seriesList.random()
      val randomEp = (1..randomSeries.episodesCount).random()
      notificationManager.sendReleaseNotification(
        title = "🎬 حلقة جديدة: ${randomSeries.title} (الحلقة $randomEp)",
        message = "تمت إضافة الحلقة $randomEp من مسلسل ${randomSeries.title} بسيرفرات مشاهدة سريعة!",
        targetType = MediaType.SERIES,
        targetId = randomSeries.id,
        tag = "حلقة جديدة"
      )
    }
  }

  fun addComment(itemId: String, author: String, rating: Int, text: String) {
    repository.addComment(itemId, author, rating, text)
  }

  fun recordWatchProgress(item: com.example.model.WatchHistoryItem) {
    repository.recordWatchProgress(item)
  }

  fun getResumePosition(contentId: String, episodeNumber: Int?): Long {
    return repository.getResumePosition(contentId, episodeNumber)
  }

  fun removeWatchHistoryItem(id: String) {
    repository.removeWatchHistoryItem(id)
  }

  fun clearWatchHistory() {
    repository.clearWatchHistory()
  }

  fun openWatchHistoryItem(item: com.example.model.WatchHistoryItem) {
    when (item.mediaType) {
      MediaType.SERIES -> {
        val series = seriesList.find { it.id == item.contentId } ?: seriesList.first()
        openDetail(ActiveDetailItem.Series(series, initialEpisodeNumber = item.episodeNumber ?: 1))
      }
      MediaType.MOVIE -> {
        val movie = moviesList.find { it.id == item.contentId } ?: moviesList.first()
        openDetail(ActiveDetailItem.Movie(movie))
      }
      MediaType.MATCH -> {
        val match = matches.find { it.id == item.contentId } ?: matches.first()
        openDetail(ActiveDetailItem.Match(match))
      }
      MediaType.CHANNEL -> {
        val channel = allChannels.value.find { it.id == item.contentId } ?: allChannels.value.first()
        openDetail(ActiveDetailItem.Channel(channel))
      }
      MediaType.CARTOON -> {
        val cartoon = cartoonsList.find { it.id == item.contentId } ?: cartoonsList.first()
        openDetail(ActiveDetailItem.Cartoon(cartoon, initialEpisodeNumber = item.episodeNumber ?: 1))
      }
    }
  }

  fun addCustomStreamChannel(name: String, streamUrl: String, openImmediately: Boolean = true): Boolean {
    if (!M3uPlaylistParser.isValidStreamUrl(streamUrl)) return false
    val channel = M3uPlaylistParser.createSingleCustomChannel(name = name, streamUrl = streamUrl)
    repository.addCustomChannels(listOf(channel))
    if (openImmediately) {
      openDetail(ActiveDetailItem.Channel(channel))
    }
    return true
  }

  suspend fun importM3uInput(rawInput: String, customTitle: String = ""): Result<Int> {
    val trimmed = rawInput.trim()
    if (trimmed.isBlank()) return Result.failure(IllegalArgumentException("أدخل رابط بث أو نص M3U صالح"))

    // 1. Direct #EXTM3U / #EXTINF playlist text pasted by user
    if (trimmed.contains("#EXTM3U", ignoreCase = true) || trimmed.contains("#EXTINF", ignoreCase = true)) {
      val parsed = M3uPlaylistParser.parseM3uContent(trimmed)
      return if (parsed.isNotEmpty()) {
        repository.addCustomChannels(parsed)
        Result.success(parsed.size)
      } else {
        Result.failure(IllegalStateException("لم يتم العثور على روابط صالحة داخل القائمة"))
      }
    }

    // 2. Single HTTP/HTTPS/RTMP/RTSP URL
    if (M3uPlaylistParser.isValidStreamUrl(trimmed)) {
      val lower = trimmed.lowercase()
      val looksLikePlaylistFile = lower.endsWith(".m3u") || lower.contains("type=m3u")
      if (looksLikePlaylistFile) {
        val fetched = M3uPlaylistParser.fetchPlaylistFromUrl(trimmed)
        if (fetched.isSuccess) {
          val text = fetched.getOrDefault("")
          if (text.contains("#EXTM3U", ignoreCase = true) || text.contains("#EXTINF", ignoreCase = true)) {
            val parsed = M3uPlaylistParser.parseM3uContent(text)
            if (parsed.isNotEmpty()) {
              repository.addCustomChannels(parsed)
              return Result.success(parsed.size)
            }
          }
        }
      }

      // Direct stream URL (.m3u8, .mp4, .ts, etc.)
      val channel = M3uPlaylistParser.createSingleCustomChannel(
        name = customTitle.ifBlank { "بث مباشر مخصص" },
        streamUrl = trimmed
      )
      repository.addCustomChannels(listOf(channel))
      return Result.success(1)
    }

    return Result.failure(IllegalArgumentException("صيغة الرابط غير مدعومة (يجب أن يبدأ بـ http:// أو https:// أو #EXTM3U)"))
  }

  fun removeCustomChannel(channelId: String) {
    repository.removeCustomChannel(channelId)
  }
}
