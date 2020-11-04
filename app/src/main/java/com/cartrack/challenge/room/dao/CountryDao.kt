package com.cartrack.challenge.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.cartrack.challenge.room.entities.Country

@Dao
interface CountryDao {
    @Query("SELECT * FROM country")
    fun getAll(): List<Country>

    @Insert
    fun insertAll(vararg countries: Country)

    @Delete
    fun delete(country: Country)
}