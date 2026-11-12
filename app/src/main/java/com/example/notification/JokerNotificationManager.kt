package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.model.MediaType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

data class AppNotification(
  val id: String,
  val title: String,
  val message: String,
  val targetType: MediaType,
  val targetId: String,
  val timestampText: String,
  val tag: String, // "حلقة جديدة", "فيلم حصري", "مباراة مباشرة"
  val isRead: Boolean = false
)

class JokerNotificationManager(private val context: Context) {

  companion object {
    const val CHANNEL_ID = "joker_tv_releases_channel"
    const val CHANNEL_NAME = "تحديثات جوكر TV الحصرية"
    const val EXTRA_TARGET_TYPE = "extra_target_type"
    const val EXTRA_TARGET_ID = "extra_target_id"
  }

  private val prefs: SharedPreferences =
    context.getSharedPreferences("joker_tv_notifications", Context.MODE_PRIVATE)

  private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
  val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

  private val _seriesNotifyEnabled = MutableStateFlow(true)
  val seriesNotifyEnabled: StateFlow<Boolean> = _seriesNotifyEnabled.asStateFlow()

  private val _moviesNotifyEnabled = MutableStateFlow(true)
  val moviesNotifyEnabled: StateFlow<Boolean> = _moviesNotifyEnabled.asStateFlow()

  private val _matchesNotifyEnabled = MutableStateFlow(true)
  val matchesNotifyEnabled: StateFlow<Boolean> = _matchesNotifyEnabled.asStateFlow()

