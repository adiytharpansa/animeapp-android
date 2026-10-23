package com.adiyth.animeapp.ui

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.adiyth.animeapp.data.Anime
import com.adiyth.animeapp.data.KATALOG

@Composable
fun HomeScreen(
    katalog: List<Anime> = KATALOG,
    onBukaAnime: (Anime) -> Unit,
    onKePencarian: () -> Unit,
) {
    val hero = katalog.first()
    val trending = katalog.sortedByDescending { it.rating.toDoubleOrNull() ?: 0.0 }
    val terbaru = katalog.sortedByDescending { it.tahun.toIntOrNull() ?: 0 }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(WarnaLatar),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        /* Header */
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Anime", color = WarnaTeks, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Text("App", color = WarnaAksenTerang, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.Filled.Search, contentDescription = "Cari",
                    tint = WarnaTeks, modifier = Modifier.size(24.dp).clickable { onKePencarian() }
                )
                Spacer(Modifier.width(16.dp))
                Box {
                    Icon(Icons.Filled.Notifications, contentDescription = "Notifikasi", tint = WarnaTeks, modifier = Modifier.size(24.dp))
                    Box(
                        modifier = Modifier.size(16.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFE5222B))
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("3", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        /* Hero */
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(WarnaPermukaan)
            ) {
                AsyncImage(
                    model = hero.poster, contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 10f).blur(28.dp)
                )
                Box(
                    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 10f)
                        .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Black.copy(alpha = 0.82f))))
                )
                Row(modifier = Modifier.padding(14.dp).align(Alignment.BottomStart), verticalAlignment = Alignment.Bottom) {
                    AsyncImage(
                        model = hero.poster, contentDescription = hero.judul,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.width(74.dp).aspectRatio(2f / 3f).clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(WarnaAksen).padding(horizontal = 8.dp, vertical = 3.dp)
                        ) { Text("UNGGULAN", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold) }
                        Spacer(Modifier.height(6.dp))
                        Text(hero.judul, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = WarnaBintang, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("${hero.rating}  •  ${hero.tahun}  •  ${hero.genre}", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = { onBukaAnime(hero) },
                            colors = ButtonDefaults.buttonColors(containerColor = WarnaAksen),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Tonton Sekarang", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        /* Trending */
        item {
            Spacer(Modifier.height(22.dp))
            JudulBagian("Trending Pekan Ini", Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(trending) { anime -> KartuAnime(anime, Modifier.width(118.dp)) { onBukaAnime(anime) } }
            }
        }

        /* Terbaru */
        item {
            Spacer(Modifier.height(22.dp))
            JudulBagian("Terbaru (Sub Indo)", Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(terbaru) { anime -> KartuAnime(anime, Modifier.width(118.dp)) { onBukaAnime(anime) } }
            }
        }

        /* Semua */
        item {
            Spacer(Modifier.height(22.dp))
            JudulBagian("Semua Anime", Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(928.dp).padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                userScrollEnabled = false
            ) {
                items(katalog) { anime -> KartuAnime(anime, Modifier.fillMaxWidth()) { onBukaAnime(anime) } }
            }
        }
    }
}
