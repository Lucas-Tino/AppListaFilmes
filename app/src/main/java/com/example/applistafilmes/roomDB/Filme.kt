package com.example.applistafilmes.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Filme(
    val nome: String,
    val diretor: String,
    val genero: String,
    val ano: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
