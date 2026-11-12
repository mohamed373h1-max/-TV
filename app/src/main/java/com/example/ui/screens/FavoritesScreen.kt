package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.FavoriteItem
import com.example.model.MediaType
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.JokerBackground
import com.example.ui.theme.JokerCyanAccent
import com.example.ui.theme.JokerGold
import com.example.ui.theme.JokerLiveRed
import com.example.ui.theme.JokerPurpleLight
import com.example.ui.theme.JokerPurplePrimary
import com.example.ui.theme.JokerSurfaceElevated
import com.example.ui.theme.JokerSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FavoritesScreen(
  favoriteItems: List<FavoriteItem>,
  onRemoveFavorite: (FavoriteItem) -> Unit,
  onOpenItem: (FavoriteItem) -> Unit,
  onExploreContent: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf("الكل") }
  val tabs = listOf("الكل", "قنوات رياضية", "مسلسلات رمضان", "أفلام سينما")

  val filteredFavorites = remember(selectedTab, favoriteItems) {
    when (selectedTab) {
      "الكل" -> favoriteItems
      "قنوات رياضية" -> favoriteItems.filter { it.mediaType == MediaType.CHANNEL || it.mediaType == MediaType.MATCH }
      "مسلسلات رمضان" -> favoriteItems.filter { it.mediaType == MediaType.SERIES }
      "أفلام سينما" -> favoriteItems.filter { it.mediaType == MediaType.MOVIE }
      else -> favoriteItems
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(JokerBackground),
    contentPadding = PaddingValues(bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = JokerLiveRed,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "قائمة المفضلة",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Surface(
          color = JokerPurplePrimary.copy(alpha = 0.2f),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = "${favoriteItems.size} عناصر محفوظة",
            color = JokerPurpleLight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }
    }

    // Filter Tabs
    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(tabs) { tab ->
          val isSelected = tab == selectedTab
          Surface(
            color = if (isSelected) JokerPurplePrimary else JokerSurfaceElevated,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.clickable { selectedTab = tab }
          ) {
            Text(
              text = tab,
              color = if (isSelected) Color.White else TextSecondary,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
          }
        }
      }
    }

    // Empty State
    if (filteredFavorites.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 24.dp)
          ) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(JokerSurfaceVariant),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(36.dp)
              )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = "قائمتك المفضلة فارغة حالياً",
              color = TextPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "يمكنك إضافة أي قناة كروية، مسلسل، أو فيلم عبر النقر على رمز القلب ❤️ للوصول السريع إليها لاحقاً.",
              color = TextSecondary,
              fontSize = 12.sp,
              lineHeight = 18.sp,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
              onClick = onExploreContent,
              colors = ButtonDefaults.buttonColors(containerColor = JokerPurplePrimary),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(text = "تصفح المحتوى الآن", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    } else {
      // Favorite Cards List
      items(filteredFavorites) { item ->
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
          FavoriteItemCard(
            item = item,
            onOpen = { onOpenItem(item) },
            onRemove = { onRemoveFavorite(item) }
          )
        }
      }
    }
  }
}

@Composable
fun FavoriteItemCard(
  item: FavoriteItem,
  onOpen: () -> Unit,
  onRemove: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onOpen() },
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
      // Thumbnail
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(RoundedCornerShape(12.dp))
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
        } else {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = when (item.mediaType) {
                MediaType.CHANNEL -> Icons.Default.LiveTv
                MediaType.MATCH -> Icons.Default.SportsSoccer
                MediaType.SERIES -> Icons.Default.Tv
                MediaType.MOVIE -> Icons.Default.LocalMovies
              },
              contentDescription = null,
              tint = JokerPurpleLight,
              modifier = Modifier.size(28.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = item.title,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )

          Surface(
            color = JokerPurplePrimary.copy(alpha = 0.2f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = item.badge,
              color = JokerPurpleLight,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = item.subtitle,
          color = TextSecondary,
          fontSize = 12.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (item.rating != null) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              tint = JokerGold,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "${item.rating}",
              color = JokerGold,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
          }

          Text(
            text = "اضغط للمشاهدة الفورية",
            color = JokerCyanAccent,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      IconButton(onClick = onRemove) {
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = "حذف من المفضلة",
          tint = TextMuted
        )
      }
    }
  }
}
