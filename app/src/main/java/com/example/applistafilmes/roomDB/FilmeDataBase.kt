package com.example.applistafilmes.roomDB

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Filme::class],
    version = 1
)

abstract class FilmeDataBase : RoomDatabase() {
    abstract fun filmeDao() : FilmeDao
}