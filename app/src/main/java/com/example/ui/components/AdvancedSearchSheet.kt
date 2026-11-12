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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EgyptianMovie
import com.example.model.FootballMatch
import com.example.model.MediaType
import com.example.model.RamadanSeries
import com.example.model.SportsChannel
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.JokerCyanAccent
import com.example.ui.theme.JokerGold
import com.example.ui.theme.JokerPurpleLight
import com.example.ui.theme.JokerPurplePrimary
import com.example.ui.theme.JokerSurface
import com.example.ui.theme.JokerSurfaceElevated
import com.example.ui.theme.JokerSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

sealed class SearchResultItem {
  data class ChannelResult(val channel: SportsChannel) : SearchResultItem()
  data class MatchResult(val match: FootballMatch) : SearchResultItem()
  data class SeriesResult(val series: RamadanSeries) : SearchResultItem()
  data class MovieResult(val movie: EgyptianMovie) : SearchResultItem()
}

@Composable
fun AdvancedSearchSheet(
  allChannels: List<SportsChannel>,
  allMatches: List<FootballMatch>,
  allSeries: List<RamadanSeries>,
  allMovies: List<EgyptianMovie>,
  onClose: () -> Unit,
  onSelectChannel: (SportsChannel) -> Unit,
  onSelectMatch: (FootballMatch) -> Unit,
  onSelectSeries: (RamadanSeries) -> Unit,
  onSelectMovie: (EgyptianMovie) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedTypeFilter by remember { mutableStateOf("الكل") }
  var selectedYearFilter by remember { mutableStateOf("كل السنوات") }
  var selectedGenreFilter by remember { mutableStateOf("كل التصنيفات") }

  val typeFilters = listOf("الكل", "قنوات رياضية", "مسلسلات رمضان", "أفلام سينما")
  val yearFilters = listOf("كل السنوات", "2025", "2024", "2023")
  val genreFilters = listOf("كل التصنيفات", "أكشن", "كوميدي", "دراما", "تاريخي")

  // Filtered Results calculation
  val results = remember(searchQuery, selectedTypeFilter, selectedYearFilter, selectedGenreFilter) {
    val q = searchQuery.trim().lowercase()
    val list = mutableListOf<SearchResultItem>()

    // Channels
    if (selectedTypeFilter == "الكل" || selectedTypeFilter == "قنوات رياضية") {
      if (selectedYearFilter == "كل السنوات" && selectedGenreFilter == "كل التصنيفات") {
        allChannels.filter {
          q.isEmpty() || it.name.lowercase().contains(q) || it.currentEvent.lowercase().contains(q) || it.category.lowercase().contains(q)
        }.forEach { list.add(SearchResultItem.ChannelResult(it)) }

        allMatches.filter {
          q.isEmpty() || it.homeTeam.lowercase().contains(q) || it.awayTeam.lowercase().contains(q) || it.championship.lowercase().contains(q) || it.commentator.lowercase().contains(q)
        }.forEach { list.add(SearchResultItem.MatchResult(it)) }
      }
    }

    // Series
    if (selectedTypeFilter == "الكل" || selectedTypeFilter == "مسلسلات رمضان") {
      allSeries.filter { series ->
        val matchesQuery = q.isEmpty() || series.title.lowercase().contains(q) || series.genre.lowercase().contains(q) || series.cast.any { it.lowercase().contains(q) } || series.director.lowercase().contains(q)
        val matchesYear = selectedYearFilter == "كل السنوات" || series.seasonYear.toString() == selectedYearFilter
        val matchesGenre = selectedGenreFilter == "كل التصنيفات" || series.genre.contains(selectedGenreFilter)
        matchesQuery && matchesYear && matchesGenre
      }.forEach { list.add(SearchResultItem.SeriesResult(it)) }
    }

    // Movies
    if (selectedTypeFilter == "الكل" || selectedTypeFilter == "أفلام سينما") {
      allMovies.filter { movie ->
        val matchesQuery = q.isEmpty() || movie.title.lowercase().contains(q) || movie.genre.lowercase().contains(q) || movie.cast.any { it.lowercase().contains(q) } || movie.director.lowercase().contains(q)
        val matchesYear = selectedYearFilter == "كل السنوات" || movie.releaseYear.toString() == selectedYearFilter
        val matchesGenre = selectedGenreFilter == "كل التصنيفات" || movie.genre.contains(selectedGenreFilter)
        matchesQuery && matchesYear && matchesGenre
      }.forEach { list.add(SearchResultItem.MovieResult(it)) }
    }

    list
  }

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
          imageVector = Icons.Default.FilterList,
          contentDescription = null,
          tint = JokerPurpleLight,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "البحث المتقدم والفلاتر",
          color = TextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
      }

      IconButton(onClick = onClose) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "إغلاق",
          tint = TextSecondary
        )
      }
    }

    // Search input
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      placeholder = { Text("ابحث عن قناة، مسلسل، فيلم، ممثل أو معلق...", color = TextMuted, fontSize = 13.sp) },
      leadingIcon = {
        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = JokerPurplePrimary)
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { searchQuery = "" }) {
            Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح", tint = TextMuted)
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(16.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = JokerPurplePrimary,
        unfocusedBorderColor = BorderSubtle,
        focusedContainerColor = JokerSurfaceVariant,
        unfocusedContainerColor = JokerSurfaceVariant,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
      )
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Type Filter Pills
    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(typeFilters) { type ->
        val selected = type == selectedTypeFilter
        Surface(
          color = if (selected) JokerPurplePrimary else JokerSurfaceElevated,
          shape = RoundedCornerShape(20.dp),
          modifier = Modifier.clickable { selectedTypeFilter = type }
        ) {
          Text(
            text = type,
            color = if (selected) Color.White else TextSecondary,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Year & Genre Filter Pills in Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Year filter scroll
      LazyRow(
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        items(yearFilters) { year ->
          val selected = year == selectedYearFilter
          Surface(
            color = if (selected) JokerCyanAccent.copy(alpha = 0.2f) else JokerSurfaceVariant,
            shape = RoundedCornerShape(8.dp),
            border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, JokerCyanAccent) else null,
            modifier = Modifier.clickable { selectedYearFilter = year }
          ) {
            Text(
              text = year,
              color = if (selected) JokerCyanAccent else TextMuted,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      // Genre filter scroll
      LazyRow(
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        items(genreFilters) { genre ->
          val selected = genre == selectedGenreFilter
          Surface(
            color = if (selected) JokerGold.copy(alpha = 0.2f) else JokerSurfaceVariant,
            shape = RoundedCornerShape(8.dp),
            border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, JokerGold) else null,
            modifier = Modifier.clickable { selectedGenreFilter = genre }
          ) {
            Text(
              text = genre,
              color = if (selected) JokerGold else TextMuted,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }
    }

    // Results count bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "تم العثور على (${results.size}) نتيجة",
        color = TextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
      )

      if (searchQuery.isNotEmpty() || selectedTypeFilter != "الكل" || selectedYearFilter != "كل السنوات" || selectedGenreFilter != "كل التصنيفات") {
        Text(
          text = "إعادة ضبط الفلاتر",
          color = JokerCyanAccent,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.clickable {
            searchQuery = ""
            selectedTypeFilter = "الكل"
            selectedYearFilter = "كل السنوات"
            selectedGenreFilter = "كل التصنيفات"
          }
        )
      }
    }

    // Results list
    if (results.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(48.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "لم نجد أي نتائج مطابقة للبحث أو الفلتر",
            color = TextSecondary,
            fontSize = 14.sp
          )
          Text(
            text = "جرّب تغيير كلمات البحث أو إعادة ضبط الفلاتر",
            color = TextMuted,
            fontSize = 12.sp
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(results) { item ->
          when (item) {
            is SearchResultItem.ChannelResult -> {
              SearchResultRow(
                icon = Icons.Default.LiveTv,
                title = item.channel.name,
                subtitle = item.channel.currentEvent,
                badge = "قناة رياضية",
                badgeColor = JokerPurplePrimary,
                onClick = { onSelectChannel(item.channel) }
              )
            }
            is SearchResultItem.MatchResult -> {
              SearchResultRow(
                icon = Icons.Default.SportsSoccer,
                title = "${item.match.homeTeam} ضد ${item.match.awayTeam}",
                subtitle = "${item.match.championship} • معلق: ${item.match.commentator}",
                badge = if (item.match.isLive) "مباشر" else item.match.timeText,
                badgeColor = if (item.match.isLive) Color.Red else JokerCyanAccent,
                onClick = { onSelectMatch(item.match) }
              )
            }
            is SearchResultItem.SeriesResult -> {
              SearchResultRow(
                icon = Icons.Default.Tv,
                title = item.series.title,
                subtitle = "رمضان ${item.series.seasonYear} • ${item.series.genre} • بطولة: ${item.series.cast.take(2).joinToString("، ")}",
                badge = "${item.series.rating} ★",
                badgeColor = JokerGold,
                onClick = { onSelectSeries(item.series) }
              )
            }
            is SearchResultItem.MovieResult -> {
              SearchResultRow(
                icon = Icons.Default.LocalMovies,
                title = item.movie.title,
                subtitle = "سينما ${item.movie.releaseYear} • ${item.movie.genre} • بطولة: ${item.movie.cast.take(2).joinToString("، ")}",
                badge = "${item.movie.rating} ★",
                badgeColor = JokerGold,
                onClick = { onSelectMovie(item.movie) }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SearchResultRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  badge: String,
  badgeColor: Color,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
    shape = RoundedCornerShape(14.dp),
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
          .size(42.dp)
          .clip(CircleShape)
          .background(badgeColor.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(imageVector = icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(22.dp))
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = title,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            color = badgeColor.copy(alpha = 0.2f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = badge,
              color = badgeColor,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = subtitle,
          color = TextSecondary,
          fontSize = 11.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}
