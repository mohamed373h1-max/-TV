package com.example.model

enum class MediaType {
  CHANNEL,
  MATCH,
  SERIES,
  MOVIE,
  CARTOON
}

data class ServerInfo(
  val id: String,
  val name: String,
  val quality: String, // "1080p FHD 60fps", "720p HD", "480p SD", "4K VIP"
  val streamUrl: String,
  val pingMs: Int = 28,
  val isVip: Boolean = false,
  val isOnline: Boolean = true
)

data class SportsChannel(
  val id: String,
  val name: String,
  val category: String, // "beIN Sports", "OnTime Sports", "SSC Sports", "Abu Dhabi Sports"
  val logoUrl: String,
  val isLive: Boolean = true,
  val currentEvent: String,
  val qualityBadge: String = "FHD",
  val servers: List<ServerInfo>
)

data class FootballMatch(
  val id: String,
  val homeTeam: String,
  val awayTeam: String,
  val homeLogo: String = "",
  val awayLogo: String = "",
  val homeScore: Int? = null,
  val awayScore: Int? = null,
  val championship: String, // "دوري أبطال أوروبا", "الدوري الإنجليزي الممتاز", "الدوري المصري"
  val timeText: String, // "21:00"
  val minute: String? = null, // "68'", "مباشر", "بين الشوطين", "انتهت", "لم تبدأ"
  val isLive: Boolean = false,
  val commentator: String,
  val channelName: String,
  val channelId: String,
  val stadium: String = "",
  val servers: List<ServerInfo>
)

data class Episode(
  val number: Int,
  val title: String,
  val duration: String = "45 دقيقة",
  val servers: List<ServerInfo>
)

data class RamadanSeries(
  val id: String,
  val title: String,
  val seasonYear: Int, // 2026, 2025, 2024
  val genre: String, // "دراما تشويق", "كوميديا", "أكشن وشعبي", "تاريخي"
  val posterUrl: String,
  val rating: Float,
  val ratingCount: Int,
  val director: String,
  val cast: List<String>,
  val synopsis: String,
  val episodesCount: Int,
  val episodes: List<Episode>
)

data class EgyptianMovie(
  val id: String,
  val title: String,
  val releaseYear: Int,
  val genre: String, // "كوميدي", "أكشن وإثارة", "دراما", "فانتازيا"
  val posterUrl: String,
  val rating: Float,
  val ratingCount: Int,
  val durationMinutes: Int,
  val director: String,
  val cast: List<String>,
  val synopsis: String,
  val boxOffice: String = "",
  val servers: List<ServerInfo>
)

data class CartoonItem(
  val id: String,
  val title: String,
  val originalTitle: String,
  val category: String, // "أنمي ياباني", "كرتون عالمي", "أفلام أنميشن"
  val dubbingLanguage: String = "مدبلج بالعربي",
  val dubbingStudio: String,
  val releaseYear: Int,
  val genre: String,
  val posterUrl: String,
  val rating: Float,
  val ratingCount: Int,
  val synopsis: String,
  val episodesCount: Int,
  val isMovie: Boolean = false,
  val ageRating: String = "عائلي • جميع الأعمار",
  val episodes: List<Episode>
)

data class UserComment(
  val id: String,
  val itemId: String,
  val authorName: String,
  val rating: Int, // 1 to 5
  val commentText: String,
  val timestampText: String,
  val likes: Int = 0
)

data class FavoriteItem(
  val id: String,
  val mediaType: MediaType,
  val title: String,
  val subtitle: String,
  val imageUrl: String,
  val badge: String,
  val rating: Float? = null
)

data class WatchHistoryItem(
  val id: String,
  val contentId: String,
  val mediaType: MediaType,
  val title: String,
  val subtitle: String,
  val imageUrl: String,
  val episodeNumber: Int? = null,
  val lastPositionMs: Long = 0L,
  val totalDurationMs: Long = 0L,
  val watchedAtTimestamp: Long = System.currentTimeMillis()
) {
  val progressFraction: Float
    get() = if (totalDurationMs > 0) (lastPositionMs.toFloat() / totalDurationMs).coerceIn(0f, 1f) else 0f

  val formattedPosition: String
    get() {
      val posSec = (lastPositionMs / 1000).toInt()
      val posMin = posSec / 60
      val posRemSec = posSec % 60
      return String.format("%02d:%02d", posMin, posRemSec)
    }

  val formattedDuration: String
    get() {
      val durSec = (totalDurationMs / 1000).toInt()
      val durMin = durSec / 60
      val durRemSec = durSec % 60
      return String.format("%02d:%02d", durMin, durRemSec)
    }
}
