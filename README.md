[README.md](https://github.com/user-attachments/files/28606926/README.md)
# Bittune — Java Swing Müzik Çalar Uygulaması

> Veri Yapıları ve Algoritmalar dersi dönem projesi.
> Spotify'dan ilham alan, Java 21 + Swing ile yazılmış masaüstü müzik çaları.
> Tüm DSA yapıları (Queue, Stack, LinkedList, HashMap, BST, Trie, MaxHeap) **sıfırdan** yazılmıştır — hazır `java.util` koleksiyonları kullanılmaz.

---

## Hazırlayanlar

| # | Ad Soyad | Numara |
|---|---|---|
| 1 | Murat ÇOLAKOĞLU | 171424011 |
| 2 | Ahmet Emre ÖZÜMAĞI | 170424029 |
| 3 | Nesrin GÜLER | 170424044 |

**Marmara Üniversitesi — Teknoloji Fakültesi — 2026 Bahar Dönemi**

---

## Özellikler

- MP3 dosyalarını gerçek zamanlı oynatma (ID3 tag + kapak resmi okuma)
- Kullanıcı kayıt / giriş + oturum hatırlama
- Şarkı listesi, beğeni, "En çok çalınanlar" kartları
- Playlist oluşturma, içine şarkı ekleme, silme
- Şarkı kuyruğu (FIFO) + "Önceki şarkı" geçmişi (LIFO)
- Anlık arama (Trie prefix önerisi + linear fallback)
- Kolon başlığına göre sıralama (Merge / Insertion / Selection Sort)
- Karanlık tema, mor vurgular, modern UI

---

## Kullanılan DSA Yapıları

| Yapı | Sınıf | Dosya |
|---|---|---|
| LinkedList | `BittunePlaylistLinkedList` | `BittuneDataStructures.java` |
| Queue (FIFO) | `BittuneSongQueue` | `BittuneQueueStack.java` |
| Stack (LIFO) | `BittuneSongHistoryStack` | `BittuneQueueStack.java` |
| HashMap | `BittuneSongHashMap` | `BittuneTreeIndex.java` |
| BST | `BittuneSongBST` | `BittuneTreeIndex.java` |
| Trie | `BittuneSearchTrie` | `BittuneSearchEngine.java` |
| MaxHeap | `BittuneMaxHeap` | `BittuneHeap.java` |
| Circular Buffer | `AudioBufferQueue` | `AudioBufferQueue.java` |

Algoritmalar: Merge Sort, Insertion Sort, Selection Sort, Linear Search, Binary Search, Trie Prefix Search, BST Inorder Traversal, MaxHeap Top-N.

---

## Çalıştırma

### Gereksinimler
- Java 21 veya üzeri
- `lib/` klasöründeki JAR'lar (JLayer, MP3SPI, Tritonus-Share, mp3agic)

### Derleme

```powershell
javac -encoding UTF-8 -cp "lib/*" -d out src/*.java
```

### Çalıştırma

```powershell
java -cp "out;lib/*" BittuneGUI
```

### DSA Testlerini Koşma

```powershell
java -cp "out;lib/*" DSATestRunner
```

8 otomatik test grubu (Queue, Stack, LinkedList, HashMap, BST, MergeSort, Trie, MaxHeap) `pass/fail` raporu verir.

---

## MP3 Dosyası Ekleme

- MP3 dosyalarını `audio/` klasörüne kopyalayın.
- `Sanatçı - Başlık.mp3` formatı otomatik ayrıştırılır.
- ID3 tag varsa öncelikle ondan okunur (kapak resmi dahil).
- Uygulama her açılışta `audio/` klasörünü tarar.

---

## Dökümantasyon

Detaylı proje raporu (kapak + 14 bölüm + Big-O analizi + test senaryoları):



---

## Lisans

Bu proje yalnızca akademik amaçlıdır.