  init {
    createNotificationChannel()
    loadPreferences()
    loadNotifications()
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "إشعارات نزول حلقات جديدة من مسلسلات رمضان وأحدث أفلام السينما المصرية"
        enableLights(true)
        enableVibration(true)
      }
      val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      manager.createNotificationChannel(channel)
    }
  }

  private fun loadPreferences() {
    _seriesNotifyEnabled.value = prefs.getBoolean("pref_series_notify", true)
    _moviesNotifyEnabled.value = prefs.getBoolean("pref_movies_notify", true)
    _matchesNotifyEnabled.value = prefs.getBoolean("pref_matches_notify", true)
  }

  fun updateSettings(series: Boolean, movies: Boolean, matches: Boolean) {
    _seriesNotifyEnabled.value = series
    _moviesNotifyEnabled.value = movies
    _matchesNotifyEnabled.value = matches
    prefs.edit()
      .putBoolean("pref_series_notify", series)
      .putBoolean("pref_movies_notify", movies)
      .putBoolean("pref_matches_notify", matches)
      .apply()
  }

  private fun loadNotifications() {
    val jsonString = prefs.getString("notifications_list_json", null)
    if (jsonString.isNullOrEmpty()) {
      val defaults = listOf(
        AppNotification(
          id = "notif_1",
          title = "🔥 فيلم حصري جديد: ولاد رزق 3 (القاضية)",
          message = "تمت إضافة فيلم ولاد رزق 3 بجودة 4K وسيرفرات فائقة السرعة بدون إعلانات!",
          targetType = MediaType.MOVIE,
          targetId = "mov_welad_rizk_3",
          timestampText = "منذ 15 دقيقة",
          tag = "فيلم حصري",
          isRead = false
        ),
        AppNotification(
          id = "notif_2",
          title = "🎬 حلقة جديدة: مسلسل الحشاشين (الحلقة 30 والأخيرة)",
          message = "الحلقة الختامية الملحمية من الحشاشين متوفرة الآن للمشاهدة بأعلى جودة.",
          targetType = MediaType.SERIES,
          targetId = "series_hashashin",
          timestampText = "منذ ساعة",
          tag = "حلقة جديدة",
          isRead = false
        ),
        AppNotification(
          id = "notif_3",
          title = "🍿 فيلم سينمائي: فاصل من اللحظات اللذيذة",
          message = "الفيلم الكوميدي لـ هشام ماجد وهنا الزاهد متاح الآن على سيرفرات جوكر TV.",
          targetType = MediaType.MOVIE,
          targetId = "mov_fasel_lahazat",
          timestampText = "منذ 3 ساعات",
          tag = "فيلم حصري",
          isRead = false
        ),
        AppNotification(
          id = "notif_4",
          title = "✨ حلقة جديدة: مسلسل أشغال شقة (الحلقة 15)",
          message = "حلقة مضحكة ومغامرة جديدة لعائلة الدكتور حمدي متوفرة الآن بسيرفر التوفير وFHD.",
          targetType = MediaType.SERIES,
          targetId = "series_ashghal",
          timestampText = "أمس",
          tag = "حلقة جديدة",
          isRead = true
        ),
        AppNotification(
          id = "notif_5",
          title = "⚽ بث مباشر الآن: ريال مدريد ضد مانشستر سيتي",
          message = "انطلقت قمة دوري أبطال أوروبا على beIN SPORTS 1 HD بتعليق عصام الشوالي.",
          targetType = MediaType.MATCH,
          targetId = "match_1",
          timestampText = "مباشر الآن",
          tag = "مباراة مباشرة",
          isRead = true
        )
      )
      _notifications.value = defaults
      saveNotifications(defaults)
      return
    }

    try {
      val jsonArray = JSONArray(jsonString)
      val list = mutableListOf<AppNotification>()
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          AppNotification(
            id = obj.getString("id"),
            title = obj.getString("title"),
            message = obj.getString("message"),
            targetType = MediaType.valueOf(obj.getString("targetType")),
            targetId = obj.getString("targetId"),
            timestampText = obj.getString("timestampText"),
            tag = obj.getString("tag"),
            isRead = obj.optBoolean("isRead", false)
          )
        )
      }
      _notifications.value = list
    } catch (e: Exception) {
      _notifications.value = emptyList()
    }
  }

  private fun saveNotifications(items: List<AppNotification>) {
    val jsonArray = JSONArray()
    for (item in items) {
      val obj = JSONObject().apply {
        put("id", item.id)
        put("title", item.title)
        put("message", item.message)
        put("targetType", item.targetType.name)
        put("targetId", item.targetId)
        put("timestampText", item.timestampText)
        put("tag", item.tag)
        put("isRead", item.isRead)
      }
      jsonArray.put(obj)
    }
    prefs.edit().putString("notifications_list_json", jsonArray.toString()).apply()
  }

  fun markAllAsRead() {
    val updated = _notifications.value.map { it.copy(isRead = true) }
    _notifications.value = updated
    saveNotifications(updated)
  }

  fun markAsRead(notificationId: String) {
    val updated = _notifications.value.map {
      if (it.id == notificationId) it.copy(isRead = true) else it
    }
    _notifications.value = updated
    saveNotifications(updated)
  }

  fun clearAll() {
    _notifications.value = emptyList()
    saveNotifications(emptyList())
  }

  fun sendReleaseNotification(
    title: String,
    message: String,
    targetType: MediaType,
    targetId: String,
    tag: String
  ) {
    // Check user preference
    val enabled = when (targetType) {
      MediaType.SERIES -> _seriesNotifyEnabled.value
      MediaType.MOVIE -> _moviesNotifyEnabled.value
      MediaType.MATCH, MediaType.CHANNEL -> _matchesNotifyEnabled.value
    }
    if (!enabled) return

    val notifId = "notif_${System.currentTimeMillis()}"
    val newNotif = AppNotification(
      id = notifId,
      title = title,
      message = message,
      targetType = targetType,
      targetId = targetId,
      timestampText = "الآن",
      tag = tag,
      isRead = false
    )

    val updatedList = listOf(newNotif) + _notifications.value
    _notifications.value = updatedList
    saveNotifications(updatedList)

    // Trigger Android System Notification
    triggerSystemNotification(newNotif)
  }

  private fun triggerSystemNotification(notification: AppNotification) {
    // Check permission on Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      val permission = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.POST_NOTIFICATIONS
      )
      if (permission != PackageManager.PERMISSION_GRANTED) {
        return
      }
    }

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra(EXTRA_TARGET_TYPE, notification.targetType.name)
      putExtra(EXTRA_TARGET_ID, notification.targetId)
    }

    val pendingIntent = PendingIntent.getActivity(
      context,
      notification.id.hashCode(),
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle(notification.title)
      .setContentText(notification.message)
      .setStyle(NotificationCompat.BigTextStyle().bigText(notification.message))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .setCategory(NotificationCompat.CATEGORY_EVENT)

    try {
      NotificationManagerCompat.from(context).notify(notification.id.hashCode(), builder.build())
    } catch (e: SecurityException) {
      // Permission not granted
    }
  }
}
