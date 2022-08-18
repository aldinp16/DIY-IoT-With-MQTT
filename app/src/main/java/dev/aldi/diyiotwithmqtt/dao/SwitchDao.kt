package dev.aldi.diyiotwithmqtt.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.aldi.diyiotwithmqtt.entity.Switch

@Dao
interface SwitchDao {
    @Insert
    suspend fun save(vararg button: Switch)

    @Query("SELECT * FROM Switch WHERE control_id = :controlId")
    suspend fun get(controlId: Int): Switch
}