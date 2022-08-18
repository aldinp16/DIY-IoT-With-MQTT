package dev.aldi.diyiotwithmqtt.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Switch(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "control_id") val controlId: Int,
    @ColumnInfo(name = "on_payload") val onPayload: String,
    @ColumnInfo(name = "off_paylad") val offPayload: String
)
