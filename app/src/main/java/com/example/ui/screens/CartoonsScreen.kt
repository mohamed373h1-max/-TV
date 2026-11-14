package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.CartoonItem
import com.example.ui.components.CartoonCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.JokerBackground
import com.example.ui.theme.JokerCyanAccent
import com.example.ui.theme.JokerNeonGreen
import com.example.ui.theme.JokerPurpleLight
import com.example.ui.theme.JokerPurplePrimary
import com.example.ui.theme.JokerSurfaceElevated
import com.example.ui.theme.JokerSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CartoonsScreen(
  cartoonsList: List<CartoonItem>,
  isFavorite: (String) -> Boolean,
  onToggleFavorite: (Any) -> Unit,
  onSelectCartoon: (CartoonItem) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedFilter by remember { mutableStateOf("الكل") }
  var searchQuery by remember { mutableStateOf("") }

  val filters = listOf(
    "الكل",
    "أنمي ياباني",
    "كرتون عالمي",
    "أفلام أنميشن",
    "مركز الزهرة",
    "ديزني بالعربي"
  )

  val filteredCartoons = remember(selectedFilter, searchQuery, cartoonsList) {
    val byCategory = when (selectedFilter) {
      "الكل" -> cartoonsList
      "أنمي ياباني" -> cartoonsList.filter { it.category == "أنمي ياباني" }
      "كرتون عالمي" -> cartoonsList.filter { it.category == "كرتون عالمي" }
      "أفلام أنميشن" -> cartoonsList.filter { it.category == "أفلام أنميشن" || it.isMovie }
      "مركز الزهرة" -> cartoonsList.filter { it.dubbingStudio.contains("الزهرة") || it.dubbingStudio.contains("سبيستون") }
      "ديزني بالعربي" -> cartoonsList.filter { it.dubbingStudio.contains("ديزني") }
      else -> cartoonsList
    }
    val q = searchQuery.trim().lowercase()
    if (q.isEmpty()) {
      byCategory
    } else {
      byCategory.filter {
        it.title.lowercase().contains(q) ||
          it.originalTitle.lowercase().contains(q) ||
          it.genre.lowercase().contains(q) ||
          it.dubbingStudio.lowercase().contains(q)
      }
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(JokerBackground),
    contentPadding = PaddingValues(bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Hero Banner for Cartoons & Anime (Arabic Dubbed)
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
        ) {
          Image(
            painter = painterResource(id = R.drawable.img_hero_cartoons_banner),
            contentDescription = "عالم الكرتون والأنمي المدبلج بالعربي",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )

          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(
                    Color.Black.copy(alpha = 0.25f),
                    Color.Black.copy(alpha = 0.35f),
                    Color.Black.copy(alpha = 0.92f)
                  )
                )
              )
          )

          Surface(
            color = JokerNeonGreen,
            shape = RoundedCornerShape(bottomEnd = 10.dp),
            modifier = Modifier.align(Alignment.TopStart)
          ) {
            Text(
              text = "مدبلج بالعربي 100%",
              color = Color.Black,
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }

          Column(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(14.dp)
          ) {
            Text(
              text = "عالم الكرتون والأنمي العربي",
              color = TextPrimary,
              fontSize = 18.sp,
              fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "أشهر مسلسلات الأنمي والكرتون وأفلام الأنميشن مدبلجة بالعربي بدون تقطيع",
              color = TextSecondary,
              fontSize = 11.sp,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Button(
                onClick = { cartoonsList.firstOrNull()?.let { onSelectCartoon(it) } },
                colors = ButtonDefaults.buttonColors(containerColor = JokerPurplePrimary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.testTag("cartoons_hero_watch_button")
              ) {
                Icon(
                  imageVector = Icons.Default.PlayArrow,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "شاهد الآن",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Surface(
                color = JokerCyanAccent.copy(alpha = 0.2f),
                shape = RoundedCornerShape(10.dp)
              ) {
                Text(
                  text = "${cartoonsList.size} عمل مدبلج",
                  color = JokerCyanAccent,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }
      }
    }

    // Quick Search inside Cartoons & Anime
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .testTag("cartoons_search_input"),
        placeholder = {
          Text(
            text = "ابحث في الكرتون والأنمي (كونان، ون بيس، غامبول، الأسد الملك...)",
            color = TextMuted,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        },
        leadingIcon = {
          Icon(imageVector = Icons.Default.Search, contentDescription = "بحث", tint = JokerPurpleLight)
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح البحث", tint = TextMuted)
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = JokerPurplePrimary,
          unfocusedBorderColor = BorderSubtle,
          focusedContainerColor = JokerSurfaceVariant,
          unfocusedContainerColor = JokerSurfaceVariant,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary
        )
      )
    }

    // Category / Dubbing Studio Filter Chips
    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(filters) { filter ->
          val isSelected = filter == selectedFilter
          Surface(
            color = if (isSelected) JokerPurplePrimary else JokerSurfaceElevated,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
              .testTag("cartoon_filter_$filter")
              .clickable { selectedFilter = filter }
          ) {
            Text(
              text = filter,
              color = if (isSelected) Color.White else TextSecondary,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
          }
        }
      }
    }

    // Empty state or 2-column Grid
    if (filteredCartoons.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "لا توجد أعمال مطابقة للبحث",
              color = TextPrimary,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "جرّب البحث باسم آخر أو اختر تصنيف «الكل»",
              color = TextSecondary,
              fontSize = 12.sp
            )
          }
        }
      }
    } else {
      val chunked = filteredCartoons.chunked(2)
      items(chunked) { rowItems ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          rowItems.forEach { cartoon ->
            CartoonCard(
              cartoon = cartoon,
              isFavorite = isFavorite(cartoon.id),
              onToggleFavorite = { onToggleFavorite(cartoon) },
              onClick = { onSelectCartoon(cartoon) },
              modifier = Modifier.weight(1f)
            )
          }
          if (rowItems.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }
}
