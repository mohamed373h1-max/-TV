package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.MediaType
import com.example.notification.JokerNotificationManager
import com.example.ui.components.AdvancedSearchSheet
import com.example.ui.components.NotificationsBottomSheet
import com.example.ui.components.WatchHistoryModalSheet
import com.example.ui.screens.ActiveDetailItem
import com.example.ui.screens.CartoonsScreen
import com.example.ui.screens.DetailPlayerScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MoviesScreen
import com.example.ui.screens.RamadanScreen
import com.example.ui.screens.SportsScreen
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.JokerBackground
import com.example.ui.theme.JokerCyanAccent
import com.example.ui.theme.JokerLiveRed
import com.example.ui.theme.JokerPurpleLight
import com.example.ui.theme.JokerPurplePrimary
import com.example.ui.theme.JokerSurface
import com.example.ui.theme.JokerSurfaceElevated
import com.example.ui.theme.JokerSurfaceVariant
import com.example.ui.theme.JokerTvTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.JokerViewModel
import com.example.ui.viewmodel.NavTab

class MainActivity : ComponentActivity() {

  private val viewModel: JokerViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    handleNotificationIntent(intent)

    setContent {
      JokerTvTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          JokerTvMainApp(viewModel = viewModel)
        }
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    handleNotificationIntent(intent)
  }

  private fun handleNotificationIntent(intent: Intent?) {
    if (intent == null) return
    val targetTypeStr = intent.getStringExtra(JokerNotificationManager.EXTRA_TARGET_TYPE)
    val targetId = intent.getStringExtra(JokerNotificationManager.EXTRA_TARGET_ID)
    if (!targetTypeStr.isNullOrEmpty() && !targetId.isNullOrEmpty()) {
      try {
        val targetType = MediaType.valueOf(targetTypeStr)
        when (targetType) {
          MediaType.SERIES -> {
            val series = viewModel.seriesList.find { it.id == targetId }
            series?.let { viewModel.openDetail(ActiveDetailItem.Series(it)) }
          }
          MediaType.MOVIE -> {
            val movie = viewModel.moviesList.find { it.id == targetId }
            movie?.let { viewModel.openDetail(ActiveDetailItem.Movie(it)) }
          }
          MediaType.MATCH -> {
            val match = viewModel.matches.find { it.id == targetId }
            match?.let { viewModel.openDetail(ActiveDetailItem.Match(it)) }
          }
          MediaType.CHANNEL -> {
            val channel = viewModel.channels.find { it.id == targetId }
            channel?.let { viewModel.openDetail(ActiveDetailItem.Channel(it)) }
          }
          MediaType.CARTOON -> {
            val cartoon = viewModel.cartoonsList.find { it.id == targetId }
            cartoon?.let { viewModel.openDetail(ActiveDetailItem.Cartoon(it)) }
          }
        }
      } catch (e: Exception) {
        // ignore format errors
      }
    }
  }
}

