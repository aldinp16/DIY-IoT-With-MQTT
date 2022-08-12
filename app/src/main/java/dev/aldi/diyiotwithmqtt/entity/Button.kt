package dev.aldi.diyiotwithmqtt.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Button(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "control_id") val controlId: Int,
    @ColumnInfo(name = "on_condition") val onCondition: String?,
    @ColumnInfo(name = "off_condition") val offCondition: String?
)
