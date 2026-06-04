import java.util.ArrayList;

// ╔══════════════════════════════════════════════════════════════════════╗
// ║            BittuneHeap — MaxHeap (Öncelik Kuyruğu)                  ║
// ╠══════════════════════════════════════════════════════════════════════╣
// ║                                                                      ║
// ║  TEK BİLEŞEN:                                                        ║
// ║                                                                      ║
// ║  BittuneMaxHeap — Maksimum Heap (Max-Heap / Priority Queue)          ║
// ║    • Dizi tabanlı tam ikili ağaç (complete binary tree)              ║
// ║    • Heap özelliği: her ebeveyn ≥ her çocuğu (öncelik bazlı)        ║
// ║    • Öncelik formülü: priority = playCount × 10 + likes             ║
// ║    • insert O(log n)     → ekle + sift-up                           ║
// ║    • extractMax O(log n) → kökü al + sift-down                      ║
// ║    • topN(k) → ilk k elemanı çıkar, orijinal heap korunur           ║
// ║                                                                      ║
// ║  NEDEN HEAP?                                                         ║
// ║    "En çok çalınan N şarkı" sorgusu için idealdir:                   ║
// ║    • N kere extractMax → O(N log n) — N küçük olduğunda verimli    ║
// ║    • ArrayList üzerinde her seferinde sort yapmaktan çok daha hızlı ║
// ║    • Dinamik: yeni şarkı oynatılınca anında öncelik değişir         ║
// ║                                                                      ║
// ║  DİZİ TEMSILI:                                                       ║
// ║    i. elemanın sol çocuğu  : 2i + 1                                 ║
// ║    i. elemanın sağ çocuğu  : 2i + 2                                 ║
// ║    i. elemanın ebeveyni    : (i - 1) / 2                            ║
// ║                                                                      ║
// ║  Big-O Özeti:                                                        ║
// ║  ┌─────────────────────┬──────────────┬────────────────────────┐     ║
// ║  │ İşlem               │ Big-O        │ Açıklama               │     ║
// ║  ├─────────────────────┼──────────────┼────────────────────────┤     ║
// ║  │ insert              │ O(log n)     │ ekle + sift-up         │     ║
// ║  │ extractMax          │ O(log n)     │ kök al + sift-down     │     ║
// ║  │ topN(k)             │ O(n log k)   │ k kez extractMax       │     ║
// ║  │ buildHeap (n ekle)  │ O(n log n)   │ n kez insert           │     ║
// ║  └─────────────────────┴──────────────┴────────────────────────┘     ║
// ╚══════════════════════════════════════════════════════════════════════╝


// =============================================================
// BittuneMaxHeap — Maksimum Heap (Öncelik Kuyruğu)
//
// DSA: Max-Heap
//   Yapı: Dizi tabanlı tam ikili ağaç
//   Özellik: kök her zaman en yüksek öncelikli eleman
//   Öncelik: priority(s) = s.playCount × 10 + s.likes
//
// Algoritma Detayları:
//   sift-up:   Ekledikten sonra ebeveynlerle karşılaştır;
//              öncelik büyükse ebeveynle yer değiştir — O(log n)
//   sift-down: Kök çıkardıktan sonra çocuklarla karşılaştır;
//              büyük çocukla yer değiştir — O(log n)
//
// topN(): Orijinal heap kopyalanır, kopya üzerinde extractMax çağrılır.
//         Böylece ana heap bozulmaz.
//
// Bağlantı: BittuneCore.topN() üzerinden GUI'ye
// =============================================================
class BittuneMaxHeap {
    private BittuneSong[] heap;
    private int size;

    /**
     * Belirtilen kapasiteyle heap oluşturur.
     * Dinamik büyüme desteklenir (grow() ile 2× genişleme).
     */
    BittuneMaxHeap(int capacity) {
        heap = new BittuneSong[Math.max(capacity, 1)];
    }

    // ── Ekleme ───────────────────────────────────────────────────

    /**
     * Şarkıyı heap'e ekler — O(log n).
     *
     * Adımlar:
     *   1. Diziyi doluysa büyüt (grow)
     *   2. Yeni elemanı dizinin sonuna ekle
     *   3. sift-up: ebeveyni ile karşılaştır, öncelik büyükse yer değiştir
     *      Bu işlem kök'e kadar devam edebilir → O(log n)
     */
    void insert(BittuneSong song) {
        if (size >= heap.length) grow();
        heap[size] = song;
        siftUp(size++);
    }

