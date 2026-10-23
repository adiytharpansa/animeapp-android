package com.adiyth.animeapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/** Opsi kualitas stream dari API. */
data class OpsiStream(val label: String, val url: String)

/** Hasil resolve satu episode dari API. */
data class HasilStream(
    val tipe: String,
    val streamUtama: String,
    val kualitas: List<OpsiStream>,
)

/**
 * Klien API AnimeApp (Cloudflare Worker).
 * Kontrak: GET /api/v1/anime/resolve?title=..&episode=..[&segar=1]
 * -> { success, data: { type, stream, qualities: [{label, stream}] } }
 * Semua URL yang dikembalikan sudah berupa tautan samaran /api/play.
 */
object ApiClient {
    const val BASE = "https://zdstfggwjwbs.adiyth-arpansa.workers.dev"

    private val http = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun resolve(judul: String, episode: Int, paksaSegar: Boolean = false): HasilStream =
        withContext(Dispatchers.IO) {
            val url = buildString {
                append(BASE)
                append("/api/v1/anime/resolve?title=")
                append(java.net.URLEncoder.encode(judul, "UTF-8"))
                append("&episode=")
                append(episode)
                if (paksaSegar) append("&segar=1")
            }
            val req = Request.Builder().url(url).get().build()
            http.newCall(req).execute().use { res ->
                val badan = res.body?.string().orEmpty()
                if (!res.isSuccessful) {
                    throw IllegalStateException("API menjawab ${res.code}")
                }
                val json = JSONObject(badan)
                if (!json.optBoolean("success")) {
                    throw IllegalStateException(json.optString("error", "Resolve gagal"))
                }
                val data = json.getJSONObject("data")
                val arr = data.getJSONArray("qualities")
                val opsi = ArrayList<OpsiStream>()
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    opsi.add(OpsiStream(o.getString("label"), o.getString("stream")))
                }
                HasilStream(
                    tipe = data.optString("type", "mp4"),
                    streamUtama = data.getString("stream"),
                    kualitas = opsi,
                )
            }
        }

    /** Katalog realtime dari API: GET /api/v1/anime -> { success, data: [...] } */
    suspend fun ambilKatalog(): List<Anime> = ambilDaftar("$BASE/api/v1/anime")

    /** Pencarian realtime dari API: GET /api/v1/anime?q=... */
    suspend fun cariKatalog(kataKunci: String): List<Anime> =
        ambilDaftar("$BASE/api/v1/anime?q=" + java.net.URLEncoder.encode(kataKunci, "UTF-8"))

    private suspend fun ambilDaftar(url: String): List<Anime> = withContext(Dispatchers.IO) {
        val req = Request.Builder().url(url).get().build()
        http.newCall(req).execute().use { res ->
            val badan = res.body?.string().orEmpty()
            if (!res.isSuccessful) throw IllegalStateException("API menjawab ${res.code}")
            val json = JSONObject(badan)
            if (!json.optBoolean("success")) {
                throw IllegalStateException(json.optString("error", "Katalog gagal"))
            }
            val arr = json.getJSONArray("data")
            val hasil = ArrayList<Anime>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val judul = o.optString("judul")
                if (judul.isBlank()) continue
                val ratingMentah = o.optDouble("rating", 0.0)
                val tahunMentah = o.optInt("tahun", 0)
                hasil.add(
                    Anime(
                        judul = judul,
                        poster = o.optString("poster"),
                        rating = if (ratingMentah > 0) String.format(java.util.Locale.US, "%.1f", ratingMentah) else "",
                        tahun = if (tahunMentah > 0) tahunMentah.toString() else "",
                        genre = o.optString("genre"),
                        totalEpisode = o.optInt("totalEpisode", 12),
                        studio = o.optString("studio"),
                        sinopsis = o.optString("sinopsis"),
                    )
                )
            }
            hasil
        }
    }
}
