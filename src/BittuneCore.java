import java.util.ArrayList;

// ╔══════════════════════════════════════════════════════════════════════╗
// ║            BittuneCore — Tüm DSA İşlemlerinin Merkezi              ║
// ╠══════════════════════════════════════════════════════════════════════╣
// ║                                                                      ║
// ║  GUI bu sınıfla konuşur. Doğrudan hiçbir veri yapısına erişmez.     ║
// ║                                                                      ║
// ║  ┌──────────────────────────────────────────────────────────────┐    ║
// ║  │  Bölüm          │ İçerik                                     │    ║
// ║  ├──────────────────────────────────────────────────────────────┤    ║
// ║  │  1. VERİ YAPILARI│ HashMap, BST, Trie, MaxHeap, Queue, Stack │    ║
// ║  │  2. İNDEKSLEME  │ buildIndex(), updateEntry()                │    ║
// ║  │  3. FİLTRE      │ showAll(), showLiked(), showPlaylist()      │    ║
// ║  │  4. ARAMA       │ search() Trie→Linear, findExact() H→B→L    │    ║
// ║  │  5. SIRALAMA    │ sortColumn() → Merge/Insertion/Selection    │    ║
// ║  │  6. KUYRUK(FIFO)│ enqueue(), dequeueOrNull(), clearQueue()   │    ║
// ║  │  7. YIĞIN(LIFO) │ pushHistory(), popHistory()                │    ║
// ║  │  8. NAVİGASYON  │ nextSong(), previousSong(), upNextSongs()  │    ║
// ║  │  9. TOP-N       │ topN(n) — MaxHeap öncelikli çıkarma        │    ║
// ║  └──────────────────────────────────────────────────────────────┘    ║
// ║                                                                      ║
// ║  Algoritma → Big-O tablosu:                                          ║
// ║  ┌───────────────────┬──────────────┬──────────────────────────┐     ║
// ║  │ Algoritma         │ Big-O        │ Kullanım yeri            │     ║
// ║  ├───────────────────┼──────────────┼──────────────────────────┤     ║
// ║  │ HashMap put/get   │ O(1) ort.    │ Başlığa anlık erişim     │     ║
// ║  │ BST insert/search │ O(log n) ort.│ Sıralı arama fallback   │     ║
// ║  │ Trie insert/find  │ O(k)         │ Arama kutusu önerisi     │     ║
// ║  │ MaxHeap ins/ext   │ O(log n)     │ Top-N en çok çalınan     │     ║
// ║  │ Queue enq/deq     │ O(1)         │ Sıraya ekle / FIFO       │     ║
// ║  │ Stack push/pop    │ O(1)         │ Önceki tuşu / LIFO       │     ║
// ║  │ Merge Sort        │ O(n log n)   │ Başlık sütunu sıralama   │     ║
// ║  │ Insertion Sort    │ O(n²)        │ Süre sütunu sıralama     │     ║
// ║  │ Selection Sort    │ O(n²)        │ Sanatçı/Albüm/Beğeni     │     ║
// ║  │ Linear Search     │ O(n)         │ Arama fallback           │     ║
// ║  │ Binary Search     │ O(log n)     │ Sıralı listede arama     │     ║
// ║  └───────────────────┴──────────────┴──────────────────────────┘     ║
// ╚══════════════════════════════════════════════════════════════════════╝

class BittuneCore {

    // ══════════════════════════════════════════════════════════════════
    //  BÖLÜM 1 — VERİ YAPILARI (6 Yapı)
    // ══════════════════════════════════════════════════════════════════

    // DSA: Hash Tablosu — Separate Chaining (Ayrı Zincirleme)
    // Yük faktörü > 0.75 → otomatik rehash (kapasite 2×)
    // put O(1) ort. | get O(1) ort.
    private final BittuneSongHashMap hashMap = new BittuneSongHashMap();

    // DSA: İkili Arama Ağacı (Binary Search Tree — BST)
    // Başlığa göre alfabetik sıralama; InOrder gezinti = sıralı liste
    // insert O(log n) ort. | search O(log n) ort.
    private final BittuneSongBST bst = new BittuneSongBST();

    // DSA: Trie (Ön Ek Ağacı / Prefix Tree)
    // HashMap tabanlı düğümler; Türkçe locale normalize (ğ, ü, ş, ı, ö, ç)
    // insert O(k) | suggest O(k)    (k = prefix uzunluğu)
    private final BittuneSearchTrie trie = new BittuneSearchTrie();

    // DSA: MaxHeap (Öncelikli Kuyruk — Priority Queue)
    // Dizi tabanlı; priority = playCount × 10 + likes
    // insert O(log n) | extractMax O(log n) | topN O(n log k)
    private final BittuneMaxHeap heap = new BittuneMaxHeap(512);

    // DSA: Kuyruk (Queue — FIFO, First In First Out)
    // Tek yönlü bağlı liste; "Sıraya Ekle" işlevi
    // enqueue O(1) | dequeue O(1)
    private final BittuneSongQueue queue = new BittuneSongQueue();

    // DSA: Geçmiş Yığını (Stack — LIFO, Last In First Out)
    // Tek yönlü bağlı liste; "← Önceki" tuşu geri geçmişi
    // push O(1) | pop O(1)
    private final BittuneSongHistoryStack history = new BittuneSongHistoryStack();

    // ── Paylaşılan referanslar (GUI ile aynı nesneler, kopyalanmaz) ──
    private final ArrayList<BittuneSong>    allSongs;
    private final ArrayList<BittuneSong>    visibleSongs;
    private final BittunePlaylistLinkedList playlists;

    // ── Durum alanları — GUI tarafından senkronize edilir ────────────
    ArrayList<BittuneSong> playContext;
    String                  playContextName = "Tüm Şarkılar";
    int                     lastSortColumn  = -1;
    boolean                 sortAscending   = true;

    // ── Kurucu ────────────────────────────────────────────────────────

    BittuneCore(ArrayList<BittuneSong> allSongs,
                 ArrayList<BittuneSong> visibleSongs,
                 BittunePlaylistLinkedList playlists) {
        this.allSongs     = allSongs;
        this.visibleSongs = visibleSongs;
        this.playlists    = playlists;
        this.playContext  = new ArrayList<>(allSongs);
    }

    // ══════════════════════════════════════════════════════════════════
    //  BÖLÜM 2 — İNDEKSLEME
    // ══════════════════════════════════════════════════════════════════

    /**
     * Tüm şarkıları altı DSA yapısına indeksler.
     * Uygulama başlangıcında applyUserLikes()'tan SONRA çağrılmalı.
     * (Beğeni durumu MaxHeap önceliğini etkiler.)
     *
     * Her yapı için Big-O:
     *   hashMap.put  → O(1) ort.
     *   bst.insert   → O(log n) ort.
     *   trie.insert  → O(k)
     *   heap.insert  → O(log n)
     */
    void buildIndex(ArrayList<BittuneSong> songs) {
        for (BittuneSong s : songs) {
            hashMap.put(s);   // Hash Tablosuna ekle — O(1) ort.
            bst.insert(s);    // BST'ye ekle         — O(log n) ort.
            trie.insert(s);   // Trie'ye ekle        — O(k)
            heap.insert(s);   // MaxHeap'e ekle      — O(log n)
        }
        System.out.printf(
            "[BittuneCore] %d şarkı indekslendi | BST yüksekliği: %d | HashMap: %d kayıt%n",
            songs.size(), bst.height(), hashMap.size()
        );
    }

    /**
     * Tek bir şarkının HashMap ve BST girişini günceller.
     * toggleLike() veya playCount değişince çağrılır.
     * Trie güncelleme gerektirmez (başlık değişmez).
     *
     *   hashMap.put  → O(1) ort.
     *   bst.insert   → O(log n) ort.
     */
    void updateEntry(BittuneSong song) {
        hashMap.put(song);   // varsa günceller, yoksa ekler — O(1) ort.
        bst.insert(song);    // varsa başlık eşleşirse günceller — O(log n)
    }

