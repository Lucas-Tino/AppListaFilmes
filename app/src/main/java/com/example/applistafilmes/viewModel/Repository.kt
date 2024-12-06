package com.example.applistafilmes.viewModel

import com.example.applistafilmes.roomDB.Filme
import com.example.applistafilmes.roomDB.FilmeDataBase

class Repository(private val db: FilmeDataBase) {
    suspend fun upsertFilme(filme: Filme) {
        db.filmeDao().upsertFilme(filme)
    }

    suspend fun deleteFilme(filme: Filme) {
        db.filmeDao().deleteFilme(filme)
    }

    fun getAllFilmes() = db.filmeDao().getAllFilmes()

    fun getFilme(id: Int) = db.filmeDao().getFilme(id)
}