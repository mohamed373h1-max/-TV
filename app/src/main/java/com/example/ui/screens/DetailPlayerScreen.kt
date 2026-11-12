package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.EgyptianMovie
import com.example.model.Episode
import com.example.model.FootballMatch
import com.example.model.RamadanSeries
import com.example.model.ServerInfo
import com.example.model.SportsChannel
import com.example.model.UserComment
import com.example.ui.components.JokerVideoPlayer
import com.example.ui.components.RatingAndCommentsSection
import com.example.ui.components.launchExternalVideoPlayer
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.JokerBackground
import com.example.ui.theme.JokerCyanAccent
import com.example.ui.theme.JokerGold
import com.example.ui.theme.JokerLiveRed
import com.example.ui.theme.JokerNeonGreen
import com.example.ui.theme.JokerPurpleLight
import com.example.ui.theme.JokerPurplePrimary
import com.example.ui.theme.JokerSurfaceElevated
import com.example.ui.theme.JokerSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

sealed class ActiveDetailItem {
  data class Channel(val channel: SportsChannel) : ActiveDetailItem()
  data class Match(val match: FootballMatch) : ActiveDetailItem()
  data class Series(val series: RamadanSeries, val initialEpisodeNumber: Int = 1) : ActiveDetailItem()
  data class Movie(val movie: EgyptianMovie) : ActiveDetailItem()
}

