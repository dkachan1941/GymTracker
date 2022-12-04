package com.example.gymtracker.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gymtracker.model.Exercise
import com.example.gymtracker.model.Section

@Database(entities = [Section::class, Exercise::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): SectionsDao
}