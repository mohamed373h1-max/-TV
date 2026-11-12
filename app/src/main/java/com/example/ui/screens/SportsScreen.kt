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
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.theme.JokerGold
import com.example.ui.theme.JokerNeonGreen
import com.example.ui.theme.JokerPurplePrimary
import com.example.ui.theme.JokerSurfaceElevated
import com.example.ui.theme.JokerSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SportsScreen(
  channels: List<SportsChannel>,
  matches: List<FootballMatch>,
  isFavorite: (String) -> Boolean,
  onToggleFavorite: (Any) -> Unit,
  onSelectChannel: (SportsChannel) -> Unit,
  onSelectMatch: (FootballMatch) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf("الكل") }
  val categories = listOf("الكل", "بي إن سبورت", "أون تايم سبورت", "قنوات SSC", "أبو ظبي الرياضية")

  val filteredChannels = remember(selectedCategory, channels) {
    if (selectedCategory == "الكل") channels
    else channels.filter { it.category == selectedCategory }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(JokerBackground),
    contentPadding = PaddingValues(bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Server Status Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
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
                text = "حالة سيرفرات البث المباشر: ",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "متصلة ومستقرة 100%",
                color = JokerNeonGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Text(
              text = "4 سيرفرات لكل قناة (FHD 60fps - HD - SD للنت الضعيف - 4K VIP)",
              color = TextSecondary,
              fontSize = 11.sp
            )
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
              text = cat,
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
    items(filteredChannels) { channel ->
      Box(modifier = Modifier.padding(horizontal = 16.dp)) {
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
