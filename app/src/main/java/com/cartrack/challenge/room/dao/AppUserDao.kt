package com.cartrack.challenge.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.cartrack.challenge.room.entities.AppUser

@Dao
interface AppUserDao {
    @Query("SELECT * FROM appUser")
    fun getAll(): List<AppUser>

    @Insert
    fun insertAll(vararg users: AppUser)

    @Delete
    fun delete(user: AppUser)
}