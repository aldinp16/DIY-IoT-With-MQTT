package dev.aldi.diyiotwithmqtt.dao

import androidx.room.*
import dev.aldi.diyiotwithmqtt.entity.Control

@Dao
interface ControlDao {
    @Query("SELECT * FROM control")
    fun getAll(): List<Control>

    @Query("SELECT * FROM control WHERE id = :id LIMIT 1")
    fun findByUid(id: Int): Control

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg control: Control)

    @Update
    fun update(vararg control: Control)

    @Delete
    fun delete(control: Control)
}