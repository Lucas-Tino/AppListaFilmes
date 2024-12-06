package com.example.applistafilmes

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import androidx.room.Room
import com.example.applistafilmes.roomDB.Filme
import com.example.applistafilmes.roomDB.FilmeDataBase
import com.example.applistafilmes.ui.theme.AppListaFilmesTheme
import com.example.applistafilmes.viewModel.FilmeViewModel
import com.example.applistafilmes.viewModel.Repository
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {

    @Serializable
    object Inicial
    @Serializable
    object Cadastro

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            FilmeDataBase::class.java,
            "filme.db"
        ).build()
    }

    public val viewModel by viewModels<FilmeViewModel> (
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T{
                    return FilmeViewModel(Repository(db)) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppListaFilmesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val act = this
                    // TelaCadastro(Modifier, viewModel, this)
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = Inicial) {
                        composable<Inicial> {
                            TelaInicial(
                                Modifier,
                                viewModel,
                                act,
                                onNavigateToCadastro = { navController.navigate(Cadastro) },
                                navController
                            )
                        }
                        composable<Cadastro> {
                            TelaCadastro(
                                Modifier,
                                viewModel,
                                act,
                                navController
                            )
                        }
                        composable(
                            "atualizar/{id}/{nome}/{diretor}/{genero}/{ano}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.IntType },
                                navArgument("nome") { type = NavType.StringType },
                                navArgument("diretor") { type = NavType.StringType },
                                navArgument("genero") { type = NavType.StringType },
                                navArgument("ano") { type = NavType.StringType },
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments!!.getInt("id")
                            val nome = backStackEntry.arguments!!.getString("nome")
                            val diretor = backStackEntry.arguments!!.getString("diretor")
                            val genero = backStackEntry.arguments!!.getString("genero")
                            val ano = backStackEntry.arguments!!.getString("ano")

                            if (nome != null) {
                                if (diretor != null) {
                                    if (genero != null) {
                                        if (ano != null) {
                                            TelaAtualizar(
                                                Modifier,
                                                viewModel,
                                                act,
                                                id,
                                                nome,
                                                diretor,
                                                genero,
                                                ano,
                                                navController
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TelaInicial(
        modifier: Modifier = Modifier,
        viewModel: FilmeViewModel,
        mainActivity: MainActivity,
        onNavigateToCadastro: () -> Unit,
        navHostController: NavHostController
    ) {
        var filmeList by remember {
            mutableStateOf(listOf<Filme>())
        }

        viewModel.getAllFilmes().observe(mainActivity) {
            filmeList = it
        }

        val context = LocalContext.current

        Column(
            Modifier
                .background(Color(0, 41, 102))
                .fillMaxHeight()
        ) {
            CenterAlignedTopAppBar(
                title = {Text(
                    text = "Lista de Filmes",
                    color = Color(255, 209, 26),
                    fontSize = 30.sp
                ) },
                actions = {
                    IconButton(onClick = {onNavigateToCadastro()}) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Novo",
                            modifier = Modifier.padding(end = 2.dp)
                        )
                    }
                },
                colors = TopAppBarColors(
                    containerColor = Color(0, 31, 77),
                    scrolledContainerColor = Color(0, 31, 77),
                    navigationIconContentColor = Color(255, 209, 26),
                    titleContentColor = Color(255, 209, 26),
                    actionIconContentColor = Color(255, 209, 26)
                ),
                modifier = modifier
            )

            Row (
                modifier.padding(3.dp)
            ){

            }
            Column (
                Modifier
                    .padding(24.dp)
            ){
                LazyColumn {
                    items(filmeList) { filme ->
                        Row(
                            Modifier
                                .fillMaxWidth(1f)
                                .padding(vertical = 8.dp),
                            Arrangement.Center
                        ) {
                            Card(
                                modifier = Modifier,
                                colors = CardColors(
                                    containerColor = Color(0, 60, 140),
                                    contentColor = Color.White,
                                    disabledContentColor = Color.White,
                                    disabledContainerColor = Color.DarkGray
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "${filme.nome}",
                                            color = Color(255, 209, 26),
                                            style = MaterialTheme.typography.titleLarge,
                                        )
                                        Spacer(Modifier.weight(1f))
                                        Icon(
                                            Icons.Rounded.Close,
                                            contentDescription = "Deletar Filme",
                                            Modifier
                                                .clickable {
                                                    viewModel.deleteBook(
                                                        filme.nome,
                                                        filme.diretor,
                                                        filme.genero,
                                                        filme.ano,
                                                        filme.id.toString()
                                                    )
                                                },
                                            tint = Color(255, 209, 26)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Diretor: ${filme.diretor}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Gênero: ${filme.genero}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Ano de Lançamento: ${filme.ano}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Button(
                                            onClick = {
                                                navHostController.navigate(
                                                    "atualizar/${filme.id}/${filme.nome}/${filme.diretor}/${filme.genero}/${filme.ano}",
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(255, 209, 26)),
                                            modifier = Modifier
                                                .align(alignment = Alignment.End)
                                        ) {
                                            Text(
                                                text = "Atualizar",
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.weight(3f))
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TelaCadastro(
        modifier: Modifier = Modifier,
        viewModel: FilmeViewModel,
        mainActivity: MainActivity,
        navHostController: NavHostController
    ) {
        var id by remember {
            mutableStateOf("")
        }
        var nome by remember {
            mutableStateOf("")
        }
        var diretor by remember {
            mutableStateOf("")
        }
        var genero by remember {
            mutableStateOf("")
        }
        var ano by remember {
            mutableStateOf("")
        }
        val filme = Filme(
            nome,
            diretor,
            genero,
            ano
        )

        var filmeList by remember {
            mutableStateOf(listOf<Filme>())
        }

        viewModel.getAllFilmes().observe(mainActivity) {
            filmeList = it
        }

        val context = LocalContext.current

        Column(
            Modifier
                .background(Color(0, 41, 102))
                .fillMaxHeight()
        ) {
            CenterAlignedTopAppBar(
                title = {Text(
                    text = "Adicionar Filme",
                    color = Color(255, 209, 26),
                    fontSize = 30.sp
                ) },
                navigationIcon = {
                    IconButton(onClick = {navHostController.popBackStack()}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                },
                colors = TopAppBarColors(
                    containerColor = Color(0, 31, 77),
                    scrolledContainerColor = Color(0, 31, 77),
                    navigationIconContentColor = Color(255, 209, 26),
                    titleContentColor = Color(255, 209, 26),
                    actionIconContentColor = Color(255, 209, 26)
                ),
                modifier = modifier
            )

            Row(
                Modifier
                    .padding(15.dp)
            ) {

            }
            Row(
                Modifier
                    .fillMaxWidth(),
                Arrangement.Center
            ) {
                TextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do Filme") },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color(255, 209, 26),
                        unfocusedLabelColor = Color.White,
                        unfocusedContainerColor = Color(0, 60, 140),
                        unfocusedTextColor = Color.White,

                        focusedIndicatorColor = Color(255, 209, 26),
                        focusedLabelColor = Color(255, 209, 26),
                        focusedContainerColor = Color(0, 60, 140),
                        focusedTextColor = Color.White,

                        cursorColor = Color(255, 209, 26)
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
            }

            Row(
                Modifier
                    .padding(15.dp)
            ) {

            }
            Row(
                Modifier
                    .fillMaxWidth(),
                Arrangement.Center
            ) {
                TextField(
                    value = diretor,
                    onValueChange = { diretor = it },
                    label = { Text("Nome do Diretor") },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color(255, 209, 26),
                        unfocusedLabelColor = Color.White,
                        unfocusedContainerColor = Color(0, 60, 140),
                        unfocusedTextColor = Color.White,

                        focusedIndicatorColor = Color(255, 209, 26),
                        focusedLabelColor = Color(255, 209, 26),
                        focusedContainerColor = Color(0, 60, 140),
                        focusedTextColor = Color.White,

                        cursorColor = Color(255, 209, 26)
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
            }

            Row(
                Modifier
                    .padding(15.dp)
            ) {

            }
            Row(
                Modifier
                    .fillMaxWidth(),
                Arrangement.Center
            ) {
                TextField(
                    value = genero,
                    onValueChange = { genero = it },
                    label = { Text("Gênero") },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color(255, 209, 26),
                        unfocusedLabelColor = Color.White,
                        unfocusedContainerColor = Color(0, 60, 140),
                        unfocusedTextColor = Color.White,

                        focusedIndicatorColor = Color(255, 209, 26),
                        focusedLabelColor = Color(255, 209, 26),
                        focusedContainerColor = Color(0, 60, 140),
                        focusedTextColor = Color.White,

                        cursorColor = Color(255, 209, 26)
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
            }

            Row(
                Modifier
                    .padding(15.dp)
            ) {

            }
            Row(
                Modifier
                    .fillMaxWidth(),
                Arrangement.Center
            ) {
                TextField(
                    value = ano,
                    onValueChange = { ano = it },
                    label = { Text("Ano de lançamento") },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color(255, 209, 26),
                        unfocusedLabelColor = Color.White,
                        unfocusedContainerColor = Color(0, 60, 140),
                        unfocusedTextColor = Color.White,

                        focusedIndicatorColor = Color(255, 209, 26),
                        focusedLabelColor = Color(255, 209, 26),
                        focusedContainerColor = Color(0, 60, 140),
                        focusedTextColor = Color.White,

                        cursorColor = Color(255, 209, 26)
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true
                )
            }
            Row(
                Modifier
                    .padding(15.dp)
            ) {

            }

            Row(
                Modifier
                    .fillMaxWidth(),
                Arrangement.Center
            ) {
                val botaoSalvar = Button(
                    onClick = {
                        viewModel.upsertFilme(filme)
                        id = ""
                        nome = ""
                        diretor = ""
                        genero = ""
                        ano = ""
                        navHostController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(255, 209, 26)),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "Salvar",
                        color = Color.White
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TelaAtualizar(
        modifier: Modifier = Modifier,
        viewModel: FilmeViewModel,
        mainActivity: MainActivity,
        codigo: Int,
        inome: String,
        idiretor: String,
        igenero: String,
        iano: String,
        navHostController: NavHostController
    ) {
        var id by remember {
            mutableStateOf("")
        }
        var nome by remember {
            mutableStateOf("")
        }
        var diretor by remember {
            mutableStateOf("")
        }
        var genero by remember {
            mutableStateOf("")
        }
        var ano by remember {
            mutableStateOf("")
        }

        LaunchedEffect(codigo, inome, idiretor, igenero, iano) {
            id = codigo.toString()
            nome = inome
            diretor = idiretor
            genero = igenero
            ano = iano
        }

        val context = LocalContext.current

        Column(
            Modifier
                .background(Color(0, 41, 102))
                .fillMaxHeight()
        ) {
            CenterAlignedTopAppBar(
                title = {Text(
                    text = "Atualizar Filme",
                    color = Color(255, 209, 26),
                    fontSize = 30.sp
                ) },
                navigationIcon = {
                    IconButton(onClick = {navHostController.popBackStack()}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                },
                colors = TopAppBarColors(
                    containerColor = Color(0, 31, 77),
                    scrolledContainerColor = Color(0, 31, 77),
                    navigationIconContentColor = Color(255, 209, 26),
                    titleContentColor = Color(255, 209, 26),
                    actionIconContentColor = Color(255, 209, 26)
                ),
                modifier = modifier
            )

            Row(
                Modifier
                    .padding(15.dp)
            ) {

            }
            Row(
                Modifier
                    .fillMaxWidth(),
                Arrangement.Center
            ) {
                TextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do Filme") },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color(255, 209, 26),
                        unfocusedLabelColor = Color.White,
                        unfocusedContainerColor = Color(0, 60, 140),
                        unfocusedTextColor = Color.White,

                        focusedIndicatorColor = Color(255, 209, 26),
                        focusedLabelColor = Color(255, 209, 26),
                        focusedContainerColor = Color(0, 60, 140),
                        focusedTextColor = Color.White,

                        cursorColor = Color(255, 209, 26)
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
            }

            Row(
                Modifier
                    .padding(15.dp)
            ) {

            }
            Row(
                Modifier
                    .fillMaxWidth(),
                Arrangement.Center
            ) {
                TextField(
                    value = diretor,
                    onValueChange = { diretor = it },
                    label = { Text("Nome do Diretor") },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color(255, 209, 26),
                        unfocusedLabelColor = Color.White,
                        unfocusedContainerColor = Color(0, 60, 140),
                        unfocusedTextColor = Color.White,

                        focusedIndicatorColor = Color(255, 209, 26),
                        focusedLabelColor = Color(255, 209, 26),
                        focusedContainerColor = Color(0, 60, 140),
                        focusedTextColor = Color.White,

                        cursorColor = Color(255, 209, 26)
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
            }

            Row(
                Modifier
                    .padding(15.dp)
            ) {

            }
            Row(
                Modifier
                    .fillMaxWidth(),
                Arrangement.Center
            ) {
                TextField(
                    value = genero,
                    onValueChange = { genero = it },
                    label = { Text("Gênero") },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color(255, 209, 26),
                        unfocusedLabelColor = Color.White,
                        unfocusedContainerColor = Color(0, 60, 140),
                        unfocusedTextColor = Color.White,

                        focusedIndicatorColor = Color(255, 209, 26),
                        focusedLabelColor = Color(255, 209, 26),
                        focusedContainerColor = Color(0, 60, 140),
                        focusedTextColor = Color.White,

                        cursorColor = Color(255, 209, 26)
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
            }

            Row(
                Modifier
                    .padding(15.dp)
            ) {

            }
            Row(
                Modifier
                    .fillMaxWidth(),
                Arrangement.Center
            ) {
                TextField(
                    value = ano,
                    onValueChange = { ano = it },
                    label = { Text("Ano de lançamento") },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color(255, 209, 26),
                        unfocusedLabelColor = Color.White,
                        unfocusedContainerColor = Color(0, 60, 140),
                        unfocusedTextColor = Color.White,

                        focusedIndicatorColor = Color(255, 209, 26),
                        focusedLabelColor = Color(255, 209, 26),
                        focusedContainerColor = Color(0, 60, 140),
                        focusedTextColor = Color.White,

                        cursorColor = Color(255, 209, 26)
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true
                )
            }
            Row(
                Modifier
                    .padding(15.dp)
            ) {

            }

            Row(
                Modifier
                    .fillMaxWidth(),
                Arrangement.Center
            ) {
                val botaoSalvar = Button(
                    onClick = {
                        viewModel.updateFilme(nome, diretor, genero, ano, id)
                        id = ""
                        nome = ""
                        diretor = ""
                        genero = ""
                        ano = ""
                        navHostController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(255, 209, 26)),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "Atualizar",
                        color = Color.White
                    )
                }
            }
        }
    }
}