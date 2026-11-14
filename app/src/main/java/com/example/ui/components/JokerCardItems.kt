package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.CartoonItem
import com.example.model.EgyptianMovie
import com.example.model.FootballMatch
import com.example.model.RamadanSeries
import com.example.model.SportsChannel
import com.example.ui.theme.BorderSubtle
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

@Composable
fun SportsChannelCard(
  channel: SportsChannel,
  isFavorite: Boolean,
  onToggleFavorite: () -> Unit,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Channel icon / logo
      Box(
        modifier = Modifier
          .size(60.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(JokerSurfaceElevated),
        contentAlignment = Alignment.Center
      ) {
        AsyncImage(
          model = ImageRequest.Builder(LocalContext.current)
            .data(channel.logoUrl)
            .crossfade(true)
            .build(),
          contentDescription = channel.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        // Overlay gradient
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
        )
        Icon(
          imageVector = Icons.Default.LiveTv,
          contentDescription = null,
          tint = JokerPurpleLight,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = channel.name,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.width(6.dp))
          if (channel.isLive) {
            Surface(
              color = JokerLiveRed,
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "مباشر",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = channel.currentEvent,
          color = TextSecondary,
          fontSize = 12.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Surface(
            color = JokerPurplePrimary.copy(alpha = 0.2f),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = channel.qualityBadge,
              color = JokerPurpleLight,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
            )
          }

          Text(
            text = "${channel.servers.size} سيرفرات متوفرة",
            color = JokerNeonGreen,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      IconButton(onClick = onToggleFavorite) {
        Icon(
          imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
          contentDescription = "إضافة للمفضلة",
          tint = if (isFavorite) JokerLiveRed else TextMuted
        )
      }
    }
  }
}

@Composable
fun MatchCard(
  match: FootballMatch,
  isFavorite: Boolean,
  onToggleFavorite: () -> Unit,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (match.isLive) JokerPurplePrimary.copy(alpha = 0.6f) else BorderSubtle
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: Championship & Live/Time
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.SportsSoccer,
            contentDescription = null,
            tint = JokerCyanAccent,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = match.championship,
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (match.isLive) {
            Surface(
              color = JokerLiveRed,
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = match.minute ?: "مباشر",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          } else {
            Text(
              text = match.timeText,
              color = TextMuted,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(28.dp).padding(start = 4.dp)
          ) {
            Icon(
              imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
              contentDescription = "تفضيل",
              tint = if (isFavorite) JokerLiveRed else TextMuted,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Teams and Score Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Home team
        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(JokerSurfaceElevated)
              .padding(4.dp),
            contentAlignment = Alignment.Center
          ) {
            if (match.homeLogo.isNotBlank()) {
              AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                  .data(match.homeLogo)
                  .crossfade(true)
                  .build(),
                contentDescription = match.homeTeam,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
              )
            } else {
              Text(
                text = match.homeTeam.take(1),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = match.homeTeam,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        // Score or VS
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(horizontal = 8.dp)
        ) {
          if (match.homeScore != null && match.awayScore != null) {
            Surface(
              color = JokerSurfaceElevated,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "${match.homeScore} - ${match.awayScore}",
                color = JokerGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          } else {
            Surface(
              color = JokerSurfaceElevated,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "ضد",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }
        }

        // Away team
        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(JokerSurfaceElevated)
              .padding(4.dp),
            contentAlignment = Alignment.Center
          ) {
            if (match.awayLogo.isNotBlank()) {
              AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                  .data(match.awayLogo)
                  .crossfade(true)
                  .build(),
                contentDescription = match.awayTeam,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
              )
            } else {
              Text(
                text = match.awayTeam.take(1),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = match.awayTeam,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Footer: Commentator & Channel
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(JokerSurfaceElevated)
          .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "🎙️ ${match.commentator}",
          color = TextSecondary,
          fontSize = 11.sp
        )
        Text(
          text = "📺 ${match.channelName}",
          color = JokerPurpleLight,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun SeriesCard(
  series: RamadanSeries,
  isFavorite: Boolean,
  onToggleFavorite: () -> Unit,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .width(155.dp)
      .clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
  ) {
    Column {
      // Poster with overlay badge and favorite button
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(2f / 3f)
          .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
      ) {
        AsyncImage(
          model = ImageRequest.Builder(LocalContext.current)
            .data(series.posterUrl)
            .crossfade(true)
            .build(),
          contentDescription = series.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Gradient
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color.Black.copy(alpha = 0.5f),
                  Color.Transparent,
                  Color.Black.copy(alpha = 0.85f)
                )
              )
            )
        )

        // Year badge
        Surface(
          color = JokerPurplePrimary.copy(alpha = 0.9f),
          shape = RoundedCornerShape(bottomEnd = 8.dp),
          modifier = Modifier.align(Alignment.TopStart)
        ) {
          Text(
            text = "رمضان ${series.seasonYear}",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }

        // Favorite button
        IconButton(
          onClick = onToggleFavorite,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .size(32.dp)
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "تفضيل",
            tint = if (isFavorite) JokerLiveRed else Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        // Rating at bottom
        Row(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = JokerGold,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "${series.rating}",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Title & Episodes Count
      Column(modifier = Modifier.padding(8.dp)) {
        Text(
          text = series.title,
          color = TextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "${series.episodesCount} حلقة • ${series.genre}",
          color = TextSecondary,
          fontSize = 10.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

@Composable
fun MovieCard(
  movie: EgyptianMovie,
  isFavorite: Boolean,
  onToggleFavorite: () -> Unit,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .width(155.dp)
      .clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
  ) {
    Column {
      // Poster
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(2f / 3f)
          .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
      ) {
        AsyncImage(
          model = ImageRequest.Builder(LocalContext.current)
            .data(movie.posterUrl)
            .crossfade(true)
            .build(),
          contentDescription = movie.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Gradient
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color.Black.copy(alpha = 0.5f),
                  Color.Transparent,
                  Color.Black.copy(alpha = 0.85f)
                )
              )
            )
        )

        // Year badge
        Surface(
          color = JokerCyanAccent.copy(alpha = 0.9f),
          shape = RoundedCornerShape(bottomEnd = 8.dp),
          modifier = Modifier.align(Alignment.TopStart)
        ) {
          Text(
            text = "${movie.releaseYear}",
            color = Color.Black,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        // Favorite button
        IconButton(
          onClick = onToggleFavorite,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .size(32.dp)
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "تفضيل",
            tint = if (isFavorite) JokerLiveRed else Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        // Rating
        Row(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = JokerGold,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "${movie.rating}",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Title & Genre
      Column(modifier = Modifier.padding(8.dp)) {
        Text(
          text = movie.title,
          color = TextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "${movie.durationMinutes} دقيقة • ${movie.genre}",
          color = TextSecondary,
          fontSize = 10.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

@Composable
fun CartoonCard(
  cartoon: CartoonItem,
  isFavorite: Boolean,
  onToggleFavorite: () -> Unit,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .width(155.dp)
      .testTag("cartoon_card_${cartoon.id}")
      .clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(2f / 3f)
          .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
      ) {
        AsyncImage(
          model = ImageRequest.Builder(LocalContext.current)
            .data(cartoon.posterUrl)
            .crossfade(true)
            .build(),
          contentDescription = cartoon.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color.Black.copy(alpha = 0.55f),
                  Color.Transparent,
                  Color.Black.copy(alpha = 0.88f)
                )
              )
            )
        )

        // Arabic Dubbed Badge
        Surface(
          color = JokerNeonGreen,
          shape = RoundedCornerShape(bottomEnd = 8.dp),
          modifier = Modifier.align(Alignment.TopStart)
        ) {
          Text(
            text = cartoon.dubbingLanguage,
            color = Color.Black,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }

        // Favorite button with accessible touch area
        IconButton(
          onClick = onToggleFavorite,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .size(48.dp)
            .testTag("favorite_cartoon_${cartoon.id}")
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "تفضيل",
            tint = if (isFavorite) JokerLiveRed else Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        // Bottom row: Rating + Episodes count / Movie badge
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(horizontal = 6.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              tint = JokerGold,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "${cartoon.rating}",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Surface(
            color = JokerPurplePrimary.copy(alpha = 0.9f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = if (cartoon.isMovie) "فيلم كامل" else "${cartoon.episodesCount} حلقة",
              color = Color.White,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
            )
          }
        }
      }

      Column(modifier = Modifier.padding(8.dp)) {
        Text(
          text = cartoon.title,
          color = TextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "${cartoon.category} • ${cartoon.dubbingStudio}",
          color = TextSecondary,
          fontSize = 10.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}
