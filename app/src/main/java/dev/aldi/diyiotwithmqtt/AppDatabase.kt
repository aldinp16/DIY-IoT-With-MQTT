package dev.aldi.diyiotwithmqtt

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.aldi.diyiotwithmqtt.dao.BrokerDao
import dev.aldi.diyiotwithmqtt.dao.ControlDao
import dev.aldi.diyiotwithmqtt.entity.Broker
import dev.aldi.diyiotwithmqtt.entity.Control

@Database(entities = [Control::class, Broker::class], version = 1)
abstract class AppDatabase  : RoomDatabase() {
    abstract fun controlDao(): ControlDao
    abstract fun brokerDao(): BrokerDao
}