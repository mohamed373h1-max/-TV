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
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FootballMatch
import com.example.model.SportsChannel
import com.example.ui.components.MatchCard
import com.example.ui.components.SportsChannelCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.JokerBackground
import com.example.ui.theme.JokerCyanAccent
import com.example.ui.theme.JokerLiveRed
import com.example.ui.theme.JokerNeonGreen
import com.example.ui.theme.JokerPurpleLight
import com.example.ui.theme.JokerPurplePrimary
import com.example.ui.theme.JokerSurface
import com.example.ui.theme.JokerSurfaceElevated
import com.example.ui.theme.JokerSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun SportsScreen(
  channels: List<SportsChannel>,
  matches: List<FootballMatch>,
  isFavorite: (String) -> Boolean,
  onToggleFavorite: (Any) -> Unit,
  onSelectChannel: (SportsChannel) -> Unit,
  onSelectMatch: (FootballMatch) -> Unit,
  onImportM3uInput: suspend (rawInput: String, customTitle: String) -> Result<Int> = { _, _ ->
    Result.success(0)
  },
  onRemoveCustomChannel: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf("الكل") }
  val customChannelsCount = remember(channels) { channels.count { it.id.startsWith("custom_") } }
  val categories = remember(customChannelsCount) {
    buildList {
      add("الكل")
      if (customChannelsCount > 0) add("قنواتك الخاصة")
      addAll(listOf("بي إن سبورت", "أون تايم سبورت", "قنوات SSC", "أبو ظبي الرياضية"))
    }
  }

  val filteredChannels = remember(selectedCategory, channels) {
    when (selectedCategory) {
      "الكل" -> channels
      "قنواتك الخاصة" -> channels.filter { it.id.startsWith("custom_") || it.category == "قنواتك الخاصة" }
      else -> channels.filter { it.category == selectedCategory }
    }
  }

  var showAddStreamDialog by remember { mutableStateOf(false) }
  var customTitleInput by remember { mutableStateOf("") }
  var customUrlOrM3uInput by remember { mutableStateOf("") }
  var importError by remember { mutableStateOf<String?>(null) }
  var importSuccessMsg by remember { mutableStateOf<String?>(null) }
  var isImporting by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  if (showAddStreamDialog) {
    AlertDialog(
      onDismissRequest = { if (!isImporting) showAddStreamDialog = false },
      containerColor = JokerSurface,
      title = {
        Text(
          text = "إضافة رابط بث مباشر أو قائمة IPTV",
          color = TextPrimary,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "الصق رابط بث مباشر (.m3u8 / .mp4) أو رابط قائمة (.m3u) أو محتوى #EXTM3U لتشغيل قنواتك فوراً داخل التطبيق.",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 18.sp
          )

          OutlinedTextField(
            value = customTitleInput,
            onValueChange = { customTitleInput = it },
            label = { Text("اسم القناة (اختياري لروابط M3U)", fontSize = 12.sp) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = JokerCyanAccent,
              unfocusedBorderColor = BorderSubtle,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("custom_stream_title_input")
          )

          OutlinedTextField(
            value = customUrlOrM3uInput,
            onValueChange = {
              customUrlOrM3uInput = it
              importError = null
            },
            label = { Text("رابط البث (.m3u8) أو قائمة (.m3u)", fontSize = 12.sp) },
            placeholder = { Text("https://.../stream.m3u8", fontSize = 11.sp) },
            minLines = 2,
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = JokerPurplePrimary,
              unfocusedBorderColor = BorderSubtle,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("custom_stream_url_input")
          )

          importError?.let { err ->
            Text(
              text = err,
              color = JokerLiveRed,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      },
      confirmButton = {
        Button(
          enabled = !isImporting,
          onClick = {
            isImporting = true
            importError = null
            coroutineScope.launch {
              val res = onImportM3uInput(customUrlOrM3uInput, customTitleInput)
              isImporting = false
              res.onSuccess { count ->
                importSuccessMsg = "تمت إضافة $count قناة بنجاح"
                selectedCategory = "قنواتك الخاصة"
                customTitleInput = ""
                customUrlOrM3uInput = ""
                showAddStreamDialog = false
              }.onFailure { ex ->
                importError = ex.message ?: "تعذر قراءة الرابط"
              }
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = JokerPurplePrimary),
          modifier = Modifier.testTag("confirm_add_stream_button")
        ) {
          if (isImporting) {
            CircularProgressIndicator(
              color = Color.White,
              strokeWidth = 2.dp,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
          }
          Text("إضافة وتشغيل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(
          enabled = !isImporting,
          onClick = { showAddStreamDialog = false }
        ) {
          Text("إلغاء", color = TextSecondary, fontSize = 12.sp)
        }
      }
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(JokerBackground),
    contentPadding = PaddingValues(bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Server Status & Custom M3U Import Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(JokerNeonGreen.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Dns,
                contentDescription = null,
                tint = JokerNeonGreen,
                modifier = Modifier.size(20.dp)
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "بث HLS مباشر متعدد السيرفرات: ",
                  color = TextPrimary,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "نشط أونلاين",
                  color = JokerNeonGreen,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Text(
                text = "تبديل تلقائي بين السيرفرات المتاحة • مع دعم إضافة روابط M3U / M3U8 الخاصة بك",
                color = TextSecondary,
                fontSize = 11.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Button(
              onClick = { showAddStreamDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = JokerCyanAccent),
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
              modifier = Modifier.testTag("add_custom_stream_button")
            ) {
              Icon(
                imageVector = Icons.Default.AddLink,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "إضافة رابط بث / قائمة M3U",
                color = Color.Black,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }

            importSuccessMsg?.let { msg ->
              Text(
                text = msg,
                color = JokerNeonGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    // Matches of the Day Ticker
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.SportsSoccer,
            contentDescription = null,
            tint = JokerCyanAccent,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "مباريات اليوم المنقولة حصرياً",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }

        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(matches) { match ->
            MatchCard(
              match = match,
              isFavorite = isFavorite(match.id),
              onToggleFavorite = { onToggleFavorite(match) },
              onClick = { onSelectMatch(match) },
              modifier = Modifier.width(285.dp)
            )
          }
        }
      }
    }

    // Category Tabs
    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(categories) { cat ->
          val isSelected = cat == selectedCategory
          Surface(
            color = if (isSelected) JokerPurplePrimary else JokerSurfaceElevated,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.clickable { selectedCategory = cat }
          ) {
            Text(
              text = if (cat == "قنواتك الخاصة") "$cat ($customChannelsCount)" else cat,
              color = if (isSelected) Color.White else TextSecondary,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
          }
        }
      }
    }

    // Channels List Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "قنوات البث المباشر (${filteredChannels.size})",
          color = TextPrimary,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(JokerNeonGreen)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "متصلة بالخدمة",
            color = JokerNeonGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // Channels Items
    items(filteredChannels, key = { it.id }) { channel ->
      Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        SportsChannelCard(
          channel = channel,
          isFavorite = isFavorite(channel.id),
          onToggleFavorite = { onToggleFavorite(channel) },
          onClick = { onSelectChannel(channel) }
        )

        if (channel.id.startsWith("custom_")) {
          IconButton(
            onClick = { onRemoveCustomChannel(channel.id) },
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(4.dp)
              .size(36.dp)
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "حذف القناة المخصصة",
              tint = JokerLiveRed,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}
