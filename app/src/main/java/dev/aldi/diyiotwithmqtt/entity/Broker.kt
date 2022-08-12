package dev.aldi.diyiotwithmqtt.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Broker(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "host") val host: String?,
    @ColumnInfo(name = "port") val port: Int?,
    @ColumnInfo(name = "username") val username: String?,
    @ColumnInfo(name = "password") val password: String?
)
