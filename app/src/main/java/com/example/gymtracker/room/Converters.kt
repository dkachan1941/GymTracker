package com.example.gymtracker.room

import androidx.room.TypeConverter
import com.example.gymtracker.model.Exercise
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromExercise(exercises: List<Exercise?>): String =
        Gson().toJson(exercises)

    @TypeConverter
    fun toExercise(value: String): List<Exercise?> =
        Gson().fromJson(value, (object : TypeToken<List<Exercise?>>() {}).type)
}