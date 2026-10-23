package com.adiyth.animeapp

import android.content.Intent
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adiyth.animeapp.data.Anime
import com.adiyth.animeapp.data.ApiClient
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

    /* Ekstra uji otomatis disimpan sebagai state agar tetap sampai walau
       intent datang lewat onNewIntent (activity dipakai ulang):
       am start ... --es autoplay_title "Chainsaw Man" --ei autoplay_episode 5 */
    private var intentOtomatis by mutableStateOf<Pair<String, Int>?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intentOtomatis = ekstrakOtomatis(intent)
        setContent {
            AnimeAppTheme {
                Aplikasi(intentOtomatis)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        ekstrakOtomatis(intent)?.let { intentOtomatis = it }
    }

    private fun ekstrakOtomatis(sumber: Intent?): Pair<String, Int>? {
        val judul = sumber?.getStringExtra("autoplay_title") ?: return null
        return judul to (sumber.getIntExtra("autoplay_episode", 1))
    }
}

@Composable
private fun Aplikasi(intentOtomatis: Pair<String, Int>?) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { Prefs(context) }

    /* Katalog HANYA dari API — tidak ada katalog bawaan/cadangan (aturan user).
       Gagal memuat -> statusMuat "gagal" dan Beranda menawarkan Coba Lagi. */
    var katalog by remember { mutableStateOf<List<Anime>>(emptyList()) }
    var statusMuat by remember { mutableStateOf("memuat") }
    var pemicuMuat by remember { mutableStateOf(0) }
    LaunchedEffect(pemicuMuat) {
        statusMuat = "memuat"
        runCatching { ApiClient.ambilKatalog() }
            .onSuccess {
                if (it.isNotEmpty()) {
                    katalog = it
                    statusMuat = "siap"
                } else statusMuat = "gagal"
            }
            .onFailure { statusMuat = "gagal" }
    }

    var layar by remember { mutableStateOf<Layar>(Layar.Beranda) }

    /* Pintu uji otomatis: begitu katalog API tiba, buka judul yang diminta. */
    var autoplayTerpicu by remember { mutableStateOf(false) }
    LaunchedEffect(katalog, intentOtomatis) {
        val oto = intentOtomatis
        if (!autoplayTerpicu && oto != null && katalog.isNotEmpty()) {
            autoplayTerpicu = true
            katalog.find { it.judul.equals(oto.first, ignoreCase = true) }
                ?.let { layar = Layar.Tonton(it, oto.second) }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(WarnaLatarDalam)) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (val aktif = layar) {
                Layar.Beranda -> HomeScreen(
                    katalog = katalog,
                    statusMuat = statusMuat,
                    onMuatUlang = { pemicuMuat++ },
                    onBukaAnime = { layar = Layar.Tonton(it, prefs.episodeTerakhir(it.judul)) },
                    onKePencarian = { layar = Layar.Cari }
                )
                Layar.Cari -> SearchScreen(
                    katalog = katalog,
                    onBukaAnime = { layar = Layar.Tonton(it, prefs.episodeTerakhir(it.judul)) }
                )
                Layar.Profil -> ProfileScreen(
                    katalog = katalog,
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
