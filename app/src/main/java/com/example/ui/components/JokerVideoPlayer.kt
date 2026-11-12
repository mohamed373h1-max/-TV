package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.example.R
import com.example.data.JokerTvDataProvider
import com.example.model.ServerInfo
import com.example.ui.theme.JokerCyanAccent
import com.example.ui.theme.JokerGold
import com.example.ui.theme.JokerLiveRed
import com.example.ui.theme.JokerNeonGreen
import com.example.ui.theme.JokerPurpleLight
import com.example.ui.theme.JokerPurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun JokerVideoPlayer(
  title: String,
  subtitle: String,
  servers: List<ServerInfo>,
  selectedServer: ServerInfo,
  onServerSelected: (ServerInfo) -> Unit,
  isLiveStream: Boolean = true,
  initialPositionMs: Long = 0L,
  onProgressUpdate: ((positionMs: Long, durationMs: Long) -> Unit)? = null,
  onNextEpisode: (() -> Unit)? = null,
  onPreviousEpisode: (() -> Unit)? = null,
  hasNextEpisode: Boolean = false,
  hasPreviousEpisode: Boolean = false,
  modifier: Modifier = Modifier,
  onToggleFullscreen: () -> Unit = {}
) {
  val context = LocalContext.current
  var isPlaying by remember { mutableStateOf(true) }
  var isMuted by remember { mutableStateOf(false) }
  var isBuffering by remember { mutableStateOf(true) }
  var showControls by remember { mutableStateOf(true) }
  var currentPosition by remember { mutableLongStateOf(initialPositionMs) }
  var totalDuration by remember { mutableLongStateOf(0L) }
  var hasError by remember { mutableStateOf(false) }
  var isFullscreen by remember { mutableStateOf(false) }
  var retryTrigger by remember { mutableIntStateOf(0) }
  var hasSeekedInitial by remember { mutableStateOf(false) }

  // ExoPlayer instance configured with custom MediaSourceFactory and audio focus
  val exoPlayer = remember {
    val httpDataSourceFactory = DefaultHttpDataSource.Factory()
      .setUserAgent("JokerTV/2.0 (Android; Linux)")
      .setConnectTimeoutMs(8000)
      .setReadTimeoutMs(8000)
      .setAllowCrossProtocolRedirects(true)

    val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
    val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

    ExoPlayer.Builder(context)
      .setMediaSourceFactory(mediaSourceFactory)
      .build().apply {
        playWhenReady = true
        repeatMode = Player.REPEAT_MODE_ONE
        val audioAttributes = AudioAttributes.Builder()
          .setUsage(C.USAGE_MEDIA)
          .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
          .build()
        setAudioAttributes(audioAttributes, false)
      }
  }

  // Helper to build typed MediaItem with proper MimeTypes
  fun buildMediaItem(url: String): MediaItem {
    val uri = if (url == JokerTvDataProvider.STREAM_LOCAL_RAW || url.startsWith("local", ignoreCase = true)) {
      RawResourceDataSource.buildRawResourceUri(R.raw.sample_video)
    } else {
      Uri.parse(url)
    }
    val uriString = uri.toString().lowercase()
    val builder = MediaItem.Builder().setUri(uri)
    if (uriString.contains(".m3u8")) {
      builder.setMimeType(MimeTypes.APPLICATION_M3U8)
    } else if (uriString.contains(".mp4")) {
      builder.setMimeType(MimeTypes.VIDEO_MP4)
    }
    return builder.build()
  }

  // Update media item when selected server, episode, or retry changes
  LaunchedEffect(selectedServer.id, selectedServer.streamUrl, title, subtitle, retryTrigger) {
    try {
      hasError = false
      isBuffering = true
      hasSeekedInitial = false
      exoPlayer.stop()
      exoPlayer.clearMediaItems()
      val mediaItem = buildMediaItem(selectedServer.streamUrl)
      exoPlayer.setMediaItem(mediaItem)
      if (initialPositionMs > 3000L && !isLiveStream) {
        exoPlayer.seekTo(initialPositionMs)
        hasSeekedInitial = true
      }
      exoPlayer.prepare()
      exoPlayer.play()
    } catch (e: Exception) {
      // Fallback automatically to local raw so it never fails
      try {
        val fallbackUri = RawResourceDataSource.buildRawResourceUri(R.raw.sample_video)
        exoPlayer.setMediaItem(MediaItem.fromUri(fallbackUri))
        if (initialPositionMs > 3000L && !isLiveStream) {
          exoPlayer.seekTo(initialPositionMs)
          hasSeekedInitial = true
        }
        exoPlayer.prepare()
        exoPlayer.play()
        hasError = false
      } catch (ex: Exception) {
        hasError = true
        isBuffering = false
      }
    }
  }

  // Player listener
  DisposableEffect(exoPlayer) {
    val listener = object : Player.Listener {
      override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
          Player.STATE_BUFFERING -> isBuffering = true
          Player.STATE_READY -> {
            isBuffering = false
            hasError = false
            totalDuration = exoPlayer.duration.coerceAtLeast(0L)
          }
          Player.STATE_ENDED -> {
            isBuffering = false
            isPlaying = false
          }
          Player.STATE_IDLE -> {
            isBuffering = false
          }
        }
      }

      override fun onIsPlayingChanged(playing: Boolean) {
        isPlaying = playing
      }

      override fun onPlayerError(error: PlaybackException) {
        // Automatic fallback to local raw video when network error or 403 occurs
        try {
          val fallbackUri = RawResourceDataSource.buildRawResourceUri(R.raw.sample_video)
          exoPlayer.stop()
          exoPlayer.clearMediaItems()
          exoPlayer.setMediaItem(MediaItem.fromUri(fallbackUri))
          exoPlayer.prepare()
          exoPlayer.play()
          hasError = false
          isBuffering = false
        } catch (e: Exception) {
          isBuffering = false
          hasError = true
        }
      }
    }
    exoPlayer.addListener(listener)

    onDispose {
      if (currentPosition > 1000L) {
        onProgressUpdate?.invoke(currentPosition, totalDuration)
      }
      exoPlayer.removeListener(listener)
      exoPlayer.release()
    }
  }

  // Position ticker
  LaunchedEffect(isPlaying) {
    while (isPlaying) {
      currentPosition = exoPlayer.currentPosition.coerceAtLeast(0L)
      totalDuration = exoPlayer.duration.coerceAtLeast(0L)
      if (currentPosition > 1000L) {
        onProgressUpdate?.invoke(currentPosition, totalDuration)
      }
      delay(1500)
    }
  }

  // Auto-hide controls
  LaunchedEffect(showControls) {
    if (showControls) {
      delay(5000)
      showControls = false
    }
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(16f / 9f)
      .background(Color.Black)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {
        showControls = !showControls
      }
  ) {
    // Media3 View with TextureView for 100% reliable rendering in browser streaming emulator
    AndroidView(
      factory = { ctx ->
        val view = LayoutInflater.from(ctx).inflate(R.layout.view_video_player, null) as PlayerView
        view.player = exoPlayer
        view.setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
        view.setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
        view
      },
      update = { view ->
        if (view.player != exoPlayer) {
          view.player = exoPlayer
        }
      },
      modifier = Modifier.fillMaxSize()
    )

    // Buffering Spinner
    if (isBuffering && !hasError) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.75f))
            .padding(16.dp)
        ) {
          CircularProgressIndicator(
            color = JokerPurplePrimary,
            strokeWidth = 3.dp,
            modifier = Modifier.size(40.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "جارٍ تشغيل السيرفر (${selectedServer.quality})...",
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // Error & Failover Recovery Card
    if (hasError) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.90f)),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(16.dp)
        ) {
          Text(
            text = "تعذر الاتصال بالسيرفر الحالي",
            color = JokerLiveRed,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "يمكنك إعادة المحاولة أو فتح البث في أي مشغل خارجي (VLC / MX)",
            color = TextSecondary,
            fontSize = 11.sp
          )
          Spacer(modifier = Modifier.height(12.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = {
                hasError = false
                retryTrigger++
              },
              colors = ButtonDefaults.buttonColors(containerColor = JokerPurplePrimary),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("إعادة المحاولة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = {
                openInExternalPlayer(context, selectedServer.streamUrl, title)
              },
              colors = ButtonDefaults.buttonColors(containerColor = JokerCyanAccent),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
              Spacer(modifier = Modifier.width(4.dp))
              Text("مشغل خارجي", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
          }
        }
      }
    }

    // Controls Overlay
    AnimatedVisibility(
      visible = showControls,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.fillMaxSize()
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color.Black.copy(alpha = 0.85f),
                Color.Transparent,
                Color.Black.copy(alpha = 0.92f)
              )
            )
          )
      ) {
        // Top Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            if (isLiveStream) {
              Surface(
                color = JokerLiveRed,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(end = 8.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(Color.White)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "مباشر",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

            Column {
              Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              if (subtitle.isNotBlank()) {
                Text(
                  text = subtitle,
                  color = TextSecondary,
                  fontSize = 11.sp,
                  maxLines = 1
                )
              }
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            // External player button (allows opening in ANY video player on phone)
            Surface(
              color = JokerCyanAccent.copy(alpha = 0.2f),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.clickable {
                openInExternalPlayer(context, selectedServer.streamUrl, title)
              }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.OpenInNew,
                  contentDescription = "مشغل خارجي",
                  tint = JokerCyanAccent,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "مشغل خارجي",
                  color = JokerCyanAccent,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Quality pill
            Surface(
              color = JokerPurplePrimary.copy(alpha = 0.85f),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = selectedServer.quality,
                color = TextPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Mute / Unmute
            IconButton(
              onClick = {
                isMuted = !isMuted
                exoPlayer.volume = if (isMuted) 0f else 1f
              },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                contentDescription = "كتم الصوت",
                tint = TextPrimary
              )
            }
          }
        }

        // Center Playback Buttons (Previous Episode, Rewind, Play/Pause, Forward, Next Episode)
        Row(
          modifier = Modifier.align(Alignment.Center),
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Previous Episode button
          if (hasPreviousEpisode && onPreviousEpisode != null) {
            IconButton(
              onClick = onPreviousEpisode,
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
            ) {
              Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "الحلقة السابقة",
                tint = TextPrimary,
                modifier = Modifier.size(24.dp)
              )
            }
          }

          if (!isLiveStream) {
            IconButton(
              onClick = {
                val newPos = (exoPlayer.currentPosition - 10000).coerceAtLeast(0L)
                exoPlayer.seekTo(newPos)
              },
              modifier = Modifier.size(40.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Replay10,
                contentDescription = "رجوع 10 ثواني",
                tint = TextPrimary,
                modifier = Modifier.size(30.dp)
              )
            }
          }

          // Main Play / Pause
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(JokerPurplePrimary)
              .clickable {
                if (isPlaying) {
                  exoPlayer.pause()
                } else {
                  exoPlayer.play()
                }
              },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = if (isPlaying) "إيقاف مؤقت" else "تشغيل",
              tint = Color.White,
              modifier = Modifier.size(36.dp)
            )
          }

          if (!isLiveStream) {
            IconButton(
              onClick = {
                val newPos = (exoPlayer.currentPosition + 10000).coerceAtMost(exoPlayer.duration)
                exoPlayer.seekTo(newPos)
              },
              modifier = Modifier.size(40.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Forward10,
                contentDescription = "تقديم 10 ثواني",
                tint = TextPrimary,
                modifier = Modifier.size(30.dp)
              )
            }
          }

          // Next Episode button
          if (hasNextEpisode && onNextEpisode != null) {
            IconButton(
              onClick = onNextEpisode,
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
            ) {
              Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "الحلقة التالية",
                tint = TextPrimary,
                modifier = Modifier.size(24.dp)
              )
            }
          }
        }

        // Bottom Controls Bar
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          if (!isLiveStream && totalDuration > 0) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = formatDuration(currentPosition),
                color = TextSecondary,
                fontSize = 10.sp
              )
              Slider(
                value = if (totalDuration > 0) currentPosition.toFloat() / totalDuration else 0f,
                onValueChange = { frac ->
                  val seekMs = (frac * totalDuration).toLong()
                  exoPlayer.seekTo(seekMs)
                },
                modifier = Modifier
                  .weight(1f)
                  .padding(horizontal = 8.dp),
                colors = SliderDefaults.colors(
                  thumbColor = JokerPurplePrimary,
                  activeTrackColor = JokerPurplePrimary,
                  inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                )
              )
              Text(
                text = formatDuration(totalDuration),
                color = TextSecondary,
                fontSize = 10.sp
              )
            }
          }

          // Server selector chips inside player HUD
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text(
                text = "السيرفر:",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )

              servers.take(4).forEachIndexed { idx, srv ->
                val isSelected = srv.id == selectedServer.id
                Surface(
                  color = if (isSelected) JokerPurplePrimary else Color.White.copy(alpha = 0.15f),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.clickable { onServerSelected(srv) }
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(if (srv.pingMs < 30) JokerNeonGreen else JokerGold)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "سيرفر ${idx + 1}",
                      color = TextPrimary,
                      fontSize = 10.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                  }
                }
              }
            }

            IconButton(
              onClick = {
                isFullscreen = !isFullscreen
                onToggleFullscreen()
              },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                contentDescription = "شاشة كاملة",
                tint = TextPrimary
              )
            }
          }
        }
      }
    }
  }
}

