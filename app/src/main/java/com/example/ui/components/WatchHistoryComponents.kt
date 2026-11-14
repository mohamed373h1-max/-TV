package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.MediaType
import com.example.model.WatchHistoryItem
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.JokerBackground
import com.example.ui.theme.JokerCyanAccent
import com.example.ui.theme.JokerGold
import com.example.ui.theme.JokerLiveRed
import com.example.ui.theme.JokerNeonGreen
import com.example.ui.theme.JokerPurpleLight
import com.example.ui.theme.JokerPurplePrimary
import com.example.ui.theme.JokerSurface
import com.example.ui.theme.JokerSurfaceElevated
import com.example.ui.theme.JokerSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ContinueWatchingShelf(
  items: List<WatchHistoryItem>,
  onSelectItem: (WatchHistoryItem) -> Unit,
  onRemoveItem: (String) -> Unit,
  onOpenAllHistory: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (items.isEmpty()) return

  Column(modifier = modifier.fillMaxWidth()) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.History,
          contentDescription = null,
          tint = JokerCyanAccent,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "متابعة المشاهدة (سجل المشاهدة)",
          color = TextPrimary,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Surface(
        color = JokerPurplePrimary.copy(alpha = 0.2f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.clickable { onOpenAllHistory() }
      ) {
        Text(
          text = "عرض السجل (${items.size})",
          color = JokerPurpleLight,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
      }
    }

    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(items, key = { it.id }) { item ->
        WatchHistoryCard(
          item = item,
          onClick = { onSelectItem(item) },
          onRemove = { onRemoveItem(item.id) }
        )
      }
    }
  }
}

