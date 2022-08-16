package dev.aldi.diyiotwithmqtt.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.aldi.diyiotwithmqtt.entity.Button

@Dao
interface ButtonDao {
    @Insert
    suspend fun save(vararg button: Button)

    @Query("SELECT * FROM Button WHERE control_id = :controlId")
    suspend fun get(controlId: Int): Button
}