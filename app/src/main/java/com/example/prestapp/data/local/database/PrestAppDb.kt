package com.example.prestapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.prestapp.data.local.dao.RutaDao
import com.example.prestapp.data.local.entities.RutaEntity

@Database(
    entities = [
        RutaEntity::class

    ],
    version = 2,
    exportSchema = false
)
abstract class PrestAppDb : RoomDatabase(){
    abstract fun rutaDao(): RutaDao

}