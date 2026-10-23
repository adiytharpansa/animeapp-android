package com.adiyth.animeapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adiyth.animeapp.data.Anime
import com.adiyth.animeapp.data.ApiClient
import com.adiyth.animeapp.data.KATALOG
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(katalog: List<Anime> = KATALOG, onBukaAnime: (Anime) -> Unit) {
    var kata by remember { mutableStateOf("") }
    var genreAktif by remember { mutableStateOf("Semua") }
    val genre = listOf("Semua", "Aksi", "Petualangan", "Fantasi", "Drama", "Komedi", "Horor", "Isekai", "Misteri", "Supernatural")

    /* Pencarian realtime ke API (jeda 600ms); hasil lokal jadi cadangan. */
    var hasilServer by remember { mutableStateOf<List<Anime>?>(null) }
    LaunchedEffect(kata) {
        if (kata.isBlank()) {
            hasilServer = null
        } else {
            delay(600)
            runCatching { ApiClient.cariKatalog(kata) }.onSuccess { hasilServer = it }
        }
    }
    val dasar = if (kata.isNotBlank() && hasilServer != null) hasilServer!! else katalog

    val hasil = dasar.filter { anime ->
        val cocokKata = kata.isBlank() ||
            anime.judul.lowercase().contains(kata.lowercase()) ||
            anime.genre.lowercase().contains(kata.lowercase()) ||
            anime.studio.lowercase().contains(kata.lowercase())
        val cocokGenre = genreAktif == "Semua" || anime.genre.contains(genreAktif)
        cocokKata && cocokGenre
    }

    Column(modifier = Modifier.fillMaxSize().background(WarnaLatar).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("Pencarian", color = WarnaTeks, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(
            value = kata,
            onValueChange = { kata = it },
            placeholder = { Text("Cari anime, genre, studio…", color = WarnaTeksAbu) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = WarnaTeksAbu) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = WarnaPermukaan,
                unfocusedContainerColor = WarnaPermukaan,
                focusedBorderColor = WarnaAksen,
                unfocusedBorderColor = WarnaPermukaanVariant,
                focusedTextColor = WarnaTeks,
                unfocusedTextColor = WarnaTeks,
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(genre) { g ->
                FilterChip(
                    selected = genreAktif == g,
                    onClick = { genreAktif = g },
                    label = { Text(g, fontSize = 12.sp) },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WarnaAksen,
                        selectedLabelColor = Color.White,
                        containerColor = WarnaPermukaan,
                        labelColor = WarnaTeksAbu,
                    ),
                    border = null
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Text("${hasil.size} judul ditemukan", color = WarnaTeksAbu, fontSize = 12.sp)
        Spacer(Modifier.height(10.dp))
        if (hasil.isEmpty()) {
            Column(modifier = Modifier.padding(top = 48.dp)) {
                Text("Tidak ada hasil", color = WarnaTeks, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text("Coba kata kunci atau genre lain.", color = WarnaTeksAbu, fontSize = 13.sp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(hasil) { anime -> KartuAnime(anime, Modifier.fillMaxWidth()) { onBukaAnime(anime) } }
            }
        }
    }
}
