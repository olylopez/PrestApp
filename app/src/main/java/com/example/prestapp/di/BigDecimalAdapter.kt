package com.example.prestapp.di

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.math.BigDecimal

class BigDecimalAdapter {

    @ToJson
    fun toJson(value: BigDecimal): String {
        return value.toPlainString()
    }

    @FromJson
    fun fromJson(value: String): BigDecimal {
        return BigDecimal(value)
    }
}