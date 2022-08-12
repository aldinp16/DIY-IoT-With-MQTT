package dev.aldi.diyiotwithmqtt

import android.app.Application

class MyApplication: Application() {
    val database by lazy { AppDatabase.getInstance(this) }
}