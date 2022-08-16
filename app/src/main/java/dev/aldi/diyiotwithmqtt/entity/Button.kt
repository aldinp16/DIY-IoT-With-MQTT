package dev.aldi.diyiotwithmqtt.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Button(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "control_id") val controlId: Int,
    @ColumnInfo(name = "payload") val payload: String?
)
