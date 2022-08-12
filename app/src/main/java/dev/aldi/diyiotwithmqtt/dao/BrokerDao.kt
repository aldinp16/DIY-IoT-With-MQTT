package dev.aldi.diyiotwithmqtt.dao

import androidx.room.*
import dev.aldi.diyiotwithmqtt.entity.Broker

@Dao
interface BrokerDao {
    @Query("SELECT * FROM broker LIMIT 1")
    suspend fun get(): Broker?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(broker: Broker)
}