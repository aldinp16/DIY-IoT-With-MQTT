package dev.aldi.diyiotwithmqtt

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.aldi.diyiotwithmqtt.dao.BrokerDao
import dev.aldi.diyiotwithmqtt.dao.ControlDao
import dev.aldi.diyiotwithmqtt.entity.Broker
import dev.aldi.diyiotwithmqtt.entity.Control
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val DATABASE_NAME = "db_diyiotmqtt"

@Database(entities = [Control::class, Broker::class], version = 1)
abstract class AppDatabase  : RoomDatabase() {
    abstract fun controlDao(): ControlDao
    abstract fun brokerDao(): BrokerDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }

        }

        class AppDatabaseCallback (private val scope: CoroutineScope): RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.brokerDao())
                    }
                }
            }

            suspend fun populateDatabase(brokerDao: BrokerDao) {
                val broker = Broker(1, "broker.emqx.io", 1883, "", "")
                brokerDao.save(broker)
            }
        }

    }
}