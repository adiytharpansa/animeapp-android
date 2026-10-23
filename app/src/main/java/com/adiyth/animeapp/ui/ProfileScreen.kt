package com.adiyth.animeapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.adiyth.animeapp.data.Anime
import com.adiyth.animeapp.data.Prefs

@Composable
fun ProfileScreen(katalog: List<Anime>, prefs: Prefs, onBukaAnime: (Anime, Int) -> Unit) {
    val bookmark = katalog.filter { prefs.bookmarkAda(it.judul) }
    val riwayat = prefs.riwayat().mapNotNull { (judul, ep) ->
        katalog.find { it.judul == judul }?.let { it to ep }
    }
    var notif by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(WarnaLatar),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(60.dp).clip(CircleShape).background(WarnaAksen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Adiyth Arpansa", color = WarnaTeks, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFFFBBF24))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) { Text("PREMIUM", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold) }
                        Spacer(Modifier.width(8.dp))
                        Text("Anggota sejak 2026", color = WarnaTeksAbu, fontSize = 12.sp)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                StatBox(Modifier.weight(1f), "${riwayat.size}", "Ditonton")
                Spacer(Modifier.width(10.dp))
                StatBox(Modifier.weight(1f), "${bookmark.size}", "Bookmark")
                Spacer(Modifier.width(10.dp))
                StatBox(Modifier.weight(1f), "${katalog.size}", "Katalog")
            }
            Spacer(Modifier.height(24.dp))
        }

        if (riwayat.isNotEmpty()) {
            item {
                JudulBagian("Terakhir Ditonton")
                Spacer(Modifier.height(12.dp))
            }
            items(riwayat) { (anime, ep) ->
                val prog = prefs.ambilProgres(anime.judul, ep)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable { onBukaAnime(anime, ep) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = anime.poster, contentDescription = anime.judul,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.width(46.dp).aspectRatio(2f / 3f).clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(anime.judul, color = WarnaTeks, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Episode $ep", color = WarnaTeksAbu, fontSize = 12.sp)
                        if (prog != null && prog.durasiMs > 0) {
                            Spacer(Modifier.height(5.dp))
                            LinearProgressIndicator(
                                progress = { (prog.posisiMs.toFloat() / prog.durasiMs).coerceIn(0f, 1f) },
                                color = WarnaAksen,
                                trackColor = WarnaPermukaanVariant,
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(10.dp)) }
        }

        item {
            JudulBagian("Bookmark Saya", Modifier.padding(top = 2.dp))
            Spacer(Modifier.height(12.dp))
            if (bookmark.isEmpty()) {
                Text("Belum ada bookmark. Ketuk ikon bookmark di halaman tonton untuk menyimpan.", color = WarnaTeksAbu, fontSize = 13.sp)
            }
        }
        items(bookmark) { anime ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    .clickable { onBukaAnime(anime, prefs.episodeTerakhir(anime.judul)) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = anime.poster, contentDescription = anime.judul,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.width(46.dp).aspectRatio(2f / 3f).clip(RoundedCornerShape(8.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(anime.judul, color = WarnaTeks, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${anime.genre} • ${anime.totalEpisode} Episode", color = WarnaTeksAbu, fontSize = 12.sp)
                }
                Icon(Icons.Filled.Bookmark, contentDescription = null, tint = WarnaAksenTerang, modifier = Modifier.size(20.dp))
            }
        }

        item {
            Spacer(Modifier.height(14.dp))
            JudulBagian("Pengaturan")
            Spacer(Modifier.height(8.dp))
            BarisMenu(Icons.Filled.Notifications, "Notifikasi episode baru") {
                Switch(
                    checked = notif, onCheckedChange = { notif = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = WarnaAksen)
                )
            }
            BarisMenu(Icons.Filled.Download, "Unduhan") {
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = WarnaTeksAbu)
            }
            BarisMenu(Icons.Filled.History, "Riwayat tontonan") {
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = WarnaTeksAbu)
            }
            BarisMenu(Icons.Filled.Settings, "Pengaturan aplikasi") {
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = WarnaTeksAbu)
            }
        }
    }
}

@Composable
private fun StatBox(modifier: Modifier, angka: String, label: String) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(WarnaPermukaan).padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(angka, color = WarnaTeks, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = WarnaTeksAbu, fontSize = 11.sp)
    }
}

@Composable
private fun BarisMenu(ikon: androidx.compose.ui.graphics.vector.ImageVector, teks: String, ekor: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(ikon, contentDescription = null, tint = WarnaAksenTerang, modifier = Modifier.size(21.dp))
        Spacer(Modifier.width(12.dp))
        Text(teks, color = WarnaTeks, fontSize = 14.sp, modifier = Modifier.weight(1f))
        ekor()
    }
}
