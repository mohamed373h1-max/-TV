package com.example.data

import com.example.model.MediaType
import com.example.model.ServerInfo
import com.example.model.SportsChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object M3uPlaylistParser {

  private const val DEFAULT_CUSTOM_LOGO =
    "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=400&auto=format&fit=crop&q=80"

  fun isValidStreamUrl(url: String): Boolean {
    val trimmed = url.trim().lowercase()
    return trimmed.startsWith("http://") ||
      trimmed.startsWith("https://") ||
      trimmed.startsWith("rtmp://") ||
      trimmed.startsWith("rtsp://")
  }

  fun parseM3uContent(
    content: String,
    defaultCategory: String = "قنواتك الخاصة"
  ): List<SportsChannel> {
    val lines = content.lines().map { it.trim() }.filter { it.isNotEmpty() }
    val results = mutableListOf<SportsChannel>()

    var pendingName: String? = null
    var pendingLogo: String? = null
    var pendingGroup: String? = null

    lines.forEachIndexed { index, line ->
      when {
        line.startsWith("#EXTM3U", ignoreCase = true) -> Unit
        line.startsWith("#EXTINF", ignoreCase = true) -> {
          pendingLogo = extractAttribute(line, "tvg-logo")
          pendingGroup = extractAttribute(line, "group-title")
          val commaIdx = line.lastIndexOf(',')
          pendingName = if (commaIdx >= 0 && commaIdx < line.length - 1) {
            line.substring(commaIdx + 1).trim()
          } else {
            null
          }
        }
        line.startsWith("#") -> Unit
        isValidStreamUrl(line) -> {
          val channelIndex = results.size + 1
          val channelName = pendingName?.takeIf { it.isNotBlank() } ?: "قناة بث مباشر $channelIndex"
          val category = pendingGroup?.takeIf { it.isNotBlank() } ?: defaultCategory
          val logo = pendingLogo?.takeIf { it.startsWith("http") } ?: DEFAULT_CUSTOM_LOGO
          val id = "custom_${System.currentTimeMillis()}_$channelIndex"

          val fallbackServers = JokerTvDataProvider.createDefaultServers(
            contentId = id,
            streamIndexOffset = index,
            mediaType = MediaType.CHANNEL
          )

          val primaryServer = ServerInfo(
            id = "${id}_user_primary",
            name = "رابط البث المضاف (مباشر)",
            quality = detectQualityFromUrl(line),
            streamUrl = line,
            pingMs = 15,
            isVip = true,
            isOnline = true
          )

          results.add(
            SportsChannel(
              id = id,
              name = channelName,
              category = category,
              logoUrl = logo,
              isLive = true,
              currentEvent = "بث IPTV مضاف بواسطة المستخدم",
              qualityBadge = detectQualityFromUrl(line),
              servers = listOf(primaryServer) + fallbackServers.take(3)
            )
          )

          pendingName = null
          pendingLogo = null
          pendingGroup = null
        }
      }
    }

    return results
  }

  fun createSingleCustomChannel(
    name: String,
    streamUrl: String,
    category: String = "قنواتك الخاصة"
  ): SportsChannel {
    val cleanUrl = streamUrl.trim()
    val cleanName = name.trim().ifBlank { "بث مباشر مخصص" }
    val id = "custom_${System.currentTimeMillis()}"
    val fallbackServers = JokerTvDataProvider.createDefaultServers(
      contentId = id,
      streamIndexOffset = 0,
      mediaType = MediaType.CHANNEL
    )
    val userServer = ServerInfo(
      id = "${id}_user_primary",
      name = "رابط البث المضاف ($cleanName)",
      quality = detectQualityFromUrl(cleanUrl),
      streamUrl = cleanUrl,
      pingMs = 14,
      isVip = true,
      isOnline = true
    )

    return SportsChannel(
      id = id,
      name = cleanName,
      category = category,
      logoUrl = DEFAULT_CUSTOM_LOGO,
      isLive = true,
      currentEvent = "بث مباشر عبر رابطك الخاص",
      qualityBadge = detectQualityFromUrl(cleanUrl),
      servers = listOf(userServer) + fallbackServers.take(3)
    )
  }

  suspend fun fetchPlaylistFromUrl(playlistUrl: String): Result<String> = withContext(Dispatchers.IO) {
    runCatching {
      val connection = (URL(playlistUrl.trim()).openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        connectTimeout = 7000
        readTimeout = 8000
        instanceFollowRedirects = true
        setRequestProperty("User-Agent", "JokerTV/2.0 (Android)")
      }
      try {
        val code = connection.responseCode
        if (code !in 200..299) {
          throw IllegalStateException("HTTP $code")
        }
        connection.inputStream.bufferedReader().use { it.readText() }
      } finally {
        connection.disconnect()
      }
    }
  }

  private fun extractAttribute(line: String, attrName: String): String? {
    val regex = Regex("""$attrName\s*=\s*"([^"]*)"""", RegexOption.IGNORE_CASE)
    return regex.find(line)?.groupValues?.getOrNull(1)?.trim()
  }

  private fun detectQualityFromUrl(url: String): String {
    val lower = url.lowercase()
    return when {
      lower.contains("4k") || lower.contains("2160") -> "4K"
      lower.contains("1080") || lower.contains("fhd") -> "FHD 1080p"
      lower.contains("720") || lower.contains("hd") -> "HD 720p"
      lower.contains(".m3u8") -> "HLS Live"
      else -> "IPTV"
    }
  }
}
