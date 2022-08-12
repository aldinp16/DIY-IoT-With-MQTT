package dev.aldi.diyiotwithmqtt.dao

import androidx.room.*
import dev.aldi.diyiotwithmqtt.entity.Control

@Dao
interface ControlDao {
    @Query("SELECT * FROM control")
    suspend fun getAll(): List<Control>

    @Query("SELECT * FROM control WHERE id = :id LIMIT 1")
    suspend fun findByUid(id: Int): Control

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg control: Control)

    @Update
    suspend fun update(vararg control: Control)

    @Delete
    suspend fun delete(control: Control)
}