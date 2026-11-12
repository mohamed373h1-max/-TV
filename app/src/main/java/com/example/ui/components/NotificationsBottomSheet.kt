package com.example.ui.components

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MediaType
import com.example.notification.AppNotification
import com.example.ui.theme.BorderSubtle
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
fun NotificationsBottomSheet(
  notifications: List<AppNotification>,
  seriesNotifyEnabled: Boolean,
  moviesNotifyEnabled: Boolean,
  matchesNotifyEnabled: Boolean,
  onUpdateSettings: (series: Boolean, movies: Boolean, matches: Boolean) -> Unit,
  onMarkAllAsRead: () -> Unit,
  onClearAll: () -> Unit,
  onNotificationClick: (AppNotification) -> Unit,
  onSimulateNewRelease: (isMovie: Boolean) -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showSettings by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(JokerSurface)
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.NotificationsActive,
          contentDescription = null,
          tint = JokerPurpleLight,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "مركز الإشعارات والتنبيهات",
          color = TextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )

        val unreadCount = notifications.count { !it.isRead }
        if (unreadCount > 0) {
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            color = JokerLiveRed,
            shape = RoundedCornerShape(10.dp)
          ) {
            Text(
              text = "$unreadCount جديد",
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      IconButton(onClick = onClose) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "إغلاق",
          tint = TextSecondary
        )
      }
    }

    // Action buttons bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
          color = if (showSettings) JokerPurplePrimary else JokerSurfaceElevated,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.clickable { showSettings = !showSettings }
        ) {
          Text(
            text = if (showSettings) "إخفاء الإعدادات" else "⚙️ إعدادات التنبيه",
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }

        if (notifications.any { !it.isRead }) {
          Surface(
            color = JokerSurfaceElevated,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.clickable { onMarkAllAsRead() }
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = JokerNeonGreen,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "تحديد الكل كمقروء",
                color = TextPrimary,
                fontSize = 11.sp
              )
            }
          }
        }
      }

      if (notifications.isNotEmpty()) {
        IconButton(onClick = onClearAll, modifier = Modifier.size(32.dp)) {
          Icon(
            imageVector = Icons.Default.DeleteSweep,
            contentDescription = "مسح الكل",
            tint = TextMuted
          )
        }
      }
    }

    // Expandable Settings Card
    if (showSettings) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "تخصيص تنبيهات المحتوى",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(10.dp))

          // Series toggle
          NotificationSettingRow(
            title = "تنبيهات الحلقات الجديدة لمسلسلات رمضان",
            checked = seriesNotifyEnabled,
            onCheckedChange = { onUpdateSettings(it, moviesNotifyEnabled, matchesNotifyEnabled) }
          )

          // Movies toggle
          NotificationSettingRow(
            title = "تنبيهات الأفلام الحصرية الجديدة للسينما المصرية",
            checked = moviesNotifyEnabled,
            onCheckedChange = { onUpdateSettings(seriesNotifyEnabled, it, matchesNotifyEnabled) }
          )

          // Matches toggle
          NotificationSettingRow(
            title = "تنبيهات انطلاق مباريات اليوم المباشرة",
            checked = matchesNotifyEnabled,
            onCheckedChange = { onUpdateSettings(seriesNotifyEnabled, moviesNotifyEnabled, it) }
          )
        }
      }
    }

    // Simulate New Release Buttons (Instant Demo Action)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      colors = CardDefaults.cardColors(containerColor = JokerSurfaceElevated),
      shape = RoundedCornerShape(14.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "اختبار نظام التنبيه:",
          color = TextSecondary,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = { onSimulateNewRelease(false) },
            colors = ButtonDefaults.buttonColors(containerColor = JokerPurplePrimary),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Icon(Icons.Default.Tv, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("إشعار حلقة جديدة", fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = { onSimulateNewRelease(true) },
            colors = ButtonDefaults.buttonColors(containerColor = JokerCyanAccent),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Icon(Icons.Default.LocalMovies, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text("إشعار فيلم حصري", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Notifications List
    if (notifications.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(48.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "لا توجد إشعارات جديدة حالياً",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "سنقوم بتنبيهك فور إضافة حلقة جديدة أو فيلم حصري جديد!",
            color = TextSecondary,
            fontSize = 12.sp
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(notifications) { notif ->
          NotificationCardItem(
            notification = notif,
            onClick = { onNotificationClick(notif) }
          )
        }
      }
    }
  }
}

@Composable
private fun NotificationSettingRow(
  title: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      color = TextSecondary,
      fontSize = 12.sp,
      modifier = Modifier.weight(1f)
    )
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = JokerPurplePrimary,
        uncheckedThumbColor = TextMuted,
        uncheckedTrackColor = JokerSurfaceElevated
      )
    )
  }
}

@Composable
fun NotificationCardItem(
  notification: AppNotification,
  onClick: () -> Unit
) {
  val tagColor = when (notification.tag) {
    "حلقة جديدة" -> JokerNeonGreen
    "فيلم حصري" -> JokerPurpleLight
    "مباراة مباشرة" -> JokerLiveRed
    else -> JokerCyanAccent
  }

  val tagIcon = when (notification.targetType) {
    MediaType.SERIES -> Icons.Default.Tv
    MediaType.MOVIE -> Icons.Default.MovieCreation
    MediaType.MATCH, MediaType.CHANNEL -> Icons.Default.SportsSoccer
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() },
    colors = CardDefaults.cardColors(
      containerColor = if (!notification.isRead) JokerSurfaceVariant else JokerSurfaceElevated
    ),
    shape = RoundedCornerShape(14.dp),
    border = if (!notification.isRead) androidx.compose.foundation.BorderStroke(1.dp, tagColor.copy(alpha = 0.5f)) else androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(tagColor.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = tagIcon,
          contentDescription = null,
          tint = tagColor,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            color = tagColor.copy(alpha = 0.2f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = notification.tag,
              color = tagColor,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = notification.timestampText,
              color = TextMuted,
              fontSize = 10.sp
            )
            if (!notification.isRead) {
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(JokerLiveRed)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = notification.title,
          color = TextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = notification.message,
          color = TextSecondary,
          fontSize = 11.sp,
          lineHeight = 16.sp
        )
      }
    }
  }
}
