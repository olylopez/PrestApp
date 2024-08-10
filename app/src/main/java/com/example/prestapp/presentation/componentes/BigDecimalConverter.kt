package com.example.prestapp.presentation.componentes

import androidx.room.TypeConverter
import java.math.BigDecimal

class BigDecimalConverter {

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String {
        return value?.toPlainString() ?: "0.0"
    }

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal {
        return value?.let { BigDecimal(it) } ?: BigDecimal("0.0")
    }
}