@Composable
fun DetailPlayerScreen(
  item: ActiveDetailItem,
  comments: List<UserComment>,
  isFavorite: Boolean,
  onToggleFavorite: () -> Unit,
  onAddComment: (author: String, rating: Int, text: String) -> Unit,
  onBack: () -> Unit,
  initialResumePosition: Long = 0L,
  onRecordProgress: (com.example.model.WatchHistoryItem) -> Unit = {},
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }
  val context = LocalContext.current

  // Extract common metadata based on item type
  val title = when (item) {
    is ActiveDetailItem.Channel -> item.channel.name
    is ActiveDetailItem.Match -> "${item.match.homeTeam} ضد ${item.match.awayTeam}"
    is ActiveDetailItem.Series -> item.series.title
    is ActiveDetailItem.Movie -> item.movie.title
  }

  val itemId = when (item) {
    is ActiveDetailItem.Channel -> item.channel.id
    is ActiveDetailItem.Match -> item.match.id
    is ActiveDetailItem.Series -> item.series.id
    is ActiveDetailItem.Movie -> item.movie.id
  }

  val defaultEpisodeNum = if (item is ActiveDetailItem.Series) item.initialEpisodeNumber else 1
  var selectedEpisodeNumber by remember(item) { mutableIntStateOf(defaultEpisodeNum) }
  var episodeRangeFilter by remember { mutableStateOf("الحلقات 1 - 10") }
  var autoLaunchPhonePlayer by remember { mutableStateOf(false) }
  var activeResumePosition by remember(item, selectedEpisodeNumber) { mutableStateOf(initialResumePosition) }
  var showResumeBanner by remember(item, selectedEpisodeNumber) { mutableStateOf(initialResumePosition > 3000L) }

  val defaultServers = when (item) {
    is ActiveDetailItem.Channel -> item.channel.servers
    is ActiveDetailItem.Match -> item.match.servers
    is ActiveDetailItem.Series -> item.series.episodes.getOrNull(selectedEpisodeNumber - 1)?.servers ?: item.series.episodes.first().servers
    is ActiveDetailItem.Movie -> item.movie.servers
  }

  var selectedServerIndex by remember(item) { mutableIntStateOf(0) }
  val selectedServer = defaultServers.getOrElse(selectedServerIndex) { defaultServers.first() }

  val isLive = when (item) {
    is ActiveDetailItem.Channel -> item.channel.isLive
    is ActiveDetailItem.Match -> item.match.isLive
    is ActiveDetailItem.Series -> false
    is ActiveDetailItem.Movie -> false
  }

  val currentSubtitle = when (item) {
    is ActiveDetailItem.Channel -> item.channel.currentEvent
    is ActiveDetailItem.Match -> "${item.match.championship} • معلق: ${item.match.commentator}"
    is ActiveDetailItem.Series -> "الحلقة $selectedEpisodeNumber من ${item.series.episodesCount}"
    is ActiveDetailItem.Movie -> "سينما ${item.movie.releaseYear} • ${item.movie.durationMinutes} دقيقة"
  }

  // Auto-launch into user's phone player (VLC / MX Player) whenever an item or episode is chosen
  LaunchedEffect(item, selectedEpisodeNumber) {
    if (autoLaunchPhonePlayer) {
      launchExternalVideoPlayer(
        context = context,
        videoUrl = selectedServer.streamUrl,
        title = if (item is ActiveDetailItem.Series) "$title - الحلقة $selectedEpisodeNumber" else title,
        resumePositionMs = activeResumePosition
      )
    }
  }

  val averageRating = when (item) {
    is ActiveDetailItem.Channel -> 4.9f
    is ActiveDetailItem.Match -> 4.8f
    is ActiveDetailItem.Series -> item.series.rating
    is ActiveDetailItem.Movie -> item.movie.rating
  }

  val ratingCount = when (item) {
    is ActiveDetailItem.Channel -> 8400
    is ActiveDetailItem.Match -> 6200
    is ActiveDetailItem.Series -> item.series.ratingCount
    is ActiveDetailItem.Movie -> item.movie.ratingCount
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(JokerBackground),
    contentPadding = PaddingValues(bottom = 36.dp)
  ) {
    // 1. Top Bar with back button
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "رجوع",
            tint = TextPrimary
          )
        }

        Text(
          text = title,
          color = TextPrimary,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
        )

        IconButton(onClick = onToggleFavorite) {
          Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "المفضلة",
            tint = if (isFavorite) JokerLiveRed else TextPrimary
          )
        }
      }
    }

    // 2. Built-in Video Player (Primary, immediate playback)
    item {
      val seriesTotalEpisodes = if (item is ActiveDetailItem.Series) item.series.episodesCount else 0

      JokerVideoPlayer(
        title = title,
        subtitle = currentSubtitle,
        servers = defaultServers,
        selectedServer = selectedServer,
        onServerSelected = { srv ->
          val idx = defaultServers.indexOfFirst { it.id == srv.id }
          if (idx >= 0) selectedServerIndex = idx
        },
        isLiveStream = isLive,
        initialPositionMs = activeResumePosition,
        onProgressUpdate = { posMs, durMs ->
          val historyItem = com.example.model.WatchHistoryItem(
            id = when (item) {
              is ActiveDetailItem.Series -> "${item.series.id}_ep_$selectedEpisodeNumber"
              is ActiveDetailItem.Movie -> "${item.movie.id}_ep_0"
              is ActiveDetailItem.Channel -> "${item.channel.id}_ep_0"
              is ActiveDetailItem.Match -> "${item.match.id}_ep_0"
            },
            contentId = itemId,
            mediaType = when (item) {
              is ActiveDetailItem.Series -> com.example.model.MediaType.SERIES
              is ActiveDetailItem.Movie -> com.example.model.MediaType.MOVIE
              is ActiveDetailItem.Channel -> com.example.model.MediaType.CHANNEL
              is ActiveDetailItem.Match -> com.example.model.MediaType.MATCH
            },
            title = title,
            subtitle = if (item is ActiveDetailItem.Series) "الحلقة $selectedEpisodeNumber من ${item.series.episodesCount}" else currentSubtitle,
            imageUrl = when (item) {
              is ActiveDetailItem.Series -> item.series.posterUrl
              is ActiveDetailItem.Movie -> item.movie.posterUrl
              is ActiveDetailItem.Channel -> item.channel.logoUrl
              is ActiveDetailItem.Match -> item.match.homeLogo
            },
            episodeNumber = if (item is ActiveDetailItem.Series) selectedEpisodeNumber else null,
            lastPositionMs = posMs,
            totalDurationMs = durMs,
            watchedAtTimestamp = System.currentTimeMillis()
          )
          onRecordProgress(historyItem)
        },
        hasNextEpisode = item is ActiveDetailItem.Series && selectedEpisodeNumber < seriesTotalEpisodes,
        hasPreviousEpisode = item is ActiveDetailItem.Series && selectedEpisodeNumber > 1,
        onNextEpisode = if (item is ActiveDetailItem.Series && selectedEpisodeNumber < seriesTotalEpisodes) {
          { selectedEpisodeNumber++ }
        } else null,
        onPreviousEpisode = if (item is ActiveDetailItem.Series && selectedEpisodeNumber > 1) {
          { selectedEpisodeNumber-- }
        } else null
      )
    }

    // 2.5 Real Match Scoreboard & Live Status Header (if Match)
    if (item is ActiveDetailItem.Match) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
          colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.match.isLive) JokerPurplePrimary else BorderSubtle
          )
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            // Championship & Live Minute Pill
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                Icon(
                  imageVector = Icons.Default.SportsSoccer,
                  contentDescription = null,
                  tint = JokerCyanAccent,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = item.match.championship,
                  color = TextSecondary,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              if (item.match.isLive) {
                Surface(
                  color = JokerLiveRed,
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text(
                    text = item.match.minute ?: "مباشر",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }
              } else {
                Surface(
                  color = JokerSurfaceElevated,
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text(
                    text = item.match.timeText,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Teams and Score Row with authentic Club Logos
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              // Home Team
              Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Box(
                  modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(JokerSurfaceElevated)
                    .padding(5.dp),
                  contentAlignment = Alignment.Center
                ) {
                  if (item.match.homeLogo.isNotBlank()) {
                    AsyncImage(
                      model = ImageRequest.Builder(LocalContext.current)
                        .data(item.match.homeLogo)
                        .crossfade(true)
                        .build(),
                      contentDescription = item.match.homeTeam,
                      contentScale = ContentScale.Fit,
                      modifier = Modifier.fillMaxSize()
                    )
                  } else {
                    Text(
                      text = item.match.homeTeam.take(1),
                      color = TextPrimary,
                      fontWeight = FontWeight.Bold,
                      fontSize = 18.sp
                    )
                  }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = item.match.homeTeam,
                  color = TextPrimary,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  textAlign = TextAlign.Center,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              // Live Score / VS Box
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 10.dp)
              ) {
                if (item.match.homeScore != null && item.match.awayScore != null) {
                  Surface(
                    color = JokerSurfaceElevated,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JokerGold.copy(alpha = 0.5f))
                  ) {
                    Text(
                      text = "${item.match.homeScore} - ${item.match.awayScore}",
                      color = JokerGold,
                      fontSize = 24.sp,
                      fontWeight = FontWeight.Black,
                      modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                  }
                } else {
                  Surface(
                    color = JokerSurfaceElevated,
                    shape = RoundedCornerShape(10.dp)
                  ) {
                    Text(
                      text = "ضد",
                      color = TextSecondary,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                  }
                }
              }

              // Away Team
              Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Box(
                  modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(JokerSurfaceElevated)
                    .padding(5.dp),
                  contentAlignment = Alignment.Center
                ) {
                  if (item.match.awayLogo.isNotBlank()) {
                    AsyncImage(
                      model = ImageRequest.Builder(LocalContext.current)
                        .data(item.match.awayLogo)
                        .crossfade(true)
                        .build(),
                      contentDescription = item.match.awayTeam,
                      contentScale = ContentScale.Fit,
                      modifier = Modifier.fillMaxSize()
                    )
                  } else {
                    Text(
                      text = item.match.awayTeam.take(1),
                      color = TextPrimary,
                      fontWeight = FontWeight.Bold,
                      fontSize = 18.sp
                    )
                  }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = item.match.awayTeam,
                  color = TextPrimary,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  textAlign = TextAlign.Center,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stadium & Commentator & Channel
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "🏟️ ${item.match.stadium}",
                color = TextSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
              )
              Surface(
                color = JokerPurplePrimary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "🎙️ ${item.match.commentator}",
                  color = JokerPurpleLight,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }
          }
        }
      }
    }

    // 2.7 Resume Playback Banner
    if (showResumeBanner && activeResumePosition > 3000L) {
      item {
        val posSec = (activeResumePosition / 1000).toInt()
        val posMin = posSec / 60
        val posRemSec = posSec % 60
        val formattedTime = String.format("%02d:%02d", posMin, posRemSec)

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
          colors = CardDefaults.cardColors(containerColor = JokerPurplePrimary.copy(alpha = 0.2f)),
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, JokerPurpleLight)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(34.dp)
                  .clip(CircleShape)
                  .background(JokerPurplePrimary),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.PlayArrow,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "استكمال المشاهدة من حيث توقفت",
                  color = TextPrimary,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "آخر نقطة مشاهدة كانت عند $formattedTime",
                  color = JokerCyanAccent,
                  fontSize = 11.sp
                )
              }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Button(
                onClick = {
                  showResumeBanner = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = JokerPurplePrimary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text(text = "متابعة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = {
                  activeResumePosition = 0L
                  showResumeBanner = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = JokerSurfaceElevated),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(text = "من البداية", color = TextSecondary, fontSize = 11.sp)
              }
            }
          }
        }
      }
    }

    // 2.8 Optional External Player (VLC / MX Player) Launch Option
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(JokerCyanAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.SmartDisplay,
                  contentDescription = null,
                  tint = JokerCyanAccent,
                  modifier = Modifier.size(18.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "التشغيل في مشغل هاتفك المفضل",
                  color = TextPrimary,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "VLC أو MX Player أو أي مشغل خارجي",
                  color = TextSecondary,
                  fontSize = 10.sp
                )
              }
            }

            Button(
              onClick = {
                launchExternalVideoPlayer(
                  context = context,
                  videoUrl = selectedServer.streamUrl,
                  title = if (item is ActiveDetailItem.Series) "$title - الحلقة $selectedEpisodeNumber" else title,
                  resumePositionMs = activeResumePosition
                )
              },
              colors = ButtonDefaults.buttonColors(containerColor = JokerCyanAccent),
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.PlayCircleFilled,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "فتح في هاتفي",
                color = Color.Black,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    // 4. Server Switcher Bar
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Dns,
                contentDescription = null,
                tint = JokerPurpleLight,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "سيرفرات البث القوية (مضادة للتقطيع)",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Surface(
              color = JokerNeonGreen.copy(alpha = 0.2f),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "سيرفر نشط ${selectedServer.pingMs}ms",
                color = JokerNeonGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(defaultServers.indices.toList()) { index ->
              val server = defaultServers[index]
              val isSelected = index == selectedServerIndex
              Surface(
                color = if (isSelected) JokerPurplePrimary else JokerSurfaceElevated,
                shape = RoundedCornerShape(12.dp),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, JokerPurpleLight) else null,
                modifier = Modifier.clickable {
                  selectedServerIndex = index
                  if (autoLaunchPhonePlayer) {
                    launchExternalVideoPlayer(
                      context = context,
                      videoUrl = server.streamUrl,
                      title = if (item is ActiveDetailItem.Series) "$title - الحلقة $selectedEpisodeNumber" else title
                    )
                  }
                }
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(7.dp)
                      .clip(CircleShape)
                      .background(if (server.pingMs < 30) JokerNeonGreen else JokerGold)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text(
                      text = server.name,
                      color = if (isSelected) Color.White else TextPrimary,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                    Text(
                      text = server.quality,
                      color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextSecondary,
                      fontSize = 9.sp
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // 5. Series Episodes Complete Library (All 30 Episodes with one-click phone player launch)
    if (item is ActiveDetailItem.Series) {
      item {
        val totalCount = item.series.episodesCount
        val rangeFilters = when {
          totalCount > 20 -> listOf("الحلقات 1 - 10", "الحلقات 11 - 20", "الحلقات 21 - $totalCount", "عرض كل الحلقات")
          totalCount > 10 -> listOf("الحلقات 1 - 10", "الحلقات 11 - $totalCount", "عرض كل الحلقات")
          else -> listOf("عرض كل الحلقات")
        }

        val filteredEpisodes = remember(episodeRangeFilter, selectedEpisodeNumber, item.series.episodes) {
          when (episodeRangeFilter) {
            "الحلقات 1 - 10" -> item.series.episodes.filter { it.number in 1..10 }
            "الحلقات 11 - 20" -> item.series.episodes.filter { it.number in 11..20 }
            "الحلقات 21 - $totalCount" -> item.series.episodes.filter { it.number in 21..totalCount }
            else -> item.series.episodes
          }
        }

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
          // Episodes header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "جميع الحلقات كاملة",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = JokerPurplePrimary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "$totalCount حلقة متوفرة",
                  color = JokerPurpleLight,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            // Next / Prev Episode quick switcher
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (selectedEpisodeNumber > 1) {
                IconButton(
                  onClick = { selectedEpisodeNumber-- },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(Icons.Default.SkipPrevious, contentDescription = "السابقة", tint = TextSecondary)
                }
              }
              Text(
                text = "$selectedEpisodeNumber / $totalCount",
                color = JokerGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
              if (selectedEpisodeNumber < totalCount) {
                IconButton(
                  onClick = { selectedEpisodeNumber++ },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(Icons.Default.SkipNext, contentDescription = "التالية", tint = TextSecondary)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Range filter pills
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(rangeFilters) { filter ->
              val isSelected = filter == episodeRangeFilter
              Surface(
                color = if (isSelected) JokerPurplePrimary else JokerSurfaceElevated,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.clickable { episodeRangeFilter = filter }
              ) {
                Text(
                  text = filter,
                  color = if (isSelected) Color.White else TextSecondary,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Episodes List / Cards: Clicking any episode launches phone's external video player
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            filteredEpisodes.forEach { ep ->
              val isCurrent = ep.number == selectedEpisodeNumber
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    selectedEpisodeNumber = ep.number
                    activeResumePosition = 0L
                    if (autoLaunchPhonePlayer) {
                      launchExternalVideoPlayer(
                        context = context,
                        videoUrl = ep.servers.first().streamUrl,
                        title = "$title - ${ep.title}"
                      )
                    }
                  },
                colors = CardDefaults.cardColors(
                  containerColor = if (isCurrent) JokerPurplePrimary.copy(alpha = 0.25f) else JokerSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isCurrent) JokerPurplePrimary else BorderSubtle
                )
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isCurrent) JokerPurplePrimary else JokerSurfaceElevated),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = if (isCurrent) Color.White else JokerCyanAccent,
                        modifier = Modifier.size(20.dp)
                      )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                      Text(
                        text = ep.title,
                        color = if (isCurrent) JokerPurpleLight else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                      )
                      Spacer(modifier = Modifier.height(2.dp))
                      Text(
                        text = "${ep.duration} • اضغط للتشغيل في مشغل هاتفك",
                        color = JokerCyanAccent,
                        fontSize = 10.sp
                      )
                    }
                  }

                  Surface(
                    color = JokerCyanAccent.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JokerCyanAccent),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable {
                      selectedEpisodeNumber = ep.number
                      launchExternalVideoPlayer(
                        context = context,
                        videoUrl = ep.servers.first().streamUrl,
                        title = "$title - ${ep.title}"
                      )
                    }
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = JokerCyanAccent,
                        modifier = Modifier.size(12.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = "مشغل الهاتف",
                        color = JokerCyanAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // 6. Synopsis & Info Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          when (item) {
            is ActiveDetailItem.Series -> {
              Text(
                text = "قصة المسلسل",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = item.series.synopsis,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 20.sp
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "إخراج: ${item.series.director}",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "طاقم العمل: ${item.series.cast.joinToString(" • ")}",
                color = TextSecondary,
                fontSize = 12.sp
              )
            }
            is ActiveDetailItem.Movie -> {
              Text(
                text = "قصة الفيلم",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = item.movie.synopsis,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 20.sp
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "إخراج: ${item.movie.director} | شباك التذاكر: ${item.movie.boxOffice}",
                color = JokerCyanAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "أبطال العمل: ${item.movie.cast.joinToString(" • ")}",
                color = TextSecondary,
                fontSize = 12.sp
              )
            }
            is ActiveDetailItem.Match -> {
              Text(
                text = "تفاصيل المباراة والملعب",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "الملعب: ${item.match.stadium} • البطولة: ${item.match.championship} • المعلق: ${item.match.commentator} • القناة الناقلة: ${item.match.channelName}",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
              )
            }
            is ActiveDetailItem.Channel -> {
              Text(
                text = "معلومات القناة",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "بث مباشر مستمر بتقنية FHD على مدار 24 ساعة لجميع مباريات البطولات العالمية والأفريقية والعربية الكبرى مع إمكانية التبديل بين 4 سيرفرات فائقة السرعة.",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
              )
            }
          }
        }
      }
    }

    // 7. Interactive 5-Star Ratings & Comments Section
    item {
      RatingAndCommentsSection(
        itemId = itemId,
        averageRating = averageRating,
        ratingCount = ratingCount,
        comments = comments,
        onAddComment = onAddComment
      )
    }
  }
}