    // ── Çıkarma ──────────────────────────────────────────────────

    /**
     * En yüksek öncelikli (en çok çalınan) şarkıyı çıkarır — O(log n).
     *
     * Adımlar:
     *   1. Kök (en yüksek öncelik) alınır
     *   2. Son eleman köke taşınır, size azaltılır
     *   3. sift-down: çocuklarıyla karşılaştır, büyük çocukla yer değiştir
     *      Bu işlem yaprak'a kadar devam edebilir → O(log n)
     */
    BittuneSong extractMax() {
        if (size == 0) return null;
        BittuneSong max = heap[0];       // en yüksek öncelikli (kök)
        heap[0] = heap[--size];          // son elemanı köke taşı
        heap[size] = null;               // bellek sızıntısını önle
        if (size > 0) siftDown(0);       // heap özelliğini yeniden sağla
        return max;
    }

    // ── Top-N Sorgusu ────────────────────────────────────────────

    /**
     * En yüksek öncelikli n şarkıyı döner — O(n log k).
     *
     * Orijinal heap korunur: bir kopya oluşturulur, kopya üzerinde çalışılır.
     * Tüm öncelikler 0 ise (hiç şarkı çalınmamışsa) boş liste döner.
     *
     * @param n Kaç şarkı döndürülsün
     * @return Öncelik sırasına göre en iyi n şarkı
     */
    ArrayList<BittuneSong> topN(int n) {
        // Orijinal heap'in kopyasını oluştur
        BittuneMaxHeap copy = new BittuneMaxHeap(size);
        for (int i = 0; i < size; i++) copy.insert(heap[i]);   // O(n log n)

        ArrayList<BittuneSong> result = new ArrayList<>();
        for (int i = 0; i < n && !copy.isEmpty(); i++) {
            BittuneSong top = copy.extractMax();                // O(log n)
            if (priority(top) > 0) result.add(top);            // 0 öncelikli → atla
        }
        return result;
    }

    boolean isEmpty() { return size == 0; }
    int     size()    { return size; }

    // ── Öncelik Fonksiyonu ───────────────────────────────────────

    /**
     * Şarkının önceliğini hesaplar.
     * playCount daha ağır çünkü çalınma sayısı beğeniden daha önemli.
     * Formül: priority = playCount × 10 + likes
     */
    private int priority(BittuneSong s) {
        return s.playCount * 10 + s.likes;
    }

    // ── Sift İşlemleri ───────────────────────────────────────────

    /**
     * Sift-Up: i indeksindeki elemanı yukarı taşır.
     * Ebeveyn önceliği daha küçükse yer değiştir, tekrarla.
     * Karmaşıklık: O(log n) — ağaç yüksekliği kadar adım
     */
    private void siftUp(int i) {
        while (i > 0) {
            int p = (i - 1) / 2;   // ebeveyn indeksi
            if (priority(heap[p]) >= priority(heap[i])) break;
            swap(i, p);
            i = p;
        }
    }

    /**
     * Sift-Down: i indeksindeki elemanı aşağı taşır.
     * Sol ve sağ çocukların en büyüğüyle karşılaştır;
     * çocuk daha büyükse yer değiştir, tekrarla.
     * Karmaşıklık: O(log n) — ağaç yüksekliği kadar adım
     */
    private void siftDown(int i) {
        while (true) {
            int max = i;
            int l = 2 * i + 1;   // sol çocuk
            int r = 2 * i + 2;   // sağ çocuk
            if (l < size && priority(heap[l]) > priority(heap[max])) max = l;
            if (r < size && priority(heap[r]) > priority(heap[max])) max = r;
            if (max == i) break;   // heap özelliği sağlandı
            swap(i, max);
            i = max;
        }
    }

    // ── Yardımcılar ──────────────────────────────────────────────

    private void swap(int a, int b) {
        BittuneSong t = heap[a]; heap[a] = heap[b]; heap[b] = t;
    }

    /** Kapasite dolunca diziyi iki katına büyütür. */
    private void grow() {
        BittuneSong[] bigger = new BittuneSong[heap.length * 2];
        System.arraycopy(heap, 0, bigger, 0, size);
        heap = bigger;
    }
}
