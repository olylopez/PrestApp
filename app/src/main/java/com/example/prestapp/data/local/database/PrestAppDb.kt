package com.example.prestapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.prestapp.data.local.dao.ClienteDao
import com.example.prestapp.data.local.dao.PagoDao
import com.example.prestapp.data.local.dao.PrestamoDao
import com.example.prestapp.data.local.dao.RutaDao
import com.example.prestapp.data.local.entities.RutaEntity
import com.example.prestapp.data.local.entities.ClienteEntity
import com.example.prestapp.data.local.entities.PagoEntity
import com.example.prestapp.data.local.entities.PrestamoEntity
import com.example.prestapp.presentation.componentes.BigDecimalConverter

@Database(
    entities = [
        RutaEntity::class,
        ClienteEntity::class,
        PrestamoEntity::class,
        PagoEntity::class 
    ],
    version = 8,
    exportSchema = false
)
@TypeConverters(BigDecimalConverter::class)
abstract class PrestAppDb : RoomDatabase(){
    abstract fun rutaDao(): RutaDao
    abstract fun clienteDao(): ClienteDao
    abstract fun prestamoDao(): PrestamoDao
    abstract fun pagoDao(): PagoDao

}