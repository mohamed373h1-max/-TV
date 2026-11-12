package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.JokerTvDataProvider
import com.example.data.UserPreferencesRepository
import com.example.model.FavoriteItem
import com.example.model.MediaType
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
  fun testFavoritesAndCommentsRepository() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = UserPreferencesRepository(context)

    // Check favorites
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

    // Verify all content items have functional servers
    val channels = JokerTvDataProvider.sportsChannels
    assertTrue(channels.all { it.servers.isNotEmpty() })
    val series = JokerTvDataProvider.ramadanSeries
    assertTrue(series.all { it.episodes.all { ep -> ep.servers.isNotEmpty() } })
  }
}

