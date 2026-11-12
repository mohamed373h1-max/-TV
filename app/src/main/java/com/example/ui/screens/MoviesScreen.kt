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
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.MovieCreation
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
import com.example.model.EgyptianMovie
import com.example.ui.components.MovieCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.JokerBackground
import com.example.ui.theme.JokerCyanAccent
import com.example.ui.theme.JokerGold
import com.example.ui.theme.JokerPurpleLight
import com.example.ui.theme.JokerPurplePrimary
import com.example.ui.theme.JokerSurfaceElevated
import com.example.ui.theme.JokerSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MoviesScreen(
  moviesList: List<EgyptianMovie>,
  isFavorite: (String) -> Boolean,
  onToggleFavorite: (Any) -> Unit,
  onSelectMovie: (EgyptianMovie) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedGenre by remember { mutableStateOf("الكل") }
  var selectedYear by remember { mutableStateOf("الكل") }

  val genreFilters = listOf("الكل", "كوميدي", "أكشن وإثارة", "دراما", "حربي ووطني", "تاريخي وفانتازيا")
  val yearFilters = listOf("الكل", "2024", "2023")

  val filteredMovies = remember(selectedGenre, selectedYear, moviesList) {
    moviesList.filter { movie ->
      val matchGenre = when (selectedGenre) {
        "الكل" -> true
        "كوميدي" -> movie.genre.contains("كوميدي")
        "أكشن وإثارة" -> movie.genre.contains("أكشن") || movie.genre.contains("إثارة")
        "دراما" -> movie.genre.contains("دراما")
        "حربي ووطني" -> movie.genre.contains("حربي") || movie.genre.contains("وطني")
        "تاريخي وفانتازيا" -> movie.genre.contains("تاريخي") || movie.genre.contains("فانتازيا")
        else -> true
      }
      val matchYear = when (selectedYear) {
        "الكل" -> true
        else -> movie.releaseYear.toString() == selectedYear
      }
      matchGenre && matchYear
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(JokerBackground),
    contentPadding = PaddingValues(bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Cinema Header
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
              .background(JokerCyanAccent.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.LocalMovies,
              contentDescription = null,
              tint = JokerCyanAccent,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "السينما المصرية والأفلام الحديثة",
              color = TextPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "جميع أفلام السينما المصرية التي نزلت في دور العرض بجودة عالية وسيرفرات فائقة",
              color = TextSecondary,
              fontSize = 11.sp,
              lineHeight = 16.sp
            )
          }
        }
      }
    }

    // Genre Filters
    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(genreFilters) { genre ->
          val isSelected = genre == selectedGenre
          Surface(
            color = if (isSelected) JokerPurplePrimary else JokerSurfaceElevated,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.clickable { selectedGenre = genre }
          ) {
            Text(
              text = genre,
              color = if (isSelected) Color.White else TextSecondary,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
          }
        }
      }
    }

    // Year Sub-Filter Row
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "سنة الإنتاج:",
          color = TextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          yearFilters.forEach { y ->
            val isSelected = y == selectedYear
            Surface(
              color = if (isSelected) JokerCyanAccent.copy(alpha = 0.2f) else JokerSurfaceVariant,
              shape = RoundedCornerShape(8.dp),
              border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, JokerCyanAccent) else null,
              modifier = Modifier.clickable { selectedYear = y }
            ) {
              Text(
                text = y,
                color = if (isSelected) JokerCyanAccent else TextMuted,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }

    // Movies Grid (2 per row)
    val chunked = filteredMovies.chunked(2)
    items(chunked) { rowItems ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        rowItems.forEach { movie ->
          MovieCard(
            movie = movie,
            isFavorite = isFavorite(movie.id),
            onToggleFavorite = { onToggleFavorite(movie) },
            onClick = { onSelectMovie(movie) },
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
