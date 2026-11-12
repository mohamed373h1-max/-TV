package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import com.example.model.UserComment
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

@Composable
fun RatingAndCommentsSection(
  itemId: String,
  averageRating: Float,
  ratingCount: Int,
  comments: List<UserComment>,
  onAddComment: (author: String, rating: Int, text: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var showAddCommentDialog by remember { mutableStateOf(false) }

  Column(modifier = modifier.fillMaxWidth()) {
    // Section Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "التقييمات وآراء المشاهدين",
        color = TextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )

      Button(
        onClick = { showAddCommentDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = JokerPurplePrimary),
        shape = RoundedCornerShape(20.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.AddComment,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "قيّم الآن", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    // Rating Overview Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      colors = CardDefaults.cardColors(containerColor = JokerSurfaceVariant),
      shape = RoundedCornerShape(16.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Average score
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(end = 16.dp)
        ) {
          Text(
            text = String.format("%.1f", averageRating),
            color = TextPrimary,
            fontSize = 38.sp,
            fontWeight = FontWeight.Black
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(5) { index ->
              val starValue = index + 1
              Icon(
                imageVector = when {
                  averageRating >= starValue -> Icons.Default.Star
                  averageRating >= starValue - 0.5f -> Icons.Default.StarHalf
                  else -> Icons.Default.StarBorder
                },
                contentDescription = null,
                tint = JokerGold,
                modifier = Modifier.size(16.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "$ratingCount تقييم",
            color = TextSecondary,
            fontSize = 11.sp
          )
        }

        // Breakdown bars
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          RatingBarRow(stars = "5 نجوم", fraction = 0.85f)
          RatingBarRow(stars = "4 نجوم", fraction = 0.10f)
          RatingBarRow(stars = "3 نجوم", fraction = 0.03f)
          RatingBarRow(stars = "2 نجوم", fraction = 0.01f)
          RatingBarRow(stars = "1 نجمة", fraction = 0.01f)
        }
      }
    }

    // Comments List
    if (comments.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "كن أول من يكتب مراجعة ويقيّم هذا العمل!",
          color = TextSecondary,
          fontSize = 13.sp
        )
      }
    } else {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        comments.forEach { comment ->
          CommentItemCard(comment = comment)
        }
      }
    }
  }

  // Add Comment Dialog
  if (showAddCommentDialog) {
    AddCommentDialog(
      onDismiss = { showAddCommentDialog = false },
      onSubmit = { author, rating, text ->
        onAddComment(author, rating, text)
        showAddCommentDialog = false
      }
    )
  }
}

@Composable
private fun RatingBarRow(stars: String, fraction: Float) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth()
  ) {
    Text(
      text = stars,
      color = TextSecondary,
      fontSize = 10.sp,
      modifier = Modifier.width(42.dp)
    )
    LinearProgressIndicator(
      progress = { fraction },
      modifier = Modifier
        .weight(1f)
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp)),
      color = JokerGold,
      trackColor = Color.White.copy(alpha = 0.1f)
    )
  }
}

@Composable
fun CommentItemCard(comment: UserComment) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = JokerSurfaceElevated),
    shape = RoundedCornerShape(14.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Avatar circle
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(JokerPurplePrimary.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = comment.authorName.take(1),
              color = JokerPurpleLight,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = comment.authorName,
              color = TextPrimary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = comment.timestampText,
              color = TextMuted,
              fontSize = 10.sp
            )
          }
        }

        // Stars
        Row {
          repeat(5) { i ->
            Icon(
              imageVector = if (i < comment.rating) Icons.Default.Star else Icons.Default.StarBorder,
              contentDescription = null,
              tint = JokerGold,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = comment.commentText,
        color = TextPrimary.copy(alpha = 0.9f),
        fontSize = 13.sp,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        var liked by remember { mutableStateOf(false) }
        var likeCount by remember { mutableIntStateOf(comment.likes) }

        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
              liked = !liked
              likeCount += if (liked) 1 else -1
            }
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.ThumbUp,
            contentDescription = "إعجاب",
            tint = if (liked) JokerCyanAccent else TextMuted,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "$likeCount",
            color = if (liked) JokerCyanAccent else TextMuted,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

@Composable
fun AddCommentDialog(
  onDismiss: () -> Unit,
  onSubmit: (author: String, rating: Int, text: String) -> Unit
) {
  var authorName by remember { mutableStateOf("") }
  var commentText by remember { mutableStateOf("") }
  var rating by remember { mutableIntStateOf(5) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = JokerSurface,
      border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Text(
          text = "إضافة تقييم ورأي",
          color = TextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "شارك رأيك وتقييمك مع مجتمع جوكر TV",
          color = TextSecondary,
          fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Star picker
        Text(
          text = "اختر التقييم (1 إلى 5 نجوم):",
          color = TextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxWidth()
        ) {
          (1..5).forEach { starIndex ->
            Icon(
              imageVector = if (starIndex <= rating) Icons.Default.Star else Icons.Default.StarBorder,
              contentDescription = "$starIndex نجوم",
              tint = JokerGold,
              modifier = Modifier
                .size(38.dp)
                .clickable { rating = starIndex }
                .padding(4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Name input
        OutlinedTextField(
          value = authorName,
          onValueChange = { authorName = it },
          label = { Text("اسمك أو لقبك") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = JokerPurplePrimary,
            unfocusedBorderColor = BorderSubtle,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Comment input
        OutlinedTextField(
          value = commentText,
          onValueChange = {
            commentText = it
            if (it.isNotBlank()) errorMessage = null
          },
          label = { Text("اكتب رأيك بالتفصيل...") },
          minLines = 3,
          maxLines = 5,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = JokerPurplePrimary,
            unfocusedBorderColor = BorderSubtle,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = errorMessage ?: "",
            color = Color.Red,
            fontSize = 11.sp
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = onDismiss) {
            Text(text = "إلغاء", color = TextSecondary)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (commentText.isBlank()) {
                errorMessage = "يرجى كتابة نص التعليق"
              } else {
                onSubmit(
                  authorName.ifBlank { "مشاهد جوكر TV" },
                  rating,
                  commentText.trim()
                )
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = JokerPurplePrimary),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(text = "نشر التقييم", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
