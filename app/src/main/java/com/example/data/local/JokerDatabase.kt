package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
  entities = [FavoriteEntity::class],
  version = 1,
  exportSchema = false
)
@TypeConverters(JokerTypeConverters::class)
abstract class JokerDatabase : RoomDatabase() {

  abstract fun favoriteDao(): FavoriteDao

  companion object {
    @Volatile
    private var INSTANCE: JokerDatabase? = null

    fun getInstance(context: Context): JokerDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          JokerDatabase::class.java,
          "joker_tv_database"
        ).build()
        INSTANCE = instance
        instance
      }
    }
  }
}
