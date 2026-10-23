package com.adiyth.animeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adiyth.animeapp.data.Anime
import com.adiyth.animeapp.data.KATALOG
import com.adiyth.animeapp.data.Prefs
import com.adiyth.animeapp.ui.AnimeAppTheme
import com.adiyth.animeapp.ui.HomeScreen
import com.adiyth.animeapp.ui.ProfileScreen
import com.adiyth.animeapp.ui.SearchScreen
import com.adiyth.animeapp.ui.WarnaAksenTerang
import com.adiyth.animeapp.ui.WarnaLatarDalam
import com.adiyth.animeapp.ui.WarnaTeksAbu
import com.adiyth.animeapp.ui.WatchScreen

/** Layar aktif aplikasi (pola SPA satu Activity). */
sealed interface Layar {
    data object Beranda : Layar
    data object Cari : Layar
    data object Profil : Layar
    data class Tonton(val anime: Anime, val episode: Int) : Layar
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Pintu masuk uji otomatis (dipakai pengujian emulator):
           am start ... --es autoplay_title "Chainsaw Man" --ei autoplay_episode 5 */
        val judulOtomatis = intent?.getStringExtra("autoplay_title")
        val episodeOtomatis = intent?.getIntExtra("autoplay_episode", 1) ?: 1

        setContent {
            AnimeAppTheme {
                Aplikasi(judulOtomatis, episodeOtomatis)
            }
        }
    }
}

@Composable
private fun Aplikasi(judulOtomatis: String?, episodeOtomatis: Int) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { Prefs(context) }

    var layar by remember {
        mutableStateOf<Layar>(
            if (judulOtomatis != null) {
                val anime = KATALOG.find { it.judul.equals(judulOtomatis, ignoreCase = true) } ?: KATALOG.first()
                Layar.Tonton(anime, episodeOtomatis)
            } else Layar.Beranda
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(WarnaLatarDalam)) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (val aktif = layar) {
                Layar.Beranda -> HomeScreen(
                    onBukaAnime = { layar = Layar.Tonton(it, prefs.episodeTerakhir(it.judul)) },
                    onKePencarian = { layar = Layar.Cari }
                )
                Layar.Cari -> SearchScreen(
                    onBukaAnime = { layar = Layar.Tonton(it, prefs.episodeTerakhir(it.judul)) }
                )
                Layar.Profil -> ProfileScreen(
                    prefs = prefs,
                    onBukaAnime = { anime, ep -> layar = Layar.Tonton(anime, ep) }
                )
                is Layar.Tonton -> WatchScreen(
                    anime = aktif.anime,
                    episodeAwal = aktif.episode,
                    prefs = prefs,
                    onKembali = { layar = Layar.Beranda }
                )
            }
        }

        /* Navigasi bawah — sama di semua halaman, termasuk Tonton. */
        NavigationBar(containerColor = WarnaLatarDalam, tonalElevation = 0.dp) {
            ItemNav("Home", Icons.Filled.Home, layar == Layar.Beranda) { layar = Layar.Beranda }
            ItemNav("Search", Icons.Filled.Search, layar == Layar.Cari) { layar = Layar.Cari }
            ItemNav("Bookmark", Icons.Filled.Bookmark, layar == Layar.Profil) { layar = Layar.Profil }
            ItemNav("Akun", Icons.Filled.Person, layar == Layar.Profil) { layar = Layar.Profil }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.ItemNav(
    label: String,
    ikon: androidx.compose.ui.graphics.vector.ImageVector,
    terpilih: Boolean,
    onKlik: () -> Unit,
) {
    NavigationBarItem(
        selected = terpilih,
        onClick = onKlik,
        icon = { Icon(ikon, contentDescription = label) },
        label = { Text(label, fontSize = 11.sp) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = WarnaAksenTerang,
            selectedTextColor = WarnaAksenTerang,
            unselectedIconColor = WarnaTeksAbu,
            unselectedTextColor = WarnaTeksAbu,
            indicatorColor = Color.Transparent,
        )
    )
}
