package dev.aldi.diyiotwithmqtt.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Control(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "type") val type: String?,
    @ColumnInfo(name = "subscribe_topic") val subscribeTopic: String?,
    @ColumnInfo(name = "publish_topic") val publishTopic: String?,
    @ColumnInfo(name = "is_retain") val is_retain: Boolean
)
