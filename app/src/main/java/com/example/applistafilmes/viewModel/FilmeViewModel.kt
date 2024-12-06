package com.example.applistafilmes.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.applistafilmes.roomDB.Filme
import kotlinx.coroutines.launch

class FilmeViewModel(private val repository: Repository) : ViewModel() {
    fun getAllFilmes() = repository.getAllFilmes().asLiveData(viewModelScope.coroutineContext)

    fun getFilme(id: Int) = repository.getFilme(id)

    fun upsertFilme(filme: Filme) {
        viewModelScope.launch {
            repository.upsertFilme(filme)
        }
    }

    fun updateFilme(nome: String, diretor: String, genero: String, ano: String, id: String) {
        viewModelScope.launch {
            val filme = Filme(
                nome,
                diretor,
                genero,
                ano,
                id.toInt()
            )

            repository.upsertFilme(filme)
        }
    }

    fun deleteBook(nome: String, diretor: String, genero: String, ano: String, id: String) {
        viewModelScope.launch {
            val filme = Filme(
                nome,
                diretor,
                genero,
                ano,
                id.toInt()
            )

            repository.deleteFilme(filme)
        }
    }
}