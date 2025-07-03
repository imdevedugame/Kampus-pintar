# Kampus Pintar Pro 🎓💡

**AI Study Assistant untuk Mahasiswa Indonesia**  
Dikembangkan oleh:  
- Muhammad Ivan Rafsanjani (A11.2023.14933)  
- Harits Martsyabel Ristyan Jessy (A11.2023.14919)  

---

## 📦 Software & Hardware Requirements

### Software Requirements:
- Java Development Kit (JDK) 17 atau lebih tinggi
- Apache Maven 3.6.0 atau lebih tinggi
- IDE: IntelliJ IDEA / Eclipse / VS Code
- Koneksi internet (untuk akses database Supabase dan Gemini AI)

### Hardware Requirements:
- RAM minimal 4GB (disarankan 8GB)
- Storage minimal 500MB
- Processor dual-core atau lebih tinggi

---

## ⚙️ Langkah Instalasi

### 1. Persiapan Environment
Pastikan Java & Maven telah terinstal:

```bash
java -version
mvn -version
````

### 2. Clone Project

```bash
git clone https://github.com/imdevedugame/Kampus-pintar.git
cd kampus-pintar-pro
```

### 3. Install Dependencies

```bash
mvn clean install
```

### 4. Konfigurasi Database

Konfigurasi Supabase tersedia di:

```
src/main/java/org/kampuspintar/database/SupabaseConfig.java
```

```java
private static final String SUPABASE_URL = "https://xtverbkbkjpflpmiiccd.supabase.co";
private static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
```

### 5. Menjalankan Aplikasi

#### Metode 1: Maven CLI

```bash
mvn javafx:run
```

#### Metode 2: Melalui IDE

Jalankan `Main.java` sebagai *Java Application*

### 6. Verifikasi Instalasi

* Tab **Chat AI** muncul
* Pesan selamat datang dari AI
* Status koneksi database muncul di console
* UI responsif dan modern

---

## 🧩 Database Schema

### Teknologi: Supabase PostgreSQL (via REST API)

#### Tabel `users`

| Kolom       | Tipe Data    | Deskripsi                     |
| ----------- | ------------ | ----------------------------- |
| id          | UUID (PK)    | ID unik pengguna              |
| username    | VARCHAR(50)  | Username unik                 |
| email       | VARCHAR(100) | Email unik                    |
| full\_name  | VARCHAR(100) | Nama lengkap                  |
| university  | VARCHAR(100) | Nama universitas              |
| major       | VARCHAR(100) | Jurusan                       |
| semester    | INTEGER      | Semester saat ini (default 1) |
| target\_gpa | DECIMAL(3,2) | Target IPK (default 3.50)     |
| created\_at | TIMESTAMP    | Waktu pembuatan               |
| updated\_at | TIMESTAMP    | Terakhir update               |

#### Tabel `chat_history`

| Kolom               | Tipe Data | Deskripsi                        |
| ------------------- | --------- | -------------------------------- |
| id                  | UUID (PK) | ID chat                          |
| user\_id            | VARCHAR   | ID pengguna                      |
| message             | TEXT      | Pesan user                       |
| response            | TEXT      | Balasan AI                       |
| message\_type       | VARCHAR   | Jenis pesan (default 'academic') |
| is\_academic\_topic | BOOLEAN   | Deteksi topik akademik           |
| created\_at         | TIMESTAMP | Timestamp                        |

#### Tabel `study_sessions`

| Kolom             | Tipe Data | Deskripsi                  |
| ----------------- | --------- | -------------------------- |
| id                | UUID (PK) | ID sesi                    |
| user\_id          | VARCHAR   | ID pengguna                |
| subject           | VARCHAR   | Mata pelajaran             |
| start\_time       | TIMESTAMP | Mulai sesi                 |
| end\_time         | TIMESTAMP | Selesai sesi               |
| duration\_minutes | INTEGER   | Durasi                     |
| session\_type     | VARCHAR   | Jenis sesi (Pomodoro, dsb) |
| completed         | BOOLEAN   | Status selesai             |

#### Tabel `courses`

| Kolom         | Tipe Data | Deskripsi        |
| ------------- | --------- | ---------------- |
| id            | UUID (PK) | ID mata kuliah   |
| user\_id      | VARCHAR   | ID pengguna      |
| course\_name  | VARCHAR   | Nama mata kuliah |
| credits       | INTEGER   | SKS              |
| grade\_point  | DECIMAL   | Nilai angka      |
| letter\_grade | VARCHAR   | Nilai huruf      |
| is\_completed | BOOLEAN   | Status selesai   |

#### Tabel `todo_items`

| Kolom     | Tipe Data | Deskripsi                   |
| --------- | --------- | --------------------------- |
| id        | UUID (PK) | ID tugas                    |
| user\_id  | VARCHAR   | ID pengguna                 |
| task      | TEXT      | Tugas                       |
| due\_date | DATE      | Deadline                    |
| priority  | VARCHAR   | Prioritas (High/Medium/Low) |

#### Tabel `study_analytics`

| Kolom                | Tipe Data | Deskripsi      |
| -------------------- | --------- | -------------- |
| id                   | UUID (PK) | ID analitik    |
| user\_id             | VARCHAR   | ID pengguna    |
| date                 | DATE      | Tanggal        |
| study\_time\_minutes | INTEGER   | Durasi belajar |

### Relasi Database:

```text
users (1) → (N) chat_history
users (1) → (N) study_sessions
users (1) → (N) courses
users (1) → (N) todo_items
users (1) → (N) study_analytics
```

---

## 🚀 Fitur & Fungsi Utama

### 💬 1. Chat AI System

* Natural conversation dalam Bahasa Indonesia
* Filtering otomatis topik akademik
* Gemini 2.0 Flash API
* Penyimpanan riwayat percakapan
* Empatik dan kontekstual

### ✅ 2. Todo Management

* Tambah/edit/hapus tugas
* Prioritas tugas (High/Medium/Low)
* Deadline & kategori
* Sinkronisasi real-time

### ⏱️ 3. Pomodoro Timer

* Timer 25/5/15 menit
* Progress bar visual
* Integrasi ke analytics
* Session tracking

### 🎓 4. GPA Calculator

* Input nilai dan SKS
* Perhitungan IPK otomatis
* Tracking total SKS
* Analisis performa akademik

### 📊 5. Study Analytics

* Visualisasi waktu belajar harian/mingguan
* Line chart, pie chart
* Distribusi mata pelajaran
* Laporan otomatis

---

## 🔁 Alur Sistem

### Startup Aplikasi:

* `Main.java` dijalankan
* Tes koneksi Supabase
* Load FXML + CSS
* Inisialisasi services
* Load data awal → tampilkan welcome message

### Alur Chat AI:

1. User kirim pesan
2. Validasi dan filter topik
3. Kirim ke Gemini API
4. Tampilkan respons & simpan history

### Alur Pomodoro:

1. Klik Mulai Pomodoro
2. Timer berjalan
3. Update analytics & session counter
4. Tampilkan notifikasi

---

## 🧠 Teknologi yang Digunakan

| Layer        | Teknologi                      |
| ------------ | ------------------------------ |
| **Frontend** | JavaFX 17, FXML, CSS3          |
| **Backend**  | Java 17, Gson, HTTP Client API |
| **Database** | Supabase PostgreSQL (REST API) |
| **AI**       | Google Gemini 2.0 Flash        |

---

## 📌 Keunggulan

| Fitur            | Benefit                                                                |
| ---------------- | ---------------------------------------------------------------------- |
| Chat AI Akademik | Bantu tugas dan pembelajaran 24/7 dengan bahasa Indonesia yang natural |
| Pomodoro Timer   | Fokus belajar terjaga dengan teknik yang terbukti efektif              |
| GPA Calculator   | Monitoring IPK & performa akademik real-time                           |
| Todo Management  | Organisasi tugas lebih rapi & efisien                                  |
| Study Analytics  | Insight belajar untuk optimasi kebiasaan belajar                       |
| Cloud Sync       | Data aman dan bisa diakses dari perangkat mana pun                     |

---