    // ══════════════════════════════════════════════════════════════════
    //  BÖLÜM 3 — FİLTRE
    // ══════════════════════════════════════════════════════════════════

    /**
     * Tüm şarkıları gösterir.
     * @return bölüm başlığı ("Tüm Şarkılar")
     */
    String showAll() {
        applyFilter(s -> true);
        playContext     = new ArrayList<>(visibleSongs);
        playContextName = "Tüm Şarkılar";
        return "Tüm Şarkılar";
    }

    /**
     * Yalnızca beğenilen şarkıları gösterir; beğenilme zamanına göre sıralar.
     * @return bölüm başlığı ("Beğenilenler")
     */
    String showLiked() {
        applyFilter(s -> s.liked);
        visibleSongs.sort((a, b) -> Long.compare(a.likedAt, b.likedAt));
        playContext     = new ArrayList<>(visibleSongs);
        playContextName = "Beğenilenler";
        return "Beğenilenler";
    }

    /**
     * Belirli bir playlist'i gösterir.
     * @return bölüm başlığı (playlist adı)
     */
    String showPlaylist(String name) {
        visibleSongs.clear();
        visibleSongs.addAll(playlists.songsOf(name));
        playContext     = new ArrayList<>(visibleSongs);
        playContextName = name;
        return name;
    }

    // ══════════════════════════════════════════════════════════════════
    //  BÖLÜM 4 — ARAMA (Çok Katmanlı Strateji)
    // ══════════════════════════════════════════════════════════════════

    /**
     * Arama kutusu için çok katmanlı DSA stratejisi:
     *   Adım 1: Trie ön ek araması        — O(k)  (k = prefix uzunluğu)
     *   Adım 2: Linear Search fallback    — O(n)  (Trie boş dönerse)
     *
     * @param query Kullanıcının arama metni
     * @return bölüm başlığı (ör. "Arama: müzik")
     */
    String search(String query) {
        if (query == null || query.trim().isEmpty()) return showAll();
        String q = query.trim();

        // Adım 1: Trie ön ek araması — O(k)
        ArrayList<BittuneSong> trieHits = trie.suggest(q);
        if (!trieHits.isEmpty()) {
            visibleSongs.clear();
            visibleSongs.addAll(trieHits);
        } else {
            // Adım 2: Linear Search fallback — tam içerik eşleştirmesi — O(n)
            applyFilter(s -> matches(s, q));
        }
        playContext     = new ArrayList<>(visibleSongs);
        playContextName = "Ara";
        return "Arama: " + q;
    }

    /**
     * Tam başlık araması — üç katmanlı DSA zinciri:
     *   Adım 1: HashMap  — O(1) ort.   (anlık eşleşme)
     *   Adım 2: BST      — O(log n)    (HashMap'ten kaçanlar)
     *   Adım 3: Linear   — O(n)        (son çare fallback)
     *
     * @param title Aranan başlık (büyük/küçük harf duyarsız)
     */
    BittuneSong findExact(String title) {
        // Adım 1: Hash Tablosu — O(1) ortalama
        BittuneSong found = hashMap.get(title);
        if (found != null) return found;

        // Adım 2: BST — O(log n) ortalama
        found = bst.search(title);
        if (found != null) return found;

        // Adım 3: Linear Search — O(n) fallback
        return BittuneSongSearcher.linearSearch(allSongs, title);
    }

    // ══════════════════════════════════════════════════════════════════
    //  BÖLÜM 5 — SIRALAMA
    // ══════════════════════════════════════════════════════════════════

