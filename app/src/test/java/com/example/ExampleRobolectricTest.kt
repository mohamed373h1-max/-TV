package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.FavoritesRepository
import com.example.data.JokerTvDataProvider
import com.example.data.M3uPlaylistParser
import com.example.data.UserPreferencesRepository
import com.example.data.local.JokerDatabase
import com.example.data.local.toEntity
import com.example.model.FavoriteItem
import com.example.model.MediaType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun testAppNameIsJokerTv() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("جوكر TV", appName)
  }

  @Test
  fun testDataProviderHasRequiredContent() {
    // 1. Check beIN Sports channels
    val channels = JokerTvDataProvider.sportsChannels
    assertTrue(channels.isNotEmpty())
    assertTrue(channels.any { it.name.contains("beIN SPORTS 1") })
    assertTrue(channels.all { it.servers.isNotEmpty() })

    // 2. Check today matches
    val matches = JokerTvDataProvider.todayMatches
    assertTrue(matches.isNotEmpty())
    assertTrue(matches.any { it.isLive })

    // 3. Check Ramadan series
    val series = JokerTvDataProvider.ramadanSeries
    assertTrue(series.isNotEmpty())
    assertTrue(series.any { it.title.contains("الحشاشين") })
    assertTrue(series.all { it.episodes.isNotEmpty() })

    // 4. Check Egyptian movies
    val movies = JokerTvDataProvider.egyptianMovies
    assertTrue(movies.isNotEmpty())
    assertTrue(movies.any { it.title.contains("ولاد رزق") })
  }

  @Test
  fun testFavoritesAndCommentsRepository() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, JokerDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    try {
      val repository = UserPreferencesRepository(context, db.favoriteDao())

      // Check favorites persisted via Room
      val testItem = FavoriteItem(
        id = "test_item_1",
        mediaType = MediaType.CHANNEL,
        title = "قناة اختبار",
        subtitle = "اختبار",
        imageUrl = "",
        badge = "LIVE"
      )

      val added = repository.toggleFavorite(testItem)
      assertTrue(added)
      assertTrue(repository.isFavorite("test_item_1"))

      val removed = repository.toggleFavorite(testItem)
      assertFalse(removed)
      assertFalse(repository.isFavorite("test_item_1"))

      // Check adding comment
      repository.addComment(
        itemId = "series_hashashin",
        authorName = "مستخدم تجريبي",
        rating = 5,
        commentText = "مسلسل رائع جداً وسيرفرات ممتازة"
      )

      val comments = repository.getCommentsForItem("series_hashashin")
      assertTrue(comments.any { it.authorName == "مستخدم تجريبي" && it.rating == 5 })
    } finally {
      db.close()
    }
  }

  @Test
  fun testRoomFavoriteDaoSportsChannelsAndMovies() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, JokerDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    try {
      val dao = db.favoriteDao()
      val favRepo = FavoritesRepository(dao)

      val sportsChannel = FavoriteItem(
        id = "bein_1",
        mediaType = MediaType.CHANNEL,
        title = "beIN SPORTS 1 HD",
        subtitle = "قمة دوري الأبطال",
        imageUrl = "",
        badge = "4K VIP",
        rating = 4.9f
      )
      val movie = FavoriteItem(
        id = "mov_welad_rizk_3",
        mediaType = MediaType.MOVIE,
        title = "ولاد رزق 3: القاضية",
        subtitle = "أكشن • 2024",
        imageUrl = "",
        badge = "فيلم",
        rating = 4.9f
      )

      dao.insertFavorite(sportsChannel.toEntity())
      dao.insertFavorite(movie.toEntity())

      val favoriteSports = favRepo.favoriteSportsChannels.first()
      assertEquals(1, favoriteSports.size)
      assertEquals("bein_1", favoriteSports.first().id)

      val favoriteMovies = favRepo.favoriteMovies.first()
      assertEquals(1, favoriteMovies.size)
      assertEquals("mov_welad_rizk_3", favoriteMovies.first().id)

      assertTrue(favRepo.observeIsFavorite("bein_1").first())

      dao.deleteFavoriteById("bein_1")
      assertFalse(dao.isFavorite("bein_1"))
      assertEquals(1, dao.observeFavoritesCount().first())

      favRepo.clearAll()
      assertTrue(favRepo.allFavorites.first().isEmpty())
    } finally {
      db.close()
    }
  }

  @Test
  fun testCartoonsAndAnimeArabicDubbedData() {
    val cartoons = JokerTvDataProvider.cartoonsList
    assertTrue("Cartoons catalog should have at least 12 entries", cartoons.size >= 12)
    assertTrue("All cartoons must be Arabic dubbed", cartoons.all { it.dubbingLanguage.contains("مدبلج") })
    assertTrue("Should include Japanese anime", cartoons.any { it.category == "أنمي ياباني" && it.title.contains("كونان") })
    assertTrue("Should include world cartoons", cartoons.any { it.category == "كرتون عالمي" && it.title.contains("غامبول") })
    assertTrue("Should include animated films", cartoons.any { it.category == "أفلام أنميشن" && it.isMovie })
    assertTrue("Every cartoon episode must have working servers", cartoons.all { c ->
      c.episodes.isNotEmpty() && c.episodes.all { ep -> ep.servers.size >= 4 }
    })
  }

  @Test
  fun testNotificationManager() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val notifManager = com.example.notification.JokerNotificationManager(context)

    assertTrue(notifManager.notifications.value.isNotEmpty())
    val initialCount = notifManager.notifications.value.size

    // Send a new notification for a new movie release
    notifManager.sendReleaseNotification(
      title = "فيلم تجريبي جديد",
      message = "متاح الآن في جوكر TV",
      targetType = MediaType.MOVIE,
      targetId = "mov_test",
      tag = "فيلم حصري"
    )

    assertEquals(initialCount + 1, notifManager.notifications.value.size)
    val latest = notifManager.notifications.value.first()
    assertEquals("فيلم تجريبي جديد", latest.title)
    assertFalse(latest.isRead)

    // Mark as read
    notifManager.markAsRead(latest.id)
    val updatedLatest = notifManager.notifications.value.first { it.id == latest.id }
    assertTrue(updatedLatest.isRead)
  }

  @Test
  fun testWatchHistoryAndResume() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = UserPreferencesRepository(context)

    // Verify initial watch history loaded
    val initialHistory = repository.watchHistory.value
    assertTrue(initialHistory.isNotEmpty())

    // Record progress for a series episode
    val testWatchItem = com.example.model.WatchHistoryItem(
      id = "series_test_ep_5",
      contentId = "series_test",
      mediaType = MediaType.SERIES,
      title = "مسلسل تجريبي",
      subtitle = "الحلقة 5",
      imageUrl = "",
      episodeNumber = 5,
      lastPositionMs = 900000L, // 15 mins
      totalDurationMs = 2700000L // 45 mins
    )

    repository.recordWatchProgress(testWatchItem)

    // Verify resume position retrieved correctly
    val resumePos = repository.getResumePosition("series_test", 5)
    assertEquals(900000L, resumePos)

    // Verify formatting & progress fraction
    assertEquals("15:00", testWatchItem.formattedPosition)
    assertEquals("45:00", testWatchItem.formattedDuration)
    assertTrue(testWatchItem.progressFraction in 0.33f..0.34f)

    // Remove item
    repository.removeWatchHistoryItem("series_test_ep_5")
    val posAfterRemove = repository.getResumePosition("series_test", 5)
    assertEquals(0L, posAfterRemove)

    // Clear all history
    repository.clearWatchHistory()
    assertTrue(repository.watchHistory.value.isEmpty())
  }

  @Test
  fun testRealMatchesOfTheDay() {
    val matches = JokerTvDataProvider.todayMatches
    assertTrue("Matches list should have at least 6 real fixtures", matches.size >= 6)

    // Check Real Madrid vs Milan
    val realMadridMatch = matches.firstOrNull { it.homeTeam.contains("ريال مدريد") }
    assertTrue("Real Madrid match should exist", realMadridMatch != null)
    assertTrue("Should be against Milan", realMadridMatch!!.awayTeam.contains("ميلان"))
    assertTrue("Should have real club logo", realMadridMatch.homeLogo.isNotEmpty())
    assertTrue("Should have real away club logo", realMadridMatch.awayLogo.isNotEmpty())
    assertEquals("beIN SPORTS 1 HD", realMadridMatch.channelName)

    // Check Al Ahly vs Zamalek
    val ahlyMatch = matches.firstOrNull { it.homeTeam.contains("الأهلي") }
    assertTrue("Al Ahly match should exist", ahlyMatch != null)
    assertTrue("Should be against Zamalek", ahlyMatch!!.awayTeam.contains("الزمالك"))
    assertTrue("Should have Al Ahly crest", ahlyMatch.homeLogo.isNotEmpty())
    assertTrue("Should have Zamalek crest", ahlyMatch.awayLogo.isNotEmpty())
    assertEquals("ON Time Sports 1 HD", ahlyMatch.channelName)

    // Check all matches have servers and commentators
    assertTrue("All matches must have servers", matches.all { it.servers.size >= 4 })
    assertTrue("All matches must have commentators", matches.all { it.commentator.isNotEmpty() })
    assertTrue("All matches must have stadiums", matches.all { it.stadium.isNotEmpty() })
  }

  @Test
  fun testVideoStreamingDataSource() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    // Check that sample_video raw resource is present and valid
    val rawUri = androidx.media3.datasource.RawResourceDataSource.buildRawResourceUri(R.raw.sample_video)
    assertTrue("Raw URI scheme should be rawresource", rawUri.scheme == "rawresource")

    // Open file descriptor to guarantee resource is valid
    val afd = context.resources.openRawResourceFd(R.raw.sample_video)
    assertTrue("Sample video should have valid length", afd.length > 0)
    afd.close()

    // Verify all content items have functional online primary servers (no LOCAL_RAW on Server 1)
    val channels = JokerTvDataProvider.sportsChannels
    assertTrue(channels.all { it.servers.isNotEmpty() })
    assertTrue("Sports channels must start on online HTTPS stream", channels.all {
      it.servers.first().streamUrl.startsWith("https://")
    })

    val series = JokerTvDataProvider.ramadanSeries
    assertTrue(series.all { it.episodes.all { ep ->
      ep.servers.isNotEmpty() && ep.servers.first().streamUrl.startsWith("https://")
    } })

    val movies = JokerTvDataProvider.egyptianMovies
    assertTrue(movies.all { it.servers.first().streamUrl.startsWith("https://") })

    val cartoons = JokerTvDataProvider.cartoonsList
    assertTrue(cartoons.all { c ->
      c.episodes.all { ep -> ep.servers.first().streamUrl.startsWith("https://") }
    })

    // Ensure categories do not all share the exact same primary URL
    val distinctPrimaryUrls = (
      channels.map { it.servers.first().streamUrl } +
        movies.map { it.servers.first().streamUrl } +
        cartoons.map { it.episodes.first().servers.first().streamUrl }
      ).toSet()
    assertTrue("Primary streams should vary across channels and categories", distinctPrimaryUrls.size >= 4)
  }

  @Test
  fun testM3uPlaylistParserAndCustomChannel() {
    val sampleM3u = """
      #EXTM3U
      #EXTINF:-1 tvg-logo="https://example.com/logo1.png" group-title="قنوات رياضية",قناة رياضية 1
      https://example.com/live/sport1.m3u8
      #EXTINF:-1 group-title="مسلسلات",قناة دراما HD
      https://example.com/live/drama.m3u8
    """.trimIndent()

    val parsed = M3uPlaylistParser.parseM3uContent(sampleM3u)
    assertEquals(2, parsed.size)
    assertEquals("قناة رياضية 1", parsed[0].name)
    assertEquals("قنوات رياضية", parsed[0].category)
    assertEquals("https://example.com/live/sport1.m3u8", parsed[0].servers.first().streamUrl)
    assertEquals("قناة دراما HD", parsed[1].name)

    val single = M3uPlaylistParser.createSingleCustomChannel(
      name = "بث خاص",
      streamUrl = "https://example.com/my_stream.m3u8"
    )
    assertEquals("بث خاص", single.name)
    assertEquals("https://example.com/my_stream.m3u8", single.servers.first().streamUrl)
    assertTrue(M3uPlaylistParser.isValidStreamUrl("https://example.com/my_stream.m3u8"))
    assertFalse(M3uPlaylistParser.isValidStreamUrl("not_a_valid_url"))
  }
}

