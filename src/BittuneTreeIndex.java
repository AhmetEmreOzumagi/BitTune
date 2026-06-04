import java.util.ArrayList;

// ╔══════════════════════════════════════════════════════════════════════╗
// ║        BittuneTreeIndex — Hash Tablosu & BST Veri Yapıları          ║
// ╠══════════════════════════════════════════════════════════════════════╣
// ║                                                                      ║
// ║  İKİ BILEŞEN:                                                        ║
// ║                                                                      ║
// ║  1. BittuneSongHashMap — Hash Tablosu (Separate Chaining)            ║
// ║     • Başlık → BittuneSong hızlı eşleştirmesi                       ║
// ║     • put O(1) ort.  |  get O(1) ort.                               ║
// ║     • Çakışma çözümü: Ayrı Zincirleme (linked list per bucket)      ║
// ║     • Yük faktörü λ = size/capacity > 0.75 → rehash (kapasite 2×)  ║
// ║     • Hash fonksiyonu: polynomial rolling — h = h*31 + c            ║
// ║                                                                      ║
// ║  2. BittuneSongBST — İkili Arama Ağacı (Binary Search Tree)         ║
// ║     • Başlığa göre alfabetik düzenleme                               ║
// ║     • insert O(log n) ort.  |  search O(log n) ort.                 ║
// ║     • InOrder gezinti → tam sıralı şarkı listesi                    ║
// ║     • HashMap'in fallback'i olarak kullanılır                        ║
// ║                                                                      ║
// ║  NEDEN HEM HASHMAP HEM BST?                                          ║
// ║    findExact zinciri (BittuneCore.findExact):                        ║
// ║      1. HashMap.get()   O(1) ort.  → anlık eşleşme                  ║
// ║      2. BST.search()    O(log n)   → HashMap'ten kaçanlar           ║
// ║      3. LinearSearch()  O(n)       → son çare fallback               ║
// ║    Her katman, bir öncekinin gözden kaçırdığını yakalar.             ║
// ║                                                                      ║
// ║  Big-O Özeti:                                                        ║
// ║  ┌──────────────────────┬──────────────┬────────────────────────┐    ║
// ║  │ İşlem                │ Big-O        │ Açıklama               │    ║
// ║  ├──────────────────────┼──────────────┼────────────────────────┤    ║
// ║  │ HashMap put          │ O(1) ort.    │ hash → bucket → ekle   │    ║
// ║  │ HashMap get          │ O(1) ort.    │ hash → bucket → ara    │    ║
// ║  │ HashMap rehash       │ O(n)         │ λ>0.75 tetikler        │    ║
// ║  │ BST insert           │ O(log n) ort.│ karşılaştırarak ilerle │    ║
// ║  │ BST search           │ O(log n) ort.│ sola/sağa dallan       │    ║
// ║  │ BST inorder          │ O(n)         │ tüm ağaç gezintisi     │    ║
// ║  └──────────────────────┴──────────────┴────────────────────────┘    ║
// ╚══════════════════════════════════════════════════════════════════════╝


// =============================================================
// BittuneSongHashMap — Özel Hash Tablosu (Separate Chaining)
//
// DSA: Hash Table
//   Hash fonksiyonu: polynomial rolling hash (h = h*31 + c)
//   Çakışma çözümü: Separate Chaining (her bucket bir bağlı liste)
//   Yük faktörü eşiği: 0.75 → aşılınca rehash (2× büyüme)
//
// Neden Hash Tablosu?
//   • findExact zincirinin ilk adımı — O(1) ile başlığa anında erişir
//   • BST O(log n) iken HashMap O(1) — ilk tercih bu yüzden
//
// Bağlantı: BittuneCore.findExact() ve buildIndex() / updateEntry()
// =============================================================
class BittuneSongHashMap {
    private static final int INITIAL_CAP = 64;   // başlangıç kapasitesi
    private ChainNode[] table;
    private int size;
    private int capacity;

    BittuneSongHashMap() {
        this.capacity = INITIAL_CAP;
        this.table    = new ChainNode[capacity];
    }

    // ── Ekleme / Güncelleme ───────────────────────────────────────

    /**
     * Şarkıyı başlık anahtarıyla ekler; zaten varsa günceller — O(1) ort.
     *
     * Adımlar:
     *   1. Yük faktörü > 0.75 ise rehash
     *   2. hash(title) → bucket indeksi
     *   3. Bucket zincirini tara: başlık eşleşirse güncelle
     *   4. Yoksa zincirin başına yeni düğüm ekle (O(1))
     */
    void put(BittuneSong song) {
        if ((float) size / capacity > 0.75f) rehash();
        int idx = hash(song.title, capacity);
        for (ChainNode c = table[idx]; c != null; c = c.next) {
            if (c.song.title.equalsIgnoreCase(song.title)) {
                c.song = song;   // var olan kaydı güncelle
                return;
            }
        }
        // Yeni kayıt — bucket başına ekle (O(1))
        ChainNode node = new ChainNode(song);
        node.next  = table[idx];
        table[idx] = node;
        size++;
    }

    // ── Arama ────────────────────────────────────────────────────

    /**
     * Başlığa göre şarkı arar — O(1) ortalama.
     * hash(title) → doğru bucket → zinciri tara.
     * Bulunamazsa null döner.
     */
    BittuneSong get(String title) {
        for (ChainNode c = table[hash(title, capacity)]; c != null; c = c.next)
            if (c.song.title.equalsIgnoreCase(title)) return c.song;
        return null;
    }

