package com.adiyth.animeapp.data

/**
 * Satu judul anime di katalog.
 * Data SELALU datang dari API (GET /api/v1/anime) — aplikasi tidak menyimpan
 * katalog bawaan/cadangan apa pun (aturan user: "Jangan ada cadangan").
 */
data class Anime(
    val judul: String,
    val poster: String,
    val rating: String,
    val tahun: String,
    val totalEpisode: Int,
    val genre: String,
    val studio: String,
    val sinopsis: String,
)
