package com.adiyth.animeapp.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.adiyth.animeapp.data.Anime
import com.adiyth.animeapp.data.ApiClient
import com.adiyth.animeapp.data.HasilStream
import com.adiyth.animeapp.data.Prefs
import kotlinx.coroutines.delay

@Composable
fun WatchScreen(
    anime: Anime,
    episodeAwal: Int,
    prefs: Prefs,
    onKembali: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var episode by remember(anime.judul) { mutableIntStateOf(episodeAwal.coerceIn(1, anime.totalEpisode)) }
    var memuat by remember { mutableStateOf(true) }
    var galat by remember { mutableStateOf<String?>(null) }
    var hasil by remember { mutableStateOf<HasilStream?>(null) }
    var indeksKualitas by remember { mutableIntStateOf(0) }
    var pemicu by remember { mutableIntStateOf(0) }
    var sinopsisTerbuka by remember { mutableStateOf(false) }
    var bookmark by remember { mutableStateOf(prefs.bookmarkAda(anime.judul)) }
    var layarPenuh by remember { mutableStateOf(false) }

    val player = remember {
        ExoPlayer.Builder(context).build().apply { playWhenReady = true }
    }

    /* Ambil link stream dari API (otomatis coba link segar sekali bila gagal). */
    LaunchedEffect(anime.judul, episode, pemicu) {
        memuat = true
        galat = null
        hasil = null
        try {
            val h = try {
                ApiClient.resolve(anime.judul, episode)
            } catch (e: Exception) {
                ApiClient.resolve(anime.judul, episode, paksaSegar = true)
            }
            hasil = h
            indeksKualitas = 0
        } catch (e: Exception) {
            galat = "Episode ini belum bisa dimuat. Periksa koneksi lalu coba lagi."
        } finally {
            memuat = false
        }
    }

    /* Terapkan URL terpilih ke pemutar, lanjutkan dari posisi terakhir. */
    LaunchedEffect(hasil, indeksKualitas) {
        val h = hasil ?: return@LaunchedEffect
        val url = h.kualitas.getOrNull(indeksKualitas)?.url ?: h.streamUtama
        val tersimpan = prefs.ambilProgres(anime.judul, episode)
        val posisi = if (tersimpan != null && tersimpan.durasiMs - tersimpan.posisiMs > 10_000) tersimpan.posisiMs else 0L
        player.setMediaItem(MediaItem.fromUri(url), posisi)
        player.prepare()
        player.playWhenReady = true
    }

    /* Simpan progres berkala + saat layar ditutup. */
    LaunchedEffect(player, episode) {
        while (true) {
            delay(3000)
            if (player.duration > 0) {
                prefs.simpanProgres(anime.judul, episode, player.currentPosition, player.duration)
            }
        }
    }
    DisposableEffect(player) {
        val pendengar = object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                galat = "Video gagal diputar. Ketuk Coba Lagi untuk meminta link baru."
            }
        }
        player.addListener(pendengar)
        onDispose {
            if (player.duration > 0) {
                prefs.simpanProgres(anime.judul, episode, player.currentPosition, player.duration)
            }
            player.removeListener(pendengar)
            player.release()
        }
    }
    DisposableEffect(Unit) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose { activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        /* ===== Area pemutar ===== */
        Box(
            modifier = if (layarPenuh) Modifier.fillMaxSize() else Modifier.fillMaxWidth().aspectRatio(16f / 9f)
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        this.player = player
                        useController = true
                        setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                    }
                },
                update = { it.player = player },
                modifier = Modifier.fillMaxSize()
            )
            /* Bar atas pemutar */
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (layarPenuh) {
                            layarPenuh = false
                            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        } else onKembali()
                    },
                    modifier = Modifier.clip(CircleShape).background(Color.Black.copy(alpha = 0.55f))
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "${anime.judul} — Episode $episode",
                    color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        layarPenuh = !layarPenuh
                        activity?.requestedOrientation = if (layarPenuh) {
                            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                        } else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    },
                    modifier = Modifier.clip(CircleShape).background(Color.Black.copy(alpha = 0.55f))
                ) {
                    Icon(
                        if (layarPenuh) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                        contentDescription = "Layar penuh", tint = Color.White
                    )
                }
            }
            /* Keadaan memuat / gagal */
            if (memuat) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = WarnaAksen)
                        Spacer(Modifier.height(10.dp))
                        Text("Menghubungi server…", color = Color.White, fontSize = 13.sp)
                    }
                }
            } else if (galat != null) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.72f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text(galat!!, color = Color.White, fontSize = 14.sp)
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { pemicu++ },
                            colors = ButtonDefaults.buttonColors(containerColor = WarnaAksen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(17.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Coba Lagi (Link Baru)")
                        }
                    }
                }
            }
        }

        if (!layarPenuh) {
            /* ===== Detail + pemilih episode ===== */
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(WarnaLatar),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Text(anime.judul, color = WarnaTeks, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(4.dp))
                    Text("Episode $episode — Sedang Diputar", color = WarnaAksenTerang, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = WarnaBintang, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("${anime.rating}  •  ${anime.tahun}  •  ${anime.genre}  •  ${anime.studio}", color = WarnaTeksAbu, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { bookmark = prefs.bookmarkGanti(anime.judul) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (bookmark) WarnaAksen else WarnaPermukaanVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                if (bookmark) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = null, modifier = Modifier.size(17.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(if (bookmark) "Terbookmark" else "Simpan ke Bookmark", fontSize = 13.sp)
                        }
                        Spacer(Modifier.width(10.dp))
                        OutlinedButton(
                            onClick = { if (episode > 1) episode-- },
                            enabled = episode > 1,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Filled.SkipPrevious, contentDescription = null, modifier = Modifier.size(17.dp), tint = WarnaTeks)
                        }
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { if (episode < anime.totalEpisode) episode++ },
                            enabled = episode < anime.totalEpisode,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Filled.SkipNext, contentDescription = null, modifier = Modifier.size(17.dp), tint = WarnaTeks)
                        }
                    }

                    /* Pilihan kualitas */
                    val h = hasil
                    if (h != null && h.kualitas.size > 1) {
                        Spacer(Modifier.height(14.dp))
                        Text("Kualitas", color = WarnaTeks, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(h.kualitas) { i, opsi ->
                                FilterChip(
                                    selected = indeksKualitas == i,
                                    onClick = { indeksKualitas = i },
                                    label = { Text(opsi.label, fontSize = 12.sp) },
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
                    }

                    Spacer(Modifier.height(16.dp))
                    Text("Sinopsis", color = WarnaTeks, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        anime.sinopsis,
                        color = WarnaTeksAbu, fontSize = 13.sp, lineHeight = 19.sp,
                        maxLines = if (sinopsisTerbuka) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable { sinopsisTerbuka = !sinopsisTerbuka }
                    )
                    Text(
                        if (sinopsisTerbuka) "Tutup" else "Baca selengkapnya",
                        color = WarnaAksenTerang, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { sinopsisTerbuka = !sinopsisTerbuka }.padding(vertical = 4.dp)
                    )

                    Spacer(Modifier.height(16.dp))
                    Text("Pilih Episode", color = WarnaTeks, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                }
                item {
                    val total = anime.totalEpisode
                    val tinggiGrid = if (total <= 24) ((total + 5) / 6 * 54).dp else 320.dp
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(6),
                        modifier = Modifier.height(tinggiGrid),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items((1..total).toList()) { ep ->
                            val aktif = ep == episode
                            val ditonton = !aktif && prefs.ambilProgres(anime.judul, ep) != null
                            Box(
                                modifier = Modifier
                                    .height(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when {
                                            aktif -> WarnaAksen
                                            ditonton -> WarnaPermukaanVariant
                                            else -> WarnaPermukaan
                                        }
                                    )
                                    .clickable { episode = ep },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$ep",
                                    color = when {
                                        aktif -> Color.White
                                        ditonton -> WarnaTeksAbu
                                        else -> WarnaTeks
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = if (aktif) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}
