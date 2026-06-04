import java.util.ArrayList;

// ╔══════════════════════════════════════════════════════════════════════╗
// ║          BittuneQueueStack — Kuyruk & Yığın Veri Yapıları           ║
// ╠══════════════════════════════════════════════════════════════════════╣
// ║                                                                      ║
// ║  İKİ BILEŞEN:                                                        ║
// ║                                                                      ║
// ║  1. BittuneSongQueue — Kuyruk (Queue / FIFO)                         ║
// ║     • FIFO: First In First Out (İlk Giren İlk Çıkar)                ║
// ║     • Tek yönlü bağlı liste — dizi değil → boyut sınırı yok        ║
// ║     • enqueue O(1) — tail (kuyruk sonu) pointer'ıyla ekler          ║
// ║     • dequeue O(1) — front (kuyruk başı) pointer'ından çıkarır      ║
// ║     • Kullanım: "Sıraya Ekle" → müzik çalma kuyruğu                 ║
// ║                                                                      ║
// ║  2. BittuneSongHistoryStack — Geçmiş Yığını (Stack / LIFO)          ║
// ║     • LIFO: Last In First Out (Son Giren İlk Çıkar)                 ║
// ║     • Tek yönlü bağlı liste — dizi değil → boyut sınırı yok        ║
// ║     • push O(1) — yığının tepesine ekler (head'e)                   ║
// ║     • pop  O(1) — yığının tepesinden çıkarır (head'den)             ║
// ║     • Kullanım: "← Önceki" tuşu → geri geçmişi                      ║
// ║                                                                      ║
// ║  NEDEN BAĞLI LİSTE?                                                  ║
// ║    • Dizi tabanlı olsaydı kapasiteyi önceden bilmek gerekirdi        ║
// ║    • Bağlı liste dinamik büyür, her işlem O(1) garantilidir         ║
// ║    • Ortaya erişim gerekmez → bağlı listenin ideal kullanım alanı   ║
// ║                                                                      ║
// ║  Big-O Özeti:                                                        ║
// ║  ┌────────────────────────┬────────┬───────────────────────────┐     ║
// ║  │ İşlem                  │ Big-O  │ Açıklama                  │     ║
// ║  ├────────────────────────┼────────┼───────────────────────────┤     ║
// ║  │ Queue enqueue          │ O(1)   │ tail pointer ile sona ekle│     ║
// ║  │ Queue dequeue          │ O(1)   │ front pointer'dan çıkar   │     ║
// ║  │ Queue clear            │ O(1)   │ pointer'ları sıfırla      │     ║
// ║  │ Stack push             │ O(1)   │ head'e ekle               │     ║
// ║  │ Stack pop              │ O(1)   │ head'den çıkar            │     ║
// ║  └────────────────────────┴────────┴───────────────────────────┘     ║
// ╚══════════════════════════════════════════════════════════════════════╝


// =============================================================
// BittuneSongQueue — Müzik Çalma Kuyruğu (FIFO)
//
// DSA: Queue (Kuyruk)
//   Prensip: FIFO — İlk eklenen şarkı ilk çalar
//   Yapı: Tek yönlü bağlı liste (front → rear)
//
// Çalışma mantığı:
//   enqueue → şarkıyı rear'ın sağına ekle, rear'ı ilerlet
//   dequeue → front'taki şarkıyı al, front'u ilerlet
//   Kuyruk boşsa: front == null → dequeue null döner
//
// Bağlantı: BittuneCore.enqueue() / dequeueOrNull() üzerinden GUI'ye
// =============================================================
class BittuneSongQueue {
    private Node front;   // kuyruğun başı — dequeue buradan
    private Node rear;    // kuyruğun sonu  — enqueue buraya

    /**
     * Şarkıyı kuyruğun SONUNA ekler — O(1).
     * rear pointer'ı yeni düğüme güncellenir.
     * Kuyruk boşsa front ve rear aynı düğümü gösterir.
     */
    void enqueue(BittuneSong song) {
        Node node = new Node(song);
        if (rear == null) {
            front = rear = node;      // boş kuyruk — ilk eleman
        } else {
            rear.next = node;         // mevcut son düğümün next'ini bağla
            rear = node;              // rear'ı ilerlet
        }
    }

