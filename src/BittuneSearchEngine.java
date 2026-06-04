import java.util.ArrayList;

// ╔══════════════════════════════════════════════════════════════════════╗
// ║         BittuneSearchEngine — Arama Veri Yapıları & Algoritmaları   ║
// ╠══════════════════════════════════════════════════════════════════════╣
// ║                                                                      ║
// ║  İKİ BILEŞEN:                                                        ║
// ║                                                                      ║
// ║  1. BittuneSearchTrie — Ön Ek Ağacı (Trie / Prefix Tree)            ║
// ║     • Şarkı başlığı + sanatçı adı indekslenir                       ║
// ║     • Her düğüm o ön eke sahip şarkıların listesini tutar           ║
// ║     • insert O(k)  — k karakter uzunluğunda kelimeyi ekle           ║
// ║     • suggest O(k) — k karakter ön eke göre eşleşenleri bul        ║
// ║     • Türkçe normalize: toLowerCase(Locale.TR) ile ğ/ü/ş/ı/ö/ç     ║
// ║                                                                      ║
// ║  2. BittuneSongSearcher — Klasik Arama Algoritmaları                 ║
// ║     • Linear Search  O(n)       — sırasız liste, başlık+sanatçı+albüm║
// ║     • Binary Search  O(log n)   — sıralı listede başlık araması     ║
// ║                                                                      ║
// ║  KULLANIM AKIŞI (BittuneCore üzerinden):                             ║
// ║    1. trie.insert(song)            → uygulama başlangıcında         ║
// ║    2. trie.suggest("mel")          → arama kutusu her tuş basışında ║
// ║    3. Trie boş dönerse →                                             ║
// ║       BittuneSongSearcher.linearSearch(list, q)  → fallback O(n)    ║
// ║    4. BittuneSongSearcher.binarySearchByTitle()  → sıralı listede   ║
// ║                                                                      ║
// ║  Big-O Özeti:                                                        ║
// ║  ┌──────────────────────┬────────────┬───────────────────────────┐   ║
// ║  │ İşlem                │ Big-O      │ Açıklama                  │   ║
// ║  ├──────────────────────┼────────────┼───────────────────────────┤   ║
// ║  │ Trie insert          │ O(k)       │ k = kelime uzunluğu       │   ║
// ║  │ Trie suggest         │ O(k)       │ k = ön ek uzunluğu        │   ║
// ║  │ Linear Search        │ O(n)       │ n = liste boyutu          │   ║
// ║  │ Binary Search        │ O(log n)   │ liste sıralı olmalı       │   ║
// ║  └──────────────────────┴────────────┴───────────────────────────┘   ║
// ╚══════════════════════════════════════════════════════════════════════╝


// =============================================================
// BittuneSearchTrie — Ön Ek Ağacı (Trie / Prefix Tree)
//
// DSA: Trie
//   • Her düğüm bir karakter temsil eder
//   • Kök düğümden başlayarak kelime boyunca ilerler
//   • Her düğüm, o noktaya kadar olan ön eke sahip şarkıları saklar
//   • Türkçe karakter desteği: normalize() metoduyla küçük harfe çevrilir
//
// Neden Trie?
//   • HashMap ile O(1) tam eşleşme bulunur fakat ön ek araması O(n) ister.
//   • Trie ile ön ek araması O(k)'ya düşer (k = girilen karakter sayısı).
//   • Arama kutusunda her tuş basışında anlık öneri için idealdir.
// =============================================================
class BittuneSearchTrie {
    private final TrieNode root = new TrieNode();

    // ── İndeksleme ────────────────────────────────────────────────

    /**
     * Şarkının başlığını VE sanatçı adını Trie'ye ekler.
     * Her ikisi de normalize edilerek (küçük harf) saklanır.
     * Karmaşıklık: O(k) — k = başlık + sanatçı karakter sayısı
     */
    void insert(BittuneSong song) {
        insertWord(normalize(song.title),  song);
        insertWord(normalize(song.artist), song);
    }

