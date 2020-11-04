package com.cartrack.challenge.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Country(
    @PrimaryKey @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "country") val country: String
)