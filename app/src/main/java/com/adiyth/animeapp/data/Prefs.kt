package com.adiyth.animeapp.data

import android.content.Context
import org.json.JSONObject

/** Kemajuan menonton per judul+episode (untuk label "sudah ditonton" & lanjut nonton). */
data class Progres(val episode: Int, val posisiMs: Long, val durasiMs: Long, val diperbarui: Long)

/**
 * Penyimpanan lokal sederhana: bookmark judul + progres tontonan.
 * Memakai SharedPreferences berisi JSON — cukup untuk skala aplikasi ini.
 */
class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("animeapp", Context.MODE_PRIVATE)

    /* ---------- Bookmark ---------- */
    fun bookmarkSemua(): Set<String> = sp.getStringSet("bookmark", emptySet())?.toSet() ?: emptySet()

    fun bookmarkAda(judul: String): Boolean = bookmarkSemua().contains(judul)

    fun bookmarkGanti(judul: String): Boolean {
        val kini = bookmarkSemua().toMutableSet()
        val jadi = if (kini.contains(judul)) { kini.remove(judul); false } else { kini.add(judul); true }
        sp.edit().putStringSet("bookmark", kini).apply()
        return jadi
    }

    /* ---------- Progres tontonan ---------- */
    private fun kunciProgres(judul: String, episode: Int) = "prog::${judul}::${episode}"

    fun simpanProgres(judul: String, episode: Int, posisiMs: Long, durasiMs: Long) {
        if (durasiMs <= 0) return
        val o = JSONObject()
            .put("posisi", posisiMs)
            .put("durasi", durasiMs)
            .put("pada", System.currentTimeMillis())
        sp.edit().putString(kunciProgres(judul, episode), o.toString()).apply()
        sp.edit().putString("terakhir::$judul", JSONObject()
            .put("episode", episode).put("pada", System.currentTimeMillis()).toString()).apply()
    }

    fun ambilProgres(judul: String, episode: Int): Progres? {
        val mentah = sp.getString(kunciProgres(judul, episode), null) ?: return null
        return try {
            val o = JSONObject(mentah)
            Progres(episode, o.getLong("posisi"), o.getLong("durasi"), o.optLong("pada"))
        } catch (e: Exception) { null }
    }

    fun episodeTerakhir(judul: String): Int {
        val mentah = sp.getString("terakhir::$judul", null) ?: return 1
        return try { JSONObject(mentah).getInt("episode") } catch (e: Exception) { 1 }
    }

    /** Semua judul yang pernah ditonton, terbaru dulu: (judul, episode terakhir). */
    fun riwayat(): List<Pair<String, Int>> {
        val hasil = ArrayList<Triple<String, Int, Long>>()
        for ((kunci, nilai) in sp.all) {
            if (kunci.startsWith("terakhir::") && nilai is String) {
                try {
                    val o = JSONObject(nilai)
                    hasil.add(Triple(kunci.removePrefix("terakhir::"), o.getInt("episode"), o.optLong("pada")))
                } catch (e: Exception) { /* abaikan entri rusak */ }
            }
        }
        return hasil.sortedByDescending { it.third }.map { it.first to it.second }
    }
}