@Composable
fun JokerTvMainApp(viewModel: JokerViewModel) {
  val context = LocalContext.current
  val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
  val activeDetail by viewModel.activeDetail.collectAsStateWithLifecycle()
  val isSearchOpen by viewModel.isSearchOpen.collectAsStateWithLifecycle()
  val isNotificationsOpen by viewModel.isNotificationsOpen.collectAsStateWithLifecycle()
  val allChannels by viewModel.allChannels.collectAsStateWithLifecycle()
  val favorites by viewModel.favoriteItems.collectAsStateWithLifecycle()
  val comments by viewModel.userComments.collectAsStateWithLifecycle()
  val watchHistory by viewModel.watchHistory.collectAsStateWithLifecycle()
  val notifications by viewModel.notifications.collectAsStateWithLifecycle()
  val unreadNotificationsCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()

  var isHistoryOpen by remember { mutableStateOf(false) }

  val seriesNotifyEnabled by viewModel.seriesNotifyEnabled.collectAsStateWithLifecycle()
  val moviesNotifyEnabled by viewModel.moviesNotifyEnabled.collectAsStateWithLifecycle()
  val matchesNotifyEnabled by viewModel.matchesNotifyEnabled.collectAsStateWithLifecycle()

  // Permission Launcher for Android 13+ Notification Runtime Permission
  val notificationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    // Handled gracefully
  }

  LaunchedEffect(Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(
          context,
          Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(JokerBackground),
    topBar = {
      if (activeDetail == null && !isSearchOpen && !isNotificationsOpen) {
        JokerTopAppBar(
          onOpenSearch = { viewModel.openSearch() },
          onOpenNotifications = { viewModel.openNotifications() },
          onOpenHistory = { isHistoryOpen = true },
          onOpenFavorites = { viewModel.setTab(NavTab.FAVORITES) },
          isFavoritesActive = currentTab == NavTab.FAVORITES,
          favoritesCount = favorites.size,
          unreadNotificationsCount = unreadNotificationsCount
        )
      }
    },
    bottomBar = {
      if (activeDetail == null && !isSearchOpen && !isNotificationsOpen) {
        JokerBottomNavigationBar(
          currentTab = currentTab,
          onTabSelected = { viewModel.setTab(it) }
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Main Screens based on active tab
      when (currentTab) {
        NavTab.HOME -> {
          HomeScreen(
            channels = allChannels,
            matches = viewModel.matches,
            seriesList = viewModel.seriesList,
            moviesList = viewModel.moviesList,
            cartoonsList = viewModel.cartoonsList,
            isFavorite = { viewModel.isFavorite(it) },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onSelectChannel = { viewModel.openDetail(ActiveDetailItem.Channel(it)) },
            onSelectMatch = { viewModel.openDetail(ActiveDetailItem.Match(it)) },
            onSelectSeries = { viewModel.openDetail(ActiveDetailItem.Series(it)) },
            onSelectMovie = { viewModel.openDetail(ActiveDetailItem.Movie(it)) },
            onSelectCartoon = { viewModel.openDetail(ActiveDetailItem.Cartoon(it)) },
            onNavigateToSports = { viewModel.setTab(NavTab.SPORTS) },
            onNavigateToRamadan = { viewModel.setTab(NavTab.RAMADAN) },
            onNavigateToMovies = { viewModel.setTab(NavTab.MOVIES) },
            onNavigateToCartoons = { viewModel.setTab(NavTab.CARTOONS) },
            watchHistory = watchHistory,
            onSelectHistoryItem = { viewModel.openWatchHistoryItem(it) },
            onRemoveHistoryItem = { viewModel.removeWatchHistoryItem(it) },
            onClearHistory = { viewModel.clearWatchHistory() }
          )
        }
        NavTab.SPORTS -> {
          SportsScreen(
            channels = allChannels,
            matches = viewModel.matches,
            isFavorite = { viewModel.isFavorite(it) },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onSelectChannel = { viewModel.openDetail(ActiveDetailItem.Channel(it)) },
            onSelectMatch = { viewModel.openDetail(ActiveDetailItem.Match(it)) },
            onImportM3uInput = { input, title -> viewModel.importM3uInput(input, title) },
            onRemoveCustomChannel = { viewModel.removeCustomChannel(it) }
          )
        }
        NavTab.RAMADAN -> {
          RamadanScreen(
            seriesList = viewModel.seriesList,
            isFavorite = { viewModel.isFavorite(it) },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onSelectSeries = { viewModel.openDetail(ActiveDetailItem.Series(it)) }
          )
        }
        NavTab.MOVIES -> {
          MoviesScreen(
            moviesList = viewModel.moviesList,
            isFavorite = { viewModel.isFavorite(it) },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onSelectMovie = { viewModel.openDetail(ActiveDetailItem.Movie(it)) }
          )
        }
        NavTab.CARTOONS -> {
          CartoonsScreen(
            cartoonsList = viewModel.cartoonsList,
            isFavorite = { viewModel.isFavorite(it) },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onSelectCartoon = { viewModel.openDetail(ActiveDetailItem.Cartoon(it)) }
          )
        }
        NavTab.FAVORITES -> {
          FavoritesScreen(
            favoriteItems = favorites,
            onRemoveFavorite = { viewModel.toggleFavorite(it) },
            onOpenItem = { viewModel.openFavoriteItem(it) },
            onExploreContent = { viewModel.setTab(NavTab.HOME) },
            onClearAllFavorites = { viewModel.clearAllFavorites() }
          )
        }
      }

      // Active Detail / Player Overlay
      activeDetail?.let { detailItem ->
        val itemId = when (detailItem) {
          is ActiveDetailItem.Channel -> detailItem.channel.id
          is ActiveDetailItem.Match -> detailItem.match.id
          is ActiveDetailItem.Series -> detailItem.series.id
          is ActiveDetailItem.Movie -> detailItem.movie.id
          is ActiveDetailItem.Cartoon -> detailItem.cartoon.id
        }
        val resumePos = when (detailItem) {
          is ActiveDetailItem.Series -> viewModel.getResumePosition(detailItem.series.id, detailItem.initialEpisodeNumber)
          is ActiveDetailItem.Cartoon -> viewModel.getResumePosition(detailItem.cartoon.id, detailItem.initialEpisodeNumber)
          is ActiveDetailItem.Movie -> viewModel.getResumePosition(detailItem.movie.id, null)
          is ActiveDetailItem.Channel -> viewModel.getResumePosition(detailItem.channel.id, null)
          is ActiveDetailItem.Match -> viewModel.getResumePosition(detailItem.match.id, null)
        }
        DetailPlayerScreen(
          item = detailItem,
          comments = comments.filter { it.itemId == itemId },
          isFavorite = viewModel.isFavorite(itemId),
          onToggleFavorite = {
            when (detailItem) {
              is ActiveDetailItem.Channel -> viewModel.toggleFavorite(detailItem.channel)
              is ActiveDetailItem.Match -> viewModel.toggleFavorite(detailItem.match)
              is ActiveDetailItem.Series -> viewModel.toggleFavorite(detailItem.series)
              is ActiveDetailItem.Movie -> viewModel.toggleFavorite(detailItem.movie)
              is ActiveDetailItem.Cartoon -> viewModel.toggleFavorite(detailItem.cartoon)
            }
          },
          onAddComment = { author, rating, text ->
            viewModel.addComment(itemId, author, rating, text)
          },
          onBack = { viewModel.closeDetail() },
          initialResumePosition = resumePos,
          onRecordProgress = { viewModel.recordWatchProgress(it) }
        )
      }

      // Advanced Search Fullscreen Overlay
      if (isSearchOpen) {
        AdvancedSearchSheet(
          allChannels = allChannels,
          allMatches = viewModel.matches,
          allSeries = viewModel.seriesList,
          allMovies = viewModel.moviesList,
          allCartoons = viewModel.cartoonsList,
          onClose = { viewModel.closeSearch() },
          onSelectChannel = { viewModel.openDetail(ActiveDetailItem.Channel(it)) },
          onSelectMatch = { viewModel.openDetail(ActiveDetailItem.Match(it)) },
          onSelectSeries = { viewModel.openDetail(ActiveDetailItem.Series(it)) },
          onSelectMovie = { viewModel.openDetail(ActiveDetailItem.Movie(it)) },
          onSelectCartoon = { viewModel.openDetail(ActiveDetailItem.Cartoon(it)) }
        )
      }

      // Notifications Fullscreen Overlay
      if (isNotificationsOpen) {
        NotificationsBottomSheet(
          notifications = notifications,
          seriesNotifyEnabled = seriesNotifyEnabled,
          moviesNotifyEnabled = moviesNotifyEnabled,
          matchesNotifyEnabled = matchesNotifyEnabled,
          onUpdateSettings = { s, m, matches ->
            viewModel.updateNotificationSettings(s, m, matches)
          },
          onMarkAllAsRead = { viewModel.markAllNotificationsAsRead() },
          onClearAll = { viewModel.clearAllNotifications() },
          onNotificationClick = { viewModel.onNotificationClick(it) },
          onSimulateNewRelease = { isMovie -> viewModel.simulateNewRelease(isMovie) },
          onClose = { viewModel.closeNotifications() }
        )
      }

      // Watch History Bottom Sheet Overlay
      if (isHistoryOpen) {
        WatchHistoryModalSheet(
          watchHistory = watchHistory,
          onSelectItem = { item ->
            isHistoryOpen = false
            viewModel.openWatchHistoryItem(item)
          },
          onRemoveItem = { viewModel.removeWatchHistoryItem(it) },
          onClearAll = { viewModel.clearWatchHistory() },
          onDismiss = { isHistoryOpen = false }
        )
      }
    }
  }
}

@Composable
fun JokerTopAppBar(
  onOpenSearch: () -> Unit,
  onOpenNotifications: () -> Unit,
  onOpenHistory: () -> Unit,
  onOpenFavorites: () -> Unit,
  isFavoritesActive: Boolean,
  favoritesCount: Int,
  unreadNotificationsCount: Int
) {
  Surface(
    color = JokerSurface,
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Branding: Logo & Title
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(JokerPurplePrimary.copy(alpha = 0.2f))
            .border(1.dp, JokerPurplePrimary, RoundedCornerShape(10.dp)),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_joker_tv_logo),
            contentDescription = "شعار جوكر TV",
            modifier = Modifier.size(30.dp)
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "جوكر TV",
              color = TextPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Black,
              maxLines = 1
            )
            Spacer(modifier = Modifier.width(5.dp))
            Surface(
              color = JokerLiveRed,
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "LIVE",
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }

          Text(
            text = "كورة • مسلسلات • سينما • كرتون",
            color = TextSecondary,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // Action Buttons: Favorites + Watch History + Notification Bell + Search
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
      ) {
        // Favorites Shortcut Button with live Room badge
        IconButton(
          onClick = onOpenFavorites,
          modifier = Modifier
            .size(48.dp)
            .testTag("nav_favorites")
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(if (isFavoritesActive) JokerPurplePrimary else JokerSurfaceVariant)
              .border(1.dp, if (isFavoritesActive) JokerPurpleLight else BorderSubtle, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            if (favoritesCount > 0) {
              BadgedBox(
                badge = {
                  Badge(containerColor = JokerLiveRed) {
                    Text("$favoritesCount", color = Color.White, fontSize = 8.sp)
                  }
                }
              ) {
                Icon(
                  imageVector = Icons.Default.Favorite,
                  contentDescription = "المفضلة",
                  tint = if (isFavoritesActive) Color.White else JokerLiveRed,
                  modifier = Modifier.size(18.dp)
                )
              }
            } else {
              Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "المفضلة",
                tint = if (isFavoritesActive) Color.White else TextSecondary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        // Watch History Button
        IconButton(
          onClick = onOpenHistory,
          modifier = Modifier
            .size(48.dp)
            .testTag("watch_history_button")
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(JokerSurfaceVariant)
              .border(1.dp, BorderSubtle, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.History,
              contentDescription = "سجل المشاهدة",
              tint = JokerCyanAccent,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // Notification Bell Icon with Badge
        IconButton(
          onClick = onOpenNotifications,
          modifier = Modifier
            .size(48.dp)
            .testTag("notification_button")
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(JokerSurfaceVariant)
              .border(1.dp, BorderSubtle, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            if (unreadNotificationsCount > 0) {
              BadgedBox(
                badge = {
                  Badge(containerColor = JokerLiveRed) {
                    Text("$unreadNotificationsCount", color = Color.White, fontSize = 8.sp)
                  }
                }
              ) {
                Icon(
                  imageVector = Icons.Default.Notifications,
                  contentDescription = "الإشعارات",
                  tint = JokerPurpleLight,
                  modifier = Modifier.size(18.dp)
                )
              }
            } else {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "الإشعارات",
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        // Advanced Search Button
        IconButton(
          onClick = onOpenSearch,
          modifier = Modifier
            .size(48.dp)
            .testTag("advanced_search_button")
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(JokerSurfaceVariant)
              .border(1.dp, BorderSubtle, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "بحث وفلاتر",
              tint = JokerPurpleLight,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun JokerBottomNavigationBar(
  currentTab: NavTab,
  onTabSelected: (NavTab) -> Unit
) {
  NavigationBar(
    containerColor = JokerSurface,
    tonalElevation = 8.dp,
    modifier = Modifier
      .navigationBarsPadding()
      .border(1.dp, BorderSubtle, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
      .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
  ) {
    NavigationBarItem(
      selected = currentTab == NavTab.HOME,
      onClick = { onTabSelected(NavTab.HOME) },
      icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
      label = { Text("الرئيسية", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.White,
        selectedTextColor = JokerPurpleLight,
        indicatorColor = JokerPurplePrimary,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted
      ),
      modifier = Modifier.testTag("nav_home")
    )

    NavigationBarItem(
      selected = currentTab == NavTab.SPORTS,
      onClick = { onTabSelected(NavTab.SPORTS) },
      icon = { Icon(Icons.Default.SportsSoccer, contentDescription = "الكورة") },
      label = { Text("الكورة", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.White,
        selectedTextColor = JokerPurpleLight,
        indicatorColor = JokerPurplePrimary,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted
      ),
      modifier = Modifier.testTag("nav_sports")
    )

    NavigationBarItem(
      selected = currentTab == NavTab.RAMADAN,
      onClick = { onTabSelected(NavTab.RAMADAN) },
      icon = { Icon(Icons.Default.Tv, contentDescription = "مسلسلات") },
      label = { Text("مسلسلات", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.White,
        selectedTextColor = JokerPurpleLight,
        indicatorColor = JokerPurplePrimary,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted
      ),
      modifier = Modifier.testTag("nav_ramadan")
    )

    NavigationBarItem(
      selected = currentTab == NavTab.MOVIES,
      onClick = { onTabSelected(NavTab.MOVIES) },
      icon = { Icon(Icons.Default.LocalMovies, contentDescription = "أفلام") },
      label = { Text("أفلام", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.White,
        selectedTextColor = JokerPurpleLight,
        indicatorColor = JokerPurplePrimary,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted
      ),
      modifier = Modifier.testTag("nav_movies")
    )

    NavigationBarItem(
      selected = currentTab == NavTab.CARTOONS,
      onClick = { onTabSelected(NavTab.CARTOONS) },
      icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "كرتون") },
      label = { Text("كرتون", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.White,
        selectedTextColor = JokerPurpleLight,
        indicatorColor = JokerPurplePrimary,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted
      ),
      modifier = Modifier.testTag("nav_cartoons")
    )
  }
}
