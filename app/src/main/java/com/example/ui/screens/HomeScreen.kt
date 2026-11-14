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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.CartoonItem
import com.example.model.EgyptianMovie
import com.example.model.FootballMatch
import com.example.model.RamadanSeries
import com.example.model.SportsChannel
import com.example.model.WatchHistoryItem
import com.example.ui.components.CartoonCard
import com.example.ui.components.ContinueWatchingShelf
import com.example.ui.components.MatchCard
import com.example.ui.components.MovieCard
import com.example.ui.components.SeriesCard
import com.example.ui.components.SportsChannelCard
import com.example.ui.components.WatchHistoryModalSheet
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
import kotlinx.coroutines.delay

data class HeroBannerItem(
  val title: String,
  val subtitle: String,
  val tag: String,
  val drawableRes: Int,
  val actionText: String
)

@Composable
fun HomeScreen(
  channels: List<SportsChannel>,
  matches: List<FootballMatch>,
  seriesList: List<RamadanSeries>,
  moviesList: List<EgyptianMovie>,
  cartoonsList: List<CartoonItem>,
  isFavorite: (String) -> Boolean,
  onToggleFavorite: (Any) -> Unit,
  onSelectChannel: (SportsChannel) -> Unit,
  onSelectMatch: (FootballMatch) -> Unit,
  onSelectSeries: (RamadanSeries) -> Unit,
  onSelectMovie: (EgyptianMovie) -> Unit,
  onSelectCartoon: (CartoonItem) -> Unit,
  onNavigateToSports: () -> Unit,
  onNavigateToRamadan: () -> Unit,
  onNavigateToMovies: () -> Unit,
  onNavigateToCartoons: () -> Unit,
  watchHistory: List<WatchHistoryItem> = emptyList(),
  onSelectHistoryItem: (WatchHistoryItem) -> Unit = {},
  onRemoveHistoryItem: (String) -> Unit = {},
  onClearHistory: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val heroBanners = remember {
    listOf(
      HeroBannerItem(
        title = "دوري أبطال أوروبا & beIN SPORTS",
        subtitle = "قمة ريال مدريد وميلان & كلاسيكو العرب الأهلي والزمالك بسيرفرات قوية",
        tag = "مباشر الآن 4K",
        drawableRes = R.drawable.hero_sports_banner,
        actionText = "شاهد البث الآن"
      ),
      HeroBannerItem(
        title = "ملحمة الحشاشين & مسلسلات رمضان",
        subtitle = "جميع حلقات الموسم كاملة وبجودة فائقة بدون إعلانات مزعجة",
        tag = "رمضان الحصري",
        drawableRes = R.drawable.hero_ramadan_banner,
        actionText = "تصفح المسلسلات"
      ),
      HeroBannerItem(
        title = "ولاد رزق 3: القاضية & أفلام السينما",
        subtitle = "أضخم إنتاجات السينما المصرية متوفرة الآن بسيرفرات متعددة",
        tag = "شباك التذاكر",
        drawableRes = R.drawable.hero_cinema_banner,
        actionText = "شاهد الفيلم"
      ),
      HeroBannerItem(
        title = "عالم الكرتون والأنمي (مدبلج بالعربي)",
        subtitle = "المحقق كونان، ون بيس، ناروتو، غامبول والأسد الملك مدبلجة بالعربي",
        tag = "مدبلج عربي 100%",
        drawableRes = R.drawable.img_hero_cartoons_banner,
        actionText = "شاهد الكرتون"
      )
    )
  }

  var currentBannerIndex by remember { mutableIntStateOf(0) }
  var showAllHistorySheet by remember { mutableStateOf(false) }

  // Auto carousel rotation
  LaunchedEffect(Unit) {
    while (true) {
      delay(5500)
      currentBannerIndex = (currentBannerIndex + 1) % heroBanners.size
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(JokerBackground),
    contentPadding = PaddingValues(bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    // 1. Hero Banner Carousel
    item {
      val activeBanner = heroBanners[currentBannerIndex]
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9.5f)
        ) {
          Image(
            painter = painterResource(id = activeBanner.drawableRes),
            contentDescription = activeBanner.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )

          // Dramatic Gradient
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(
                    Color.Black.copy(alpha = 0.35f),
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.92f)
                  )
                )
              )
          )

          // Banner Tag
          Surface(
            color = JokerPurplePrimary,
            shape = RoundedCornerShape(bottomEnd = 10.dp),
            modifier = Modifier.align(Alignment.TopStart)
          ) {
            Text(
              text = activeBanner.tag,
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }

          // Content overlay at bottom
          Column(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(14.dp)
          ) {
            Text(
              text = activeBanner.title,
              color = TextPrimary,
              fontSize = 17.sp,
              fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = activeBanner.subtitle,
              color = TextSecondary,
              fontSize = 12.sp,
              maxLines = 1
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Button(
                onClick = {
                  when (currentBannerIndex) {
                    0 -> channels.firstOrNull()?.let { onSelectChannel(it) }
                    1 -> seriesList.firstOrNull()?.let { onSelectSeries(it) }
                    2 -> moviesList.firstOrNull()?.let { onSelectMovie(it) }
                    3 -> cartoonsList.firstOrNull()?.let { onSelectCartoon(it) }
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = JokerPurplePrimary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.PlayArrow,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = activeBanner.actionText,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              // Indicators
              Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                heroBanners.indices.forEach { idx ->
                  Box(
                    modifier = Modifier
                      .size(if (idx == currentBannerIndex) 16.dp else 6.dp, 6.dp)
                      .clip(CircleShape)
                      .background(
                        if (idx == currentBannerIndex) JokerPurpleLight else Color.White.copy(alpha = 0.3f)
                      )
                      .clickable { currentBannerIndex = idx }
                  )
                }
              }
            }
          }
        }
      }
    }

    // 2. Quick Navigation Shortcuts
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        QuickCategoryPill(
          icon = Icons.Default.SportsSoccer,
          title = "قنوات الكورة",
          color = JokerLiveRed,
          onClick = onNavigateToSports,
          modifier = Modifier.weight(1f)
        )
        QuickCategoryPill(
          icon = Icons.Default.Tv,
          title = "مسلسلات",
          color = JokerPurplePrimary,
          onClick = onNavigateToRamadan,
          modifier = Modifier.weight(1f)
        )
        QuickCategoryPill(
          icon = Icons.Default.LocalMovies,
          title = "سينما",
          color = JokerCyanAccent,
          onClick = onNavigateToMovies,
          modifier = Modifier.weight(1f)
        )
        QuickCategoryPill(
          icon = Icons.Default.AutoAwesome,
          title = "كرتون",
          color = JokerGold,
          onClick = onNavigateToCartoons,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 2.5 Continue Watching Shelf (Watch History)
    if (watchHistory.isNotEmpty()) {
      item {
        ContinueWatchingShelf(
          items = watchHistory,
          onSelectItem = onSelectHistoryItem,
          onRemoveItem = onRemoveHistoryItem,
          onOpenAllHistory = { showAllHistorySheet = true }
        )
      }
    }

    // 3. Live Matches Today
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitleRow(
          title = "مباريات اليوم المباشرة",
          actionText = "كل المباريات",
          onAction = onNavigateToSports
        )

        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(matches) { match ->
            MatchCard(
              match = match,
              isFavorite = isFavorite(match.id),
              onToggleFavorite = { onToggleFavorite(match) },
              onClick = { onSelectMatch(match) },
              modifier = Modifier.width(290.dp)
            )
          }
        }
      }
    }

    // 4. beIN Sports Channels Section
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitleRow(
          title = "قنوات بي إن سبورت الرياضية (سيرفرات قوية)",
          actionText = "كل القنوات",
          onAction = onNavigateToSports
        )

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          channels.take(4).forEach { channel ->
            SportsChannelCard(
              channel = channel,
              isFavorite = isFavorite(channel.id),
              onToggleFavorite = { onToggleFavorite(channel) },
              onClick = { onSelectChannel(channel) }
            )
          }
        }
      }
    }

    // 5. Ramadan Series Section
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitleRow(
          title = "أحدث مسلسلات رمضان (المواسم كاملة)",
          actionText = "عرض الكل",
          onAction = onNavigateToRamadan
        )

        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(seriesList) { series ->
            SeriesCard(
              series = series,
              isFavorite = isFavorite(series.id),
              onToggleFavorite = { onToggleFavorite(series) },
              onClick = { onSelectSeries(series) }
            )
          }
        }
      }
    }

    // 6. Egyptian Movies Section
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitleRow(
          title = "أفلام السينما المصرية الحديثة",
          actionText = "كل الأفلام",
          onAction = onNavigateToMovies
        )

        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(moviesList) { movie ->
            MovieCard(
              movie = movie,
              isFavorite = isFavorite(movie.id),
              onToggleFavorite = { onToggleFavorite(movie) },
              onClick = { onSelectMovie(movie) }
            )
          }
        }
      }
    }

    // 7. Cartoons & Anime Section (Arabic Dubbed)
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitleRow(
          title = "عالم الكرتون والأنمي (مدبلج بالعربي)",
          actionText = "كل الكرتون",
          onAction = onNavigateToCartoons
        )

        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(cartoonsList, key = { it.id }) { cartoon ->
            CartoonCard(
              cartoon = cartoon,
              isFavorite = isFavorite(cartoon.id),
              onToggleFavorite = { onToggleFavorite(cartoon) },
              onClick = { onSelectCartoon(cartoon) }
            )
          }
        }
      }
    }
  }

  if (showAllHistorySheet) {
    WatchHistoryModalSheet(
      watchHistory = watchHistory,
      onSelectItem = onSelectHistoryItem,
      onRemoveItem = onRemoveHistoryItem,
      onClearAll = onClearHistory,
      onDismiss = { showAllHistorySheet = false }
    )
  }
}

@Composable
fun SectionTitleRow(
  title: String,
  actionText: String,
  onAction: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      color = TextPrimary,
      fontSize = 17.sp,
      fontWeight = FontWeight.Bold
    )

    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .clickable { onAction() }
        .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
      Text(
        text = actionText,
        color = JokerPurpleLight,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )
      Icon(
        imageVector = Icons.Default.ChevronLeft,
        contentDescription = null,
        tint = JokerPurpleLight,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}

@Composable
fun QuickCategoryPill(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  color: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp, horizontal = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(color.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = color,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = title,
        color = TextPrimary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
