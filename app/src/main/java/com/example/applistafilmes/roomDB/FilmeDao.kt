package com.example.applistafilmes.roomDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmeDao {
    @Upsert
    suspend fun upsertFilme(filme: Filme)

    @Delete
    suspend fun deleteFilme(filme: Filme)

    @Query("SELECT * FROM Filme")
    fun getAllFilmes(): Flow<List<Filme>>

    @Query("SELECT * FROM Filme WHERE id = :id")
    fun getFilme(id: Int): Filme
}