package com.adiyth.animeapp.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/* Palet AnimeApp — gelap premium + ungu elektrik, sama dengan versi web. */
val WarnaLatar = Color(0xFF0B0B10)
val WarnaLatarDalam = Color(0xFF08080C)
val WarnaPermukaan = Color(0xFF121218)
val WarnaPermukaanVariant = Color(0xFF1C1C26)
val WarnaAksen = Color(0xFF8B5CF6)
val WarnaAksenTerang = Color(0xFFA78BFA)
val WarnaAksenGelap = Color(0xFF7C3AED)
val WarnaTeks = Color(0xFFFFFFFF)
val WarnaTeksAbu = Color(0xFFA6A8B5)
val WarnaBintang = Color(0xFFFBBF24)

private val Skema = darkColorScheme(
    primary = WarnaAksen,
    onPrimary = Color.White,
    primaryContainer = WarnaAksenGelap,
    background = WarnaLatar,
    onBackground = WarnaTeks,
    surface = WarnaPermukaan,
    onSurface = WarnaTeks,
    surfaceVariant = WarnaPermukaanVariant,
    onSurfaceVariant = WarnaTeksAbu,
)

@Composable
fun AnimeAppTheme(konten: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Skema, content = konten)
}