    /**
     * Kuyruğun BAŞINDAN şarkı çıkarır — O(1).
     * front pointer'ı bir sonraki düğüme ilerler.
     * Kuyruk boşsa null döner — GUI bu durumu Queue'dan değil context'ten çalar.
     */
    BittuneSong dequeue() {
        if (front == null) return null;
        BittuneSong song = front.song;
        front = front.next;
        if (front == null) rear = null;   // kuyruk boşaldı
        return song;
    }

    /**
     * Kuyruğu tamamen sıfırlar — O(1).
     * Yalnızca pointer'lar null yapılır; GC düğümleri temizler.
     */
    void clear() {
        front = rear = null;
    }

    /**
     * Belirli bir şarkıyı kuyruktan kaldırır — O(n).
     * İlk eşleşen düğümü bulup zincirden çıkarır.
     * @return true = bulundu ve silindi, false = bulunamadı
     */
    boolean remove(BittuneSong song) {
        if (front == null) return false;
        if (front.song == song) {
            front = front.next;
            if (front == null) rear = null;
            return true;
        }
        Node prev = front, curr = front.next;
        while (curr != null) {
            if (curr.song == song) {
                prev.next = curr.next;
                if (curr == rear) rear = prev;
                return true;
            }
            prev = curr; curr = curr.next;
        }
        return false;
    }

    /**
     * Kuyruk içeriğini ArrayList olarak döner — O(n).
     * UI listesi ve "Sıra" görünümü için kullanılır.
     * Orijinal kuyruk değiştirilmez (salt okunur görünüm).
     */
    ArrayList<BittuneSong> toArrayList() {
        ArrayList<BittuneSong> values = new ArrayList<>();
        Node temp = front;
        while (temp != null) {
            values.add(temp.song);
            temp = temp.next;
        }
        return values;
    }

    // ── Düğüm ────────────────────────────────────────────────────
    private static class Node {
        final BittuneSong song;
        Node next;
        Node(BittuneSong song) { this.song = song; }
    }
}


// =============================================================
// BittuneSongHistoryStack — Çalma Geçmişi Yığını (LIFO)
//
// DSA: Stack (Yığın)
//   Prensip: LIFO — Son eklenen şarkı ilk geri alınır
//   Yapı: Tek yönlü bağlı liste (top → ... → null)
//
// Çalışma mantığı:
//   push → yeni düğümü top'a ekle, eski top'u sağına bağla
//   pop  → top'taki şarkıyı al, top'u bir sonrakine ilerlet
//   Yığın boşsa: top == null → pop null döner
//
// Kullanım senaryosu:
//   Yeni şarkıya geçilirken eski şarkı push edilir.
//   "← Önceki" tuşuna basılınca pop ile geri dönülür.
//
// Bağlantı: BittuneCore.pushHistory() / popHistory() üzerinden
// =============================================================
class BittuneSongHistoryStack {
    private Node top;   // yığının tepesi — push ve pop buradan

    /**
     * Şarkıyı yığının TEPESİNE iter — O(1).
     * Yeni düğüm, eski top'u sağına alarak başa geçer.
     */
    void push(BittuneSong song) {
        Node node = new Node(song);
        node.next = top;   // eski teperi yeni düğümün altına al
        top = node;        // top'u güncelle
    }

    /**
     * Yığının TEPESİNDEN şarkı çıkarır — O(1).
     * top bir sonraki düğüme ilerler.
     * Yığın boşsa null döner — GUI bu durumda bağlamda geriye gider.
     */
    BittuneSong pop() {
        if (top == null) return null;
        BittuneSong song = top.song;
        top = top.next;   // top'u bir alta indir
        return song;
    }

    /** Yığın boş mu? */
    boolean isEmpty() { return top == null; }

    // ── Düğüm ────────────────────────────────────────────────────
    private static class Node {
        final BittuneSong song;
        Node next;
        Node(BittuneSong song) { this.song = song; }
    }
}
