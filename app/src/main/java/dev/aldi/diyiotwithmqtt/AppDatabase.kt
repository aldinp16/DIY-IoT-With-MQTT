package dev.aldi.diyiotwithmqtt

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.aldi.diyiotwithmqtt.dao.BrokerDao
import dev.aldi.diyiotwithmqtt.dao.ControlDao
import dev.aldi.diyiotwithmqtt.entity.Broker
import dev.aldi.diyiotwithmqtt.entity.Control

const val DATABASE_NAME = "db_diyiotmqtt"

@Database(entities = [Control::class, Broker::class], version = 1)
abstract class AppDatabase  : RoomDatabase() {
    abstract fun controlDao(): ControlDao
    abstract fun brokerDao(): BrokerDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME).build()
                INSTANCE = instance
                instance
            }

        }

    }
}