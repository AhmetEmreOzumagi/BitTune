import java.util.ArrayList;

// ╔══════════════════════════════════════════════════════════════════════╗
// ║            BittuneSorter — Sıralama Algoritmaları                   ║
// ╠══════════════════════════════════════════════════════════════════════╣
// ║                                                                      ║
// ║  TEK BİLEŞEN:                                                        ║
// ║                                                                      ║
// ║  BittuneSongSorter — Üç Farklı Sıralama Algoritması                  ║
// ║                                                                      ║
// ║  Her sütun farklı algoritmayla sıralanır (kasıtlı seçim):           ║
// ║                                                                      ║
// ║  ┌────────────────┬──────────────────┬──────────────┬─────────────┐ ║
// ║  │ Sütun          │ Algoritma        │ Big-O        │ Neden?      │ ║
// ║  ├────────────────┼──────────────────┼──────────────┼─────────────┤ ║
// ║  │ Başlık         │ Merge Sort       │ O(n log n)   │ Büyük       │ ║
// ║  │                │                  │              │ listede     │ ║
// ║  │                │                  │              │ en hızlı    │ ║
// ║  ├────────────────┼──────────────────┼──────────────┼─────────────┤ ║
// ║  │ Sanatçı        │ Selection Sort   │ O(n²)        │ Karşılaştır │ ║
// ║  │ Albüm          │                  │              │ maliyeti az │ ║
// ║  │ Beğeni         │                  │              │             │ ║
// ║  ├────────────────┼──────────────────┼──────────────┼─────────────┤ ║
// ║  │ Süre           │ Insertion Sort   │ O(n²)        │ Neredeyse   │ ║
// ║  │                │                  │              │ sıralıda    │ ║
// ║  │                │                  │              │ O(n)'e yakın│ ║
// ║  └────────────────┴──────────────────┴──────────────┴─────────────┘ ║
// ║                                                                      ║
// ║  ALGORİTMA DETAYLARI:                                                ║
// ║                                                                      ║
// ║  Merge Sort (Birleşim Sıralaması):                                   ║
// ║    Böl → Sırala → Birleştir                                          ║
// ║    Böler: Liste ikiye bölünür, her yarı özyinelemeli sıralanır       ║
// ║    Birleştirir: İki sıralı yarı O(n)'de birleştirilir                ║
// ║    Kararlı (stable): Eşit elemanların sırası korunur                 ║
// ║    Her zaman O(n log n) — en kötü durumda da bozulmaz               ║
// ║                                                                      ║
// ║  Selection Sort (Seçim Sıralaması):                                  ║
// ║    Her turda kalan listede minimum/maksimum bulunur                   ║
// ║    Bulunan eleman listede i. konuma alınır                           ║
// ║    O(n²) karşılaştırma, O(n) takas — takas az olduğunda tercih     ║
// ║                                                                      ║
// ║  Insertion Sort (Ekleme Sıralaması):                                 ║
// ║    Her eleman, sıralı kısıma doğru yerine kaydırılır                ║
// ║    Neredeyse sıralı listede O(n)'e yakın davranır                    ║
// ║    Bubble Sort'tan hızlı: takas değil kaydırma yapar                ║
// ╚══════════════════════════════════════════════════════════════════════╝


// =============================================================
// BittuneSongSorter — Şarkı Listesi Sıralama Algoritmaları
//
// Tüm metodlar statik — nesne oluşturmaya gerek yok.
// Tüm sıralamalar in-place (liste doğrudan değiştirilir).
//
// Bağlantı: BittuneCore.sortColumn() → sortBy() üzerinden
// =============================================================
class BittuneSongSorter {

    // ══════════════════════════════════════════════════════════════
    //  1. MERGE SORT — Birleşim Sıralaması — O(n log n)
    //     Başlık sütunu için kullanılır
    // ══════════════════════════════════════════════════════════════

    /**
     * Birleşim Sıralaması (Merge Sort) — O(n log n).
     *
     * Böl-ve-Fethet stratejisi:
     *   1. Listeyi ortadan ikiye böl
     *   2. Her yarıyı özyinelemeli olarak sırala
     *   3. İki sıralı yarıyı O(n)'de birleştir
     *
     * Avantajları:
     *   • Her zaman O(n log n) — en kötü durumda bozulmaz
     *   • Kararlı (stable) sıralama — eşit elemanların sırası korunur
     *   • Büyük listelerde Selection/Insertion Sort'tan belirgin hızlı
     *
     * Dezavantajı: O(n) ek bellek (sol ve sağ kopyalar)
     *
     * @param list      Sıralanacak şarkı listesi (in-place değiştirilir)
     * @param ascending true = A-Z (artan), false = Z-A (azalan)
     */
    static void mergeSortByTitle(ArrayList<BittuneSong> list, boolean ascending) {
        if (list.size() <= 1) return;   // tek eleman zaten sıralı

        // Böl: listeyi ikiye ayır
        int mid = list.size() / 2;
        ArrayList<BittuneSong> left  = new ArrayList<>(list.subList(0, mid));
        ArrayList<BittuneSong> right = new ArrayList<>(list.subList(mid, list.size()));

        // Fethet: her yarıyı özyinelemeli sırala
        mergeSortByTitle(left,  ascending);
        mergeSortByTitle(right, ascending);

        // Birleştir: iki sıralı yarıyı orijinal listeye yaz
        int i = 0, j = 0, k = 0;
        while (i < left.size() && j < right.size()) {
            int cmp = left.get(i).title.compareToIgnoreCase(right.get(j).title);
            boolean takeLeft = ascending ? cmp <= 0 : cmp >= 0;
            list.set(k++, takeLeft ? left.get(i++) : right.get(j++));
        }
        // Kalan elemanları yaz
        while (i < left.size())  list.set(k++, left.get(i++));
        while (j < right.size()) list.set(k++, right.get(j++));
    }

    // ══════════════════════════════════════════════════════════════
    //  2. SELECTION SORT — Seçim Sıralaması — O(n²)
    //     Sanatçı, Albüm ve Beğeni sütunları için kullanılır
    // ══════════════════════════════════════════════════════════════

    /**
     * Seçim Sıralaması (Selection Sort) — O(n²) karşılaştırma, O(n) takas.
     *
     * Her turda kalan listenin en küçük/büyük elemanını seçer
     * ve mevcut konumla yer değiştirir.
     *
     * Avantajı:
     *   • Takas sayısı minimum (O(n)) — takas maliyetli ise tercih edilir
     *   • Uygulaması basit ve anlaşılır
     *
     * Dezavantajı:
     *   • Her zaman O(n²) karşılaştırma — liste sıralı olsa bile
     *   • Kararsız (unstable) — eşit elemanların sırası bozulabilir
     *
     * @param list      Sıralanacak şarkı listesi (in-place)
     * @param field     "artist", "album" veya "title"
     * @param ascending true = A-Z, false = Z-A
     */
    static void selectionSortByText(ArrayList<BittuneSong> list, String field, boolean ascending) {
        for (int i = 0; i < list.size() - 1; i++) {
            int selected = i;   // bu turda seçilen (min/max) konum

            // Kalan listede min veya max'ı bul
            for (int j = i + 1; j < list.size(); j++) {
                int cmp = textValue(list.get(j), field)
                              .compareToIgnoreCase(textValue(list.get(selected), field));
                if ((ascending && cmp < 0) || (!ascending && cmp > 0)) selected = j;
            }

            // Seçilen elemanı mevcut konumla takas et
            if (selected != i) {
                BittuneSong temp = list.get(i);
                list.set(i, list.get(selected));
                list.set(selected, temp);
            }
        }
    }

    /**
     * Seçim Sıralaması — Beğeni (boolean) alanı için.
     * Beğenilen şarkılar başa veya sona alınır.
     *
     * @param list       Sıralanacak şarkı listesi (in-place)
     * @param likedFirst true = beğenilenler başta, false = sonda
     */
    static void selectionSortByLiked(ArrayList<BittuneSong> list, boolean likedFirst) {
        for (int i = 0; i < list.size() - 1; i++) {
            int selected = i;
            for (int j = i + 1; j < list.size(); j++) {
                boolean shouldSelect = likedFirst
                    ? list.get(j).liked && !list.get(selected).liked
                    : !list.get(j).liked && list.get(selected).liked;
                if (shouldSelect) selected = j;
            }
            if (selected != i) {
                BittuneSong temp = list.get(i);
                list.set(i, list.get(selected));
                list.set(selected, temp);
            }
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  3. INSERTION SORT — Ekleme Sıralaması — O(n²)
    //     Süre sütunu için kullanılır
    // ══════════════════════════════════════════════════════════════

    /**
     * Ekleme Sıralaması (Insertion Sort) — O(n²) en kötü, O(n) neredeyse sıralıda.
     *
     * Her elemanı sıralı kısıma doğru yerine kaydırır.
     * Kart sıralama algoritmasına benzer: soldaki sıralı, sağdaki sırasız.
     *
     * Avantajları:
     *   • Neredeyse sıralı listede O(n)'e çok yakın davranır
     *   • Bubble Sort'tan hızlı: takas yerine kaydırma yapar
     *   • Kararlı (stable) — eşit elemanların sırası korunur
     *   • Küçük listelerde Merge Sort overhead'inden kaçınılır
     *
     * Dezavantajı:
     *   • Tamamen ters sıralı listede O(n²) karşılaştırma ve kaydırma
     *
     * @param list      Sıralanacak şarkı listesi (in-place)
     * @param ascending true = kısadan uzuna, false = uzundan kısaya
     */
    static void insertionSortByDuration(ArrayList<BittuneSong> list, boolean ascending) {
        for (int i = 1; i < list.size(); i++) {
            BittuneSong key = list.get(i);   // mevcut eleman
            int j = i - 1;

            // key'den büyük/küçük elemanları sağa kaydır
            while (j >= 0) {
                boolean shift = ascending
                    ? list.get(j).durationSeconds > key.durationSeconds
                    : list.get(j).durationSeconds < key.durationSeconds;
                if (!shift) break;
                list.set(j + 1, list.get(j));   // bir sağa kaydır
                j--;
            }
            list.set(j + 1, key);   // key'i doğru konuma yerleştir
        }
    }

    // ── Yardımcı ─────────────────────────────────────────────────

    /**
     * Şarkının belirtilen alanından metin değerini döner.
     * selectionSortByText() tarafından kullanılır.
     */
    private static String textValue(BittuneSong song, String field) {
        return switch (field) {
            case "artist" -> song.artist;
            case "album"  -> song.album;
            default       -> song.title;
        };
    }
}