@Composable
fun WatchHistoryCard(
  item: WatchHistoryItem,
  onClick: () -> Unit,
  onRemove: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .width(220.dp)
      .clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
  ) {
    Column {
      // Thumbnail Box with Play Icon + Progress Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(16f / 9.5f)
          .background(JokerSurfaceElevated)
      ) {
        if (item.imageUrl.isNotBlank()) {
          AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
              .data(item.imageUrl)
              .crossfade(true)
              .build(),
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }

        // Gradient
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color.Black.copy(alpha = 0.35f),
                  Color.Transparent,
                  Color.Black.copy(alpha = 0.85f)
                )
              )
            )
        )

        // Center Play Button
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(JokerPurplePrimary.copy(alpha = 0.85f))
            .align(Alignment.Center),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "استئناف",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
        }

        // Remove from history button (top-right)
        IconButton(
          onClick = onRemove,
          modifier = Modifier
            .size(28.dp)
            .align(Alignment.TopEnd)
            .padding(2.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.6f))
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "حذف من السجل",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
        }

        // Badge (Type)
        Surface(
          color = when (item.mediaType) {
            MediaType.SERIES -> JokerPurplePrimary
            MediaType.MOVIE -> JokerCyanAccent
            MediaType.MATCH -> JokerLiveRed
            MediaType.CHANNEL -> JokerGold
            MediaType.CARTOON -> JokerNeonGreen
          }.copy(alpha = 0.9f),
          shape = RoundedCornerShape(bottomEnd = 8.dp),
          modifier = Modifier.align(Alignment.TopStart)
        ) {
          Text(
            text = when (item.mediaType) {
              MediaType.SERIES -> "مسلسل"
              MediaType.MOVIE -> "فيلم"
              MediaType.MATCH -> "مباراة"
              MediaType.CHANNEL -> "قناة"
              MediaType.CARTOON -> "كرتون"
            },
            color = if (item.mediaType == MediaType.MOVIE || item.mediaType == MediaType.CARTOON) Color.Black else Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        // Time indicator at bottom of thumbnail
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(horizontal = 8.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = item.formattedPosition,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
          if (item.totalDurationMs > 0) {
            Text(
              text = item.formattedDuration,
              color = TextSecondary,
              fontSize = 10.sp
            )
          }
        }
      }

      // Linear Progress Bar (showing exact watch progress)
      LinearProgressIndicator(
        progress = { item.progressFraction },
        modifier = Modifier
          .fillMaxWidth()
          .height(3.5.dp),
        color = JokerPurplePrimary,
        trackColor = Color.White.copy(alpha = 0.15f)
      )

      // Content description
      Column(modifier = Modifier.padding(10.dp)) {
        Text(
          text = item.title,
          color = TextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = item.subtitle,
          color = TextSecondary,
          fontSize = 11.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          val percent = (item.progressFraction * 100).toInt()
          Text(
            text = "تمت مشاهدة $percent%",
            color = JokerCyanAccent,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )

          Surface(
            color = JokerPurplePrimary.copy(alpha = 0.15f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = "استئناف الآن",
              color = JokerPurpleLight,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchHistoryModalSheet(
  watchHistory: List<WatchHistoryItem>,
  onSelectItem: (WatchHistoryItem) -> Unit,
  onRemoveItem: (String) -> Unit,
  onClearAll: () -> Unit,
  onDismiss: () -> Unit
) {
  var selectedFilter by remember { mutableStateOf("الكل") }
  val filters = listOf("الكل", "مسلسلات رمضان", "أفلام سينما", "كرتون وأنمي", "مباريات وقنوات")

  val filteredList = remember(selectedFilter, watchHistory) {
    when (selectedFilter) {
      "الكل" -> watchHistory
      "مسلسلات رمضان" -> watchHistory.filter { it.mediaType == MediaType.SERIES }
      "أفلام سينما" -> watchHistory.filter { it.mediaType == MediaType.MOVIE }
      "كرتون وأنمي" -> watchHistory.filter { it.mediaType == MediaType.CARTOON }
      "مباريات وقنوات" -> watchHistory.filter { it.mediaType == MediaType.MATCH || it.mediaType == MediaType.CHANNEL }
      else -> watchHistory
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    containerColor = JokerSurface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 32.dp)
    ) {
      // Sheet Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = JokerPurpleLight,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "سجل المشاهدة (Watch History)",
              color = TextPrimary,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "استكمال المشاهدة فوراً من حيث توقفت",
              color = TextSecondary,
              fontSize = 11.sp
            )
          }
        }

        if (watchHistory.isNotEmpty()) {
          Surface(
            color = JokerLiveRed.copy(alpha = 0.15f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.clickable { onClearAll() }
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.DeleteSweep,
                contentDescription = "مسح الكل",
                tint = JokerLiveRed,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "مسح الكل",
                color = JokerLiveRed,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // Filter Chips
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(filters) { f ->
          val isSelected = f == selectedFilter
          Surface(
            color = if (isSelected) JokerPurplePrimary else JokerSurfaceElevated,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.clickable { selectedFilter = f }
          ) {
            Text(
              text = f,
              color = if (isSelected) Color.White else TextSecondary,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Empty State
      if (filteredList.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.History,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "لا توجد فيديوهات مسجلة حالياً",
              color = TextPrimary,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "الفيديوهات والحلقات التي تشاهدها ستظهر هنا تلقائياً مع نقطة التوقف بدقة.",
              color = TextSecondary,
              fontSize = 12.sp,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      } else {
        // Items list
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredList, key = { it.id }) { item ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  onDismiss()
                  onSelectItem(item)
                },
              colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
              shape = RoundedCornerShape(14.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Thumbnail
                Box(
                  modifier = Modifier
                    .size(68.dp, 54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(JokerSurfaceElevated)
                ) {
                  if (item.imageUrl.isNotBlank()) {
                    AsyncImage(
                      model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build(),
                      contentDescription = item.title,
                      contentScale = ContentScale.Crop,
                      modifier = Modifier.fillMaxSize()
                    )
                  }

                  Box(
                    modifier = Modifier
                      .size(24.dp)
                      .clip(CircleShape)
                      .background(JokerPurplePrimary.copy(alpha = 0.85f))
                      .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.PlayArrow,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = item.title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = item.subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Spacer(modifier = Modifier.height(4.dp))

                  LinearProgressIndicator(
                    progress = { item.progressFraction },
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(3.dp),
                    color = JokerPurplePrimary,
                    trackColor = Color.White.copy(alpha = 0.15f)
                  )

                  Spacer(modifier = Modifier.height(4.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = "توقفت عند ${item.formattedPosition}",
                      color = JokerCyanAccent,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Medium
                    )
                    Text(
                      text = "${(item.progressFraction * 100).toInt()}%",
                      color = TextSecondary,
                      fontSize = 10.sp
                    )
                  }
                }

                IconButton(onClick = { onRemoveItem(item.id) }) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "حذف",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
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
