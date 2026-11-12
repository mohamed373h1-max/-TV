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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Nightlight
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
import com.example.model.RamadanSeries
import com.example.ui.components.SeriesCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.JokerBackground
import com.example.ui.theme.JokerGold
import com.example.ui.theme.JokerPurpleLight
import com.example.ui.theme.JokerPurplePrimary
import com.example.ui.theme.JokerSurfaceElevated
import com.example.ui.theme.JokerSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RamadanScreen(
  seriesList: List<RamadanSeries>,
  isFavorite: (String) -> Boolean,
  onToggleFavorite: (Any) -> Unit,
  onSelectSeries: (RamadanSeries) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedFilter by remember { mutableStateOf("الكل") }
  val filters = listOf("الكل", "رمضان 2024", "رمضان 2025", "كوميديا", "دراما وتشويق", "أكشن وشعبي", "تاريخي ورعب")

  val filteredSeries = remember(selectedFilter, seriesList) {
    when (selectedFilter) {
      "الكل" -> seriesList
      "رمضان 2024" -> seriesList.filter { it.seasonYear == 2024 }
      "رمضان 2025" -> seriesList.filter { it.seasonYear == 2025 }
      "كوميديا" -> seriesList.filter { it.genre.contains("كوميدي") }
      "دراما وتشويق" -> seriesList.filter { it.genre.contains("دراما") || it.genre.contains("تشويق") }
      "أكشن وشعبي" -> seriesList.filter { it.genre.contains("أكشن") || it.genre.contains("شعبي") }
      "تاريخي ورعب" -> seriesList.filter { it.genre.contains("تاريخي") || it.genre.contains("رعب") }
      else -> seriesList
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(JokerBackground),
    contentPadding = PaddingValues(bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Ramadan Header Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(JokerGold.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Nightlight,
              contentDescription = null,
              tint = JokerGold,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "مسلسلات رمضان المبارك",
              color = TextPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "جميع المسلسلات الرمضانية التي نزلت كاملة بجميع الحلقات وسيرفرات سريعة",
              color = TextSecondary,
              fontSize = 11.sp,
              lineHeight = 16.sp
            )
          }
        }
      }
    }

    // Filter Chips
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
            modifier = Modifier.clickable { selectedFilter = filter }
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

    // Series Grid in Rows (2 per row)
    val chunked = filteredSeries.chunked(2)
    items(chunked) { rowItems ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        rowItems.forEach { series ->
          SeriesCard(
            series = series,
            isFavorite = isFavorite(series.id),
            onToggleFavorite = { onToggleFavorite(series) },
            onClick = { onSelectSeries(series) },
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