    boolean containsKey(String title) { return get(title) != null; }
    int     size()                    { return size; }

    // ── Hash Fonksiyonu ──────────────────────────────────────────

    /**
     * Polynomial Rolling Hash: h = (h * 31 + c) mod cap
     * Her karakter 31 ile çarpılarak güçlü bir dağılım sağlanır.
     * Büyük/küçük harf duyarsızlığı için lowercase uygulanır.
     */
    private int hash(String key, int cap) {
        int h = 0;
        for (char c : key.toLowerCase().toCharArray())
            h = (h * 31 + c) & Integer.MAX_VALUE;
        return h % cap;
    }

    // ── Rehash ───────────────────────────────────────────────────

    /**
     * Yük faktörü 0.75'i geçince kapasiteyi iki katına çıkarır — O(n).
     * Tüm mevcut kayıtlar yeni tabloya yeniden hash edilir.
     * Bu işlem amortize O(1) maliyete katkıda bulunur.
     */
    private void rehash() {
        capacity *= 2;
        ChainNode[] newTable = new ChainNode[capacity];
        for (ChainNode head : table) {
            for (ChainNode c = head; c != null; ) {
                ChainNode next = c.next;
                int idx = hash(c.song.title, capacity);
                c.next = newTable[idx];
                newTable[idx] = c;
                c = next;
            }
        }
        table = newTable;
    }

    // ── Düğüm ────────────────────────────────────────────────────
    private static class ChainNode {
        BittuneSong song;
        ChainNode   next;
        ChainNode(BittuneSong song) { this.song = song; }
    }
}


// =============================================================
// BittuneSongBST — İkili Arama Ağacı (Binary Search Tree)
//
// DSA: BST
//   Özellik: sol alt ağaç < kök < sağ alt ağaç (alfabetik sıraya göre)
//   insert O(log n) ort.  |  search O(log n) ort.
//   InOrder gezinti → alfabetik sıralı şarkı listesi
//
// Neden BST?
//   • HashMap'in fallback'i — HashMap O(1) sunarken BST O(log n) ile
//     ikinci savunma hattını oluşturur
//   • InOrder ile tüm şarkıları sıralı almak mümkündür
//   • Tüm şarkıları bellekte sıralı tutmak yerine isteğe göre sıralı
//     çıktı almayı sağlar (height() istatistik olarak da kullanılır)
//
// Bağlantı: BittuneCore.findExact() ve buildIndex() / updateEntry()
// =============================================================
class BittuneSongBST {
    private BSTNode root;

    // ── Ekleme ───────────────────────────────────────────────────

    /**
     * BST'ye şarkı ekler — O(log n) ortalama.
     * Başlık karşılaştırmasına göre sola (küçük) veya sağa (büyük) ilerler.
     * Aynı başlık varsa günceller (duplicate yok).
     */
    void insert(BittuneSong song) {
        root = insertRec(root, song);
    }

    private BSTNode insertRec(BSTNode node, BittuneSong song) {
        if (node == null) return new BSTNode(song);   // yer bulundu → düğüm oluştur
        int cmp = song.title.compareToIgnoreCase(node.song.title);
        if      (cmp < 0) node.left  = insertRec(node.left,  song);   // sola git
        else if (cmp > 0) node.right = insertRec(node.right, song);   // sağa git
        else              node.song  = song;                           // güncelle
        return node;
    }

    // ── Arama ────────────────────────────────────────────────────

    /**
     * Tam başlık eşleşmesiyle arama — O(log n) ortalama.
     * Her adımda ağaç yarıya bölünür.
     * Bulunamazsa null döner; BittuneCore.findExact() LinearSearch'e geçer.
     */
    BittuneSong search(String title) {
        return searchRec(root, title);
    }

    private BittuneSong searchRec(BSTNode node, String title) {
        if (node == null) return null;
        int cmp = title.compareToIgnoreCase(node.song.title);
        if (cmp == 0) return node.song;                                           // bulundu
        return cmp < 0 ? searchRec(node.left, title) : searchRec(node.right, title);
    }

    // ── InOrder Gezinti ──────────────────────────────────────────

    /**
     * InOrder (Sol → Kök → Sağ) gezinti ile alfabetik sıralı liste döner.
     * BST özelliği gereği InOrder her zaman sıralı sonuç verir.
     * Karmaşıklık: O(n) — tüm düğümler ziyaret edilir.
     */
    ArrayList<BittuneSong> inorderList() {
        ArrayList<BittuneSong> result = new ArrayList<>();
        inorderRec(root, result);
        return result;
    }

    private void inorderRec(BSTNode node, ArrayList<BittuneSong> list) {
        if (node == null) return;
        inorderRec(node.left, list);    // önce sol alt ağaç
        list.add(node.song);            // kök
        inorderRec(node.right, list);   // sonra sağ alt ağaç
    }

    // ── İstatistik ───────────────────────────────────────────────

    /**
     * Ağaç yüksekliğini döner — dengesizlik kontrolü için.
     * Dengeli BST: height ≈ log₂(n)
     * Tamamen dengesiz (sorted insert): height = n (zincir gibi)
     */
    int height() {
        return heightRec(root);
    }

    private int heightRec(BSTNode n) {
        if (n == null) return 0;
        return 1 + Math.max(heightRec(n.left), heightRec(n.right));
    }

    // ── Düğüm ────────────────────────────────────────────────────
    private static class BSTNode {
        BittuneSong song;
        BSTNode left, right;
        BSTNode(BittuneSong song) { this.song = song; }
    }
}
