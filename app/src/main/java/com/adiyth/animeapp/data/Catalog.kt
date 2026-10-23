package com.adiyth.animeapp.data

/** Satu judul anime di katalog lokal aplikasi. */
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

/** Katalog bawaan (poster terverifikasi dari CDN MyAnimeList). */
val KATALOG: List<Anime> = listOf(
    Anime("Chainsaw Man", "https://cdn.myanimelist.net/images/anime/1806/126216.jpg", "8.5", "2022", 12, "Aksi • Horor", "Studio MAPPA", "Denji adalah remaja miskin yang bekerja sebagai Pemburu Iblis swasta demi melunasi utang ayahnya. Bersama iblis gergaji mesin peliharaannya, Pochita, ia hidup pas-pasan — sampai sebuah pengkhianatan membuatnya tewas dan bangkit kembali sebagai Chainsaw Man. Kini direkrut Divisi Keamanan Publik, Denji berburu Iblis Senjata (Gun Devil) bersama Aki, Power, dan tim pemburu eksentrik lainnya."),
    Anime("Jujutsu Kaisen", "https://cdn.myanimelist.net/images/anime/1171/109222.jpg", "8.6", "2020", 47, "Aksi • Supernatural", "Studio MAPPA", "Yuji Itadori, siswa SMA berfisik luar biasa, menelan jari terkutuk Ryomen Sukuna demi menyelamatkan teman-temannya. Kini ia masuk Sekolah Jujutsu Tokyo dan berburu sisa jari Sukuna bersama Megumi dan Nobara, sambil menunggu hukuman mati yang menantinya di akhir jalan."),
    Anime("Demon Slayer: Kimetsu no Yaiba", "https://cdn.myanimelist.net/images/anime/1286/99889.jpg", "8.5", "2019", 55, "Aksi • Fantasi", "Studio ufotable", "Keluarga Tanjiro Kamado dibantai iblis; hanya adiknya, Nezuko, yang selamat — namun berubah menjadi iblis yang masih menyimpan emosi manusia. Tanjiro bergabung dengan Korps Pembasmi Iblis untuk memburu Muzan Kibutsuji dan mencari cara mengembalikan Nezuko menjadi manusia."),
    Anime("Attack on Titan", "https://cdn.myanimelist.net/images/anime/10/47347.jpg", "8.9", "2013", 89, "Aksi • Drama", "Studio Wit / MAPPA", "Manusia berlindung di balik tembok raksasa dari para Titan pemakan manusia. Ketika Tembok Maria runtuh dan ibunya tewas, Eren Yeager bersumpah membasmi semua Titan dan bergabung dengan Korps Penyelidik — tanpa tahu rahasia besar yang tersimpan dalam dirinya dan di balik tembok."),
    Anime("One Punch Man", "https://cdn.myanimelist.net/images/anime/12/76049.jpg", "8.5", "2015", 24, "Aksi • Komedi", "Studio Madhouse", "Saitama berlatih begitu keras hingga bisa mengalahkan monster apa pun dengan satu pukulan — dan justru bosan setengah mati karena tak ada lawan sepadan. Bersama murid cyborg-nya, Genos, ia bergabung dengan Asosiasi Pahlawan demi mencari tantangan sejati."),
    Anime("Naruto Shippuden", "https://cdn.myanimelist.net/images/anime/1565/111305.jpg", "8.3", "2007", 500, "Aksi • Petualangan", "Studio Pierrot", "Dua setengah tahun berlatih bersama Jiraiya, Naruto Uzumaki kembali ke Konoha dengan tekad lebih besar: menjadi Hokage dan menyelamatkan Sasuke. Di hadapannya berdiri Akatsuki, organisasi kriminal yang memburu semua Jinchuriki — termasuk dirinya."),
    Anime("Hunter x Hunter (2011)", "https://cdn.myanimelist.net/images/anime/1337/99013.jpg", "9.0", "2011", 148, "Petualangan • Fantasi", "Studio Madhouse", "Gon Freecss meninggalkan Pulau Paus untuk mengikuti Ujian Hunter, profesi elite yang dulu ditempuh ayahnya, Ging. Bersama Killua, Kurapika, dan Leorio, Gon mengarungi ujian mematikan dan dunia yang jauh lebih berbahaya dari yang ia bayangkan."),
    Anime("Re:Zero — Starting Life in Another World", "https://cdn.myanimelist.net/images/anime/1522/128039.jpg", "8.2", "2016", 66, "Isekai • Drama", "Studio White Fox", "Sepulang dari minimarket, Subaru Natsuki tiba-tiba terlempar ke dunia fantasi. Ia segera tewas — lalu terbangun kembali di titik awal hari itu. Dengan kemampuan Return by Death yang menyiksa, Subaru berjuang melindungi Emilia dan orang-orang yang ia sayangi, lagi dan lagi."),
    Anime("Death Note", "https://cdn.myanimelist.net/images/anime/1079/138100.jpg", "8.6", "2006", 37, "Misteri • Thriller", "Studio Madhouse", "Light Yagami, siswa jenius, menemukan Death Note — buku dewa kematian Ryuk yang membunuh siapa pun yang namanya ditulis di dalamnya. Light berambisi menciptakan dunia tanpa kriminal sebagai Kira, tetapi detektif misterius L memburunya dalam duel kecerdasan paling menegangkan."),
    Anime("Fullmetal Alchemist: Brotherhood", "https://cdn.myanimelist.net/images/anime/1208/94745.jpg", "9.1", "2009", 64, "Aksi • Petualangan", "Studio Bones", "Percobaan alkimia terlarang untuk menghidupkan kembali ibu mereka membuat Edward kehilangan kaki dan lengan, dan Alphonse kehilangan seluruh tubuhnya. Demi mengembalikan tubuh mereka, kakak-beradik Elric menjadi Alkimiawan Negara dan memburu Batu Bertuah — di tengah konspirasi besar yang mengancam negeri Amestris.")
)