    /**
     * Tek bir kelimeyi (normalize edilmiş) Trie'ye ekler.
     * Kelimenin her karakteri için düğüm oluşturur/günceller;
     * her düğüme bu ön eke sahip şarkıyı ekler.
     */
    private void insertWord(String word, BittuneSong song) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            curr.children.putIfAbsent(c, new TrieNode());
            curr = curr.children.get(c);
            if (!curr.songs.contains(song)) curr.songs.add(song);
        }
    }

    // ── Arama ────────────────────────────────────────────────────

    /**
     * Verilen ön eke göre eşleşen şarkıları döndürür.
     * Karmaşıklık: O(k) — k = prefix uzunluğu
     *
     * Boş prefix veya eşleşme yoksa boş liste döner.
     * Dönen liste BittuneCore.search() tarafından kullanılır.
     *
     * @param prefix Kullanıcının yazdığı arama metni
     * @return Ön ekle eşleşen şarkı listesi
     */
    ArrayList<BittuneSong> suggest(String prefix) {
        if (prefix == null || prefix.isEmpty()) return new ArrayList<>();
        TrieNode curr = root;
        for (char c : normalize(prefix).toCharArray()) {
            TrieNode next = curr.children.get(c);
            if (next == null) return new ArrayList<>();   // ön ek bulunamadı
            curr = next;
        }
        return new ArrayList<>(curr.songs);   // bu ön eke sahip tüm şarkılar
    }

    // ── Yardımcı ─────────────────────────────────────────────────

    /**
     * Türkçe locale ile küçük harf normalizasyonu.
     * "İ" → "i", "I" → "ı", "Ğ" → "ğ", "Ü" → "ü", "Ş" → "ş", "Ö" → "ö", "Ç" → "ç"
     * BittuneCore.matches() tarafından da kullanılır (static).
     */
    static String normalize(String s) {
        return s.toLowerCase(java.util.Locale.forLanguageTag("tr"));
    }

    // ── İç Düğüm ─────────────────────────────────────────────────

    /**
     * Trie düğümü.
     * children: sonraki karakterlere giden kenarlar (HashMap tabanlı)
     * songs: bu ön eke sahip şarkıların listesi
     */
    private static class TrieNode {
        final java.util.HashMap<Character, TrieNode> children = new java.util.HashMap<>();
        final ArrayList<BittuneSong> songs = new ArrayList<>();
    }
}


// =============================================================
// BittuneSongSearcher — Klasik Arama Algoritmaları
//
// DSA: Linear Search O(n) + Binary Search O(log n)
//
// Linear Search:
//   • Sırasız listede başlık, sanatçı ve albüm alanlarında arar
//   • Her eleman kontrol edildiğinden O(n)
//   • Trie'nin fallback'i olarak kullanılır
//
// Binary Search:
//   • Yalnızca SIRALANMIŞ listede çalışır
//   • Her adımda arama alanını yarıya böler → O(log n)
//   • mergeSortByTitle() sonrası çağrılmalıdır
// =============================================================
class BittuneSongSearcher {

    /**
     * Doğrusal Arama (Linear Search) — O(n).
     *
     * Liste boyunca her şarkıyı sırayla kontrol eder.
     * Başlık, sanatçı veya albümde eşleşme arar.
     * Sırasız veya küçük listelerde tercih edilir.
     * BittuneCore.findExact() zincirinin son fallback adımıdır.
     *
     * @param list  Arama yapılacak şarkı listesi
     * @param query Aranan metin (büyük/küçük harf duyarsız)
     * @return İlk eşleşen şarkı; bulunamazsa null
     */
    static BittuneSong linearSearch(ArrayList<BittuneSong> list, String query) {
        String needle = query.trim().toLowerCase();
        for (BittuneSong song : list) {
            if (song.title.toLowerCase().contains(needle)
                    || song.artist.toLowerCase().contains(needle)
                    || song.album.toLowerCase().contains(needle)) {
                return song;
            }
        }
        return null;
    }

    /**
     * İkili Arama (Binary Search) — O(log n).
     *
     * Her adımda listenin ortasına bakarak arama alanını yarıya böler.
     * Yalnızca başlığa göre artan sıralı listede doğru sonuç verir.
     * mergeSortByTitle() veya selectionSortByText("title") sonrası kullanılmalı.
     *
     * @param sorted Başlığa göre artan sıralı şarkı listesi
     * @param title  Aranan başlık (büyük/küçük harf duyarsız)
     * @return Eşleşen elemanın indeksi; bulunamazsa -1
     */
    static int binarySearchByTitle(ArrayList<BittuneSong> sorted, String title) {
        int lo = 0, hi = sorted.size() - 1;
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            int cmp = title.compareToIgnoreCase(sorted.get(mid).title);
            if (cmp == 0) return mid;        // tam eşleşme
            if (cmp  < 0) hi  = mid - 1;    // sol yarıya git
            else          lo  = mid + 1;    // sağ yarıya git
        }
        return -1;   // bulunamadı
    }
}