    /**
     * Sütun numarasına göre sıralama yapar.
     * İki kez aynı sütuna basınca yön tersine döner (toggle).
     *
     * Algoritma seçimi sütuna göre:
     *   1 (Başlık)  → Merge Sort      O(n log n) — büyük listede en hızlı
     *   2 (Sanatçı) → Selection Sort  O(n²)
     *   3 (Albüm)   → Selection Sort  O(n²)
     *   4 (Süre)    → Insertion Sort  O(n²)      — neredeyse sıralıda çok hızlı
     *   5 (Beğeni)  → Selection Sort  O(n²)
     *
     * @param column 1-5 arası sütun indeksi
     * @param base   mevcut bölüm başlığı (suffix eklenir)
     * @return yeni bölüm başlığı
     */
    String sortColumn(int column, String base) {
        if (column < 1 || column > 5) return base;
        if (lastSortColumn == column) sortAscending = !sortAscending;
        else { lastSortColumn = column; sortAscending = (column != 5); }

        String suffix = sortBy(visibleSongs, column, sortAscending);
        return suffix.isEmpty() ? base : base + "  /  " + suffix;
    }

    /**
     * Verilen listeyi sütuna göre yerinde (in-place) sıralar.
     *
     * @param list      Sıralanacak liste
     * @param col       1-5 arası sütun numarası
     * @param ascending true = A-Z / küçükten büyüğe
     * @return Sütun etiket metni (GUI başlığı için)
     */
    private String sortBy(ArrayList<BittuneSong> list, int col, boolean ascending) {
        switch (col) {
            case 1 -> {
                // ── Birleşim Sıralaması (Merge Sort) — O(n log n) ──────────
                // Büyük listelerde Selection/Insertion Sort'tan belirgin hızlı.
                // Bölme → Birleştirme; kararlı (stable) sıralama.
                BittuneSongSorter.mergeSortByTitle(list, ascending);
                return "Başlık " + (ascending ? "A-Z" : "Z-A");
            }
            case 2 -> {
                // ── Seçim Sıralaması (Selection Sort) — O(n²) ─────────────
                // Her turda minimumu/maksimumu bulup başa/sona alır.
                BittuneSongSorter.selectionSortByText(list, "artist", ascending);
                return "Sanatçı " + (ascending ? "A-Z" : "Z-A");
            }
            case 3 -> {
                // ── Seçim Sıralaması (Selection Sort) — O(n²) ─────────────
                BittuneSongSorter.selectionSortByText(list, "album", ascending);
                return "Albüm " + (ascending ? "A-Z" : "Z-A");
            }
            case 4 -> {
                // ── Ekleme Sıralaması (Insertion Sort) — O(n²) ─────────────
                // Neredeyse sıralı listelerde Bubble/Selection Sort'tan çok hızlı;
                // her elemanı doğru konumuna kaydırır.
                BittuneSongSorter.insertionSortByDuration(list, ascending);
                return "Süre " + (ascending ? "kısadan uzuna" : "uzundan kısaya");
            }
            case 5 -> {
                // ── Seçim Sıralaması (Selection Sort) — O(n²) ─────────────
                BittuneSongSorter.selectionSortByLiked(list, !ascending);
                return ascending ? "Önce beğenilmeyenler" : "Önce beğenilenler";
            }
            default -> { return ""; }
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  BÖLÜM 6 — KUYRUK (Queue — FIFO)
    // ══════════════════════════════════════════════════════════════════

    /**
     * Şarkıyı kuyruğun sonuna ekler.
     * DSA: Queue — FIFO bağlı liste — O(1)
     */
    void enqueue(BittuneSong song) {
        queue.enqueue(song);   // O(1) — tail pointer ile
    }

    /**
     * Kuyruğun başından şarkı çıkarır.
     * DSA: Queue — FIFO bağlı liste — O(1)
     * Kuyruk boşsa null döner.
     */
    BittuneSong dequeueOrNull() {
        return queue.dequeue();   // O(1) — head pointer ile
    }

    /**
     * Kuyruğu tamamen sıfırlar — O(1).
     */
    void clearQueue() {
        queue.clear();
    }

    /**
     * Tek bir şarkıyı kuyruktan kaldırır — O(n).
     * DSA: Queue bağlı listesinde düğüm silme.
     */
    boolean removeFromQueue(BittuneSong song) {
        return queue.remove(song);
    }

    /**
     * Mevcut kuyruk içeriğinin kopyasını döner (UI listesi için).
     */
    ArrayList<BittuneSong> queueSnapshot() {
        return queue.toArrayList();
    }

    /**
     * Kuyruk boş mu?
     */
    boolean isQueueEmpty() {
        return queue.toArrayList().isEmpty();
    }

    // ══════════════════════════════════════════════════════════════════
    //  BÖLÜM 7 — YIĞIN (Stack — LIFO)
    // ══════════════════════════════════════════════════════════════════

    /**
     * Şarkıyı geçmiş yığınına iter.
     * DSA: Stack — LIFO bağlı liste — O(1)
     * Kullanım: Yeni şarkıya geçince eski şarkı yığına itilir.
     */
    void pushHistory(BittuneSong song) {
        if (song != null) history.push(song);   // O(1) — head'e ekle
    }

    /**
     * Yığından önceki şarkıyı çıkarır.
     * DSA: Stack — LIFO bağlı liste — O(1)
     * Yığın boşsa null döner.
     */
    BittuneSong popHistory() {
        return history.pop();   // O(1) — head'den çıkar
    }

    // ══════════════════════════════════════════════════════════════════
    //  BÖLÜM 8 — NAVİGASYON (Sonraki / Önceki şarkı)
    // ══════════════════════════════════════════════════════════════════

    /**
     * Bir sonraki şarkıyı belirler — öncelik sırası:
     *   1. Queue'dan çek (kullanıcı sıraya eklediyse) — O(1)  FIFO
     *   2. Queue boşsa aktif bağlamdan döngüsel ilerle — O(n)
     *
     * @param current Şu an çalan şarkı
     * @param context Aktif oynatma bağlamı (filtre/playlist/arama)
     */
    BittuneSong nextSong(BittuneSong current, ArrayList<BittuneSong> context) {
        BittuneSong fromQueue = queue.dequeue();   // O(1) — FIFO çıkarma
        if (fromQueue != null) return fromQueue;
        return nextInList(current, resolveSource(context));
    }

    /**
     * Önceki şarkıyı belirler — öncelik sırası:
     *   1. History Stack'ten çek (pushHistory ile kaydedildiyse) — O(1)  LIFO
     *   2. Stack boşsa bağlamda geriye döner — O(n)
     *
     * @param current Şu an çalan şarkı
     * @param context Aktif oynatma bağlamı
     */
    BittuneSong previousSong(BittuneSong current, ArrayList<BittuneSong> context) {
        BittuneSong fromStack = history.pop();   // O(1) — LIFO çıkarma
        if (fromStack != null) return fromStack;
        return previousInList(current, resolveSource(context));
    }

    /**
     * Sağ panelde "Sıradakiler" listesini oluşturur.
     * Bağlamda şarkı yoksa Queue içeriğini döner.
     * Bağlamda şarkı varsa döngüsel olarak ilerler.
     *
     * @param current Şu an çalan şarkı
     * @param context Aktif oynatma bağlamı
     */
    ArrayList<BittuneSong> upNextSongs(BittuneSong current, ArrayList<BittuneSong> context) {
        return upNextSongs(current, context, false);
    }

    /** noWrap=true → kullanıcı playlist'inde son şarkıdan sonra başa dönme */
    ArrayList<BittuneSong> upNextSongs(BittuneSong current, ArrayList<BittuneSong> context, boolean noWrap) {
        // Queue şarkıları HER ZAMAN önce gelir (FIFO)
        ArrayList<BittuneSong> result = new ArrayList<>(queue.toArrayList());

        // Ardından context şarkıları (playlist / albüm sırası)
        if (current != null && context != null
                && !context.isEmpty() && context.contains(current)) {
            int idx = context.indexOf(current);
            for (int step = 1; step < context.size(); step++) {
                int nextIdx = idx + step;
                if (noWrap && nextIdx >= context.size()) break; // playlist bitti, dur
                BittuneSong s = context.get(nextIdx % context.size());
                if (!result.contains(s)) result.add(s);
            }
        }

        return result;
    }

    // ══════════════════════════════════════════════════════════════════
    //  BÖLÜM 9 — TOP-N (MaxHeap — Öncelikli Kuyruk)
    // ══════════════════════════════════════════════════════════════════

    /**
     * En çok çalınan / beğenilen n şarkıyı döner.
     * DSA: MaxHeap — Öncelikli Kuyruk
     *   priority = playCount × 10 + likes
     *   insert O(log n) | extractMax O(log n) | topN → O(n log k)
     *
     * Anlık playCount değerleri için geçici heap kurulur —
     * orijinal heap korunur (kopyası üzerinde çalışır).
     *
     * @param songs Anlık şarkı listesi
     * @param n     Kaç şarkı döndürülsün
     * @return En yüksek öncelikli n şarkı; tümü 0 ise ilk n'i döner
     */
    ArrayList<BittuneSong> topN(ArrayList<BittuneSong> songs, int n) {
        if (songs.isEmpty()) return new ArrayList<>();
        BittuneMaxHeap tmp = new BittuneMaxHeap(songs.size() + 1);
        for (BittuneSong s : songs) tmp.insert(s);          // O(n log n) toplam
        ArrayList<BittuneSong> result = tmp.topN(n);        // O(n log k)
        // Tümü 0 öncelikli ise ilk n'i fallback olarak göster
        return result.isEmpty()
            ? new ArrayList<>(songs.subList(0, Math.min(n, songs.size())))
            : result;
    }

    // ══════════════════════════════════════════════════════════════════
    //  TEŞHİS / İSTATİSTİK
    // ══════════════════════════════════════════════════════════════════

    /** BST yüksekliği — dengesiz büyüme kontrolü için. */
    int bstHeight()   { return bst.height(); }

    /** HashMap'teki kayıt sayısı. */
    int hashMapSize() { return hashMap.size(); }

    // ══════════════════════════════════════════════════════════════════
    //  YARDIMCILAR (private)
    // ══════════════════════════════════════════════════════════════════

    /** visibleSongs'u filtreye göre in-place günceller. */
    private void applyFilter(SongFilter f) {
        visibleSongs.clear();
        for (BittuneSong s : allSongs) if (f.accept(s)) visibleSongs.add(s);
    }

    /** Bir şarkının arama terimiyle eşleşip eşleşmediğini kontrol eder. */
    private boolean matches(BittuneSong s, String q) {
        String needle = BittuneSearchTrie.normalize(q);
        return BittuneSearchTrie.normalize(s.title).contains(needle)
            || BittuneSearchTrie.normalize(s.artist).contains(needle)
            || BittuneSearchTrie.normalize(s.album).contains(needle);
    }

    /** Bağlam boşsa tüm şarkı listesini kaynak olarak kullan. */
    private ArrayList<BittuneSong> resolveSource(ArrayList<BittuneSong> context) {
        return (context == null || context.isEmpty()) ? allSongs : context;
    }

    private BittuneSong nextInList(BittuneSong current, ArrayList<BittuneSong> source) {
        if (source.isEmpty()) return null;
        int idx = source.indexOf(current);
        if (idx < 0) { source = allSongs; idx = allSongs.indexOf(current); }
        if (source.isEmpty()) return null;
        return source.get((idx + 1 + source.size()) % source.size());
    }

    private BittuneSong previousInList(BittuneSong current, ArrayList<BittuneSong> source) {
        if (source.isEmpty()) return null;
        int idx = source.indexOf(current);
        if (idx < 0) { source = allSongs; idx = allSongs.indexOf(current); }
        if (source.isEmpty()) return null;
        return source.get((idx - 1 + source.size()) % source.size());
    }
}
