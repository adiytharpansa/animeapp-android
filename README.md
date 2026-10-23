# AnimeApp — Android Native (Kotlin)

Aplikasi streaming anime native Android (Kotlin + Jetpack Compose + Media3 ExoPlayer),
versi native dari AnimeApp web (repo projektest).

- **API**: `https://zdstfggwjwbs.adiyth-arpansa.workers.dev`
  - `GET /api/v1/anime/resolve?title=..&episode=..` mengembalikan tautan putar tersamar per kualitas.
  - Aplikasi otomatis meminta link segar (`&segar=1`) sekali bila resolve/putar gagal;
    rotasi link di tengah putar ditangani server.
- **Fitur**: Beranda (hero + trending + terbaru + semua), Pencarian (teks + genre),
  Pemutar (ExoPlayer, kualitas, episode grid, lanjut dari posisi terakhir, prev/next),
  Bookmark & riwayat di Profil.
- **Build**: GitHub Actions (`.github/workflows/build.yml`) — build APK debug lalu
  uji emulator: APK dipasang dan dibuka langsung ke Chainsaw Man episode 5;
  tangkapan layar uji diunggah sebagai artefak `bukti-uji-emulator`.

Tanpa kunci/rahasia apa pun di repo ini — alamat API bersifat publik.