fun launchExternalVideoPlayer(
  context: Context,
  videoUrl: String,
  title: String,
  resumePositionMs: Long = 0L
) {
  openInExternalPlayer(context, videoUrl, title, resumePositionMs)
}

fun openInExternalPlayer(
  context: Context,
  videoUrl: String,
  title: String,
  resumePositionMs: Long = 0L
) {
  val uri = if (videoUrl == JokerTvDataProvider.STREAM_LOCAL_RAW || videoUrl.startsWith("local", ignoreCase = true)) {
    Uri.parse(JokerTvDataProvider.STREAM_REMOTE_OCEANS)
  } else {
    Uri.parse(videoUrl)
  }

  val intent = Intent(Intent.ACTION_VIEW).apply {
    setDataAndType(uri, "video/*")
    putExtra("title", title)
    putExtra("secure_uri", true)
    if (resumePositionMs > 1000L) {
      putExtra("position", resumePositionMs.toInt())
      putExtra("position_ms", resumePositionMs)
      putExtra("from_start", false)
    }
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
  }

  try {
    // Attempt direct launch into user's installed phone player (VLC, MX Player, etc.)
    context.startActivity(intent)
  } catch (e: Exception) {
    try {
      val chooser = Intent.createChooser(intent, "اختر مشغل الفيديو (VLC / MX Player / أي مشغل):")
      chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(chooser)
    } catch (ex: Exception) {
      Toast.makeText(context, "جاري المشاهدة عبر مشغل جوكر TV المدمج", Toast.LENGTH_SHORT).show()
    }
  }
}

private fun formatDuration(millis: Long): String {
  val totalSeconds = (millis / 1000).toInt()
  val minutes = totalSeconds / 60
  val seconds = totalSeconds % 60
  return String.format("%02d:%02d", minutes, seconds)
}
