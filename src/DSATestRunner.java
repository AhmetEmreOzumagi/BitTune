import java.awt.Color;
import java.util.ArrayList;

// ╔══════════════════════════════════════════════════════════════════════╗
// ║  DSATestRunner — Bağımsız DSA Test Sınıfı (JUnit gerektirmez)       ║
// ║  Çalıştırma: java DSATestRunner                                      ║
// ╚══════════════════════════════════════════════════════════════════════╝
public class DSATestRunner {

    static int pass = 0;
    static int fail = 0;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║         Bittune DSA Test Runner — Başlıyor           ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();

        testQueueFIFO();
        testStackLIFO();
        testPlaylistLinkedList();
        testHashMap();
        testBST();
        testMergeSort();
        testTrieSuggest();
        testMaxHeap();

        System.out.println();
        System.out.println("══════════════════════════════════════════════════════");
        System.out.println("  SONUÇ: " + pass + "/" + (pass + fail) + " test geçti");
        System.out.println("══════════════════════════════════════════════════════");

        if (fail > 0) System.exit(1);
    }

    // ─────────────────────────────────────────────────────────────────
    //  1. Queue FIFO Testi — BittuneSongQueue
    // ─────────────────────────────────────────────────────────────────
    static void testQueueFIFO() {
        System.out.println("── 1. Queue FIFO Testi ──────────────────────────────");
        try {
            BittuneSongQueue queue = new BittuneSongQueue();

            BittuneSong s1 = song("Şarkı A", "Sanatçı 1");
            BittuneSong s2 = song("Şarkı B", "Sanatçı 2");
            BittuneSong s3 = song("Şarkı C", "Sanatçı 3");

            queue.enqueue(s1);
            queue.enqueue(s2);
            queue.enqueue(s3);

            BittuneSong d1 = queue.dequeue();
            BittuneSong d2 = queue.dequeue();
            BittuneSong d3 = queue.dequeue();
            BittuneSong d4 = queue.dequeue(); // boş kuyruk

            assertEq("Queue FIFO - 1. dequeue Şarkı A", "Şarkı A", d1.title);
            assertEq("Queue FIFO - 2. dequeue Şarkı B", "Şarkı B", d2.title);
            assertEq("Queue FIFO - 3. dequeue Şarkı C", "Şarkı C", d3.title);
            assertNull("Queue FIFO - boş kuyrukta null döner", d4);
        } catch (Exception e) {
            failTest("Queue FIFO - beklenmedik hata: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  2. Stack LIFO Testi — BittuneSongHistoryStack
    // ─────────────────────────────────────────────────────────────────
    static void testStackLIFO() {
        System.out.println("── 2. Stack LIFO Testi ──────────────────────────────");
        try {
            BittuneSongHistoryStack stack = new BittuneSongHistoryStack();

            BittuneSong s1 = song("İlk Şarkı",    "Sanatçı 1");
            BittuneSong s2 = song("İkinci Şarkı", "Sanatçı 2");
            BittuneSong s3 = song("Son Şarkı",    "Sanatçı 3");

            stack.push(s1);
            stack.push(s2);
            stack.push(s3);

            BittuneSong p1 = stack.pop();
            BittuneSong p2 = stack.pop();
            BittuneSong p3 = stack.pop();
            BittuneSong p4 = stack.pop(); // boş yığın

            assertEq("Stack LIFO - 1. pop Son Şarkı",    "Son Şarkı",    p1.title);
            assertEq("Stack LIFO - 2. pop İkinci Şarkı", "İkinci Şarkı", p2.title);
            assertEq("Stack LIFO - 3. pop İlk Şarkı",   "İlk Şarkı",    p3.title);
            assertNull("Stack LIFO - boş yığında null döner", p4);
        } catch (Exception e) {
            failTest("Stack LIFO - beklenmedik hata: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  3. Playlist LinkedList Testi — BittunePlaylistLinkedList
    // ─────────────────────────────────────────────────────────────────
    static void testPlaylistLinkedList() {
        System.out.println("── 3. Playlist LinkedList Testi ─────────────────────");
        try {
            BittunePlaylistLinkedList pl = new BittunePlaylistLinkedList();

            boolean added     = pl.add("Favorilerim");
            boolean duplicate = pl.add("Favorilerim"); // aynı isim

            assertTrue("LinkedList - playlist eklendi",              added);
            assertFalse("LinkedList - duplicate playlist eklenmedi", duplicate);

            BittuneSong s1 = song("Mavi Gökyüzü", "Grup A");
            BittuneSong s2 = song("Kırmızı Akşam", "Grup B");
            BittuneSong s3 = song("Yeşil Vadi",   "Grup C");

            pl.addSong("Favorilerim", s1);
            pl.addSong("Favorilerim", s2);
            pl.addSong("Favorilerim", s3);
            pl.addSong("Favorilerim", s1); // duplicate şarkı — eklenmemeli

            ArrayList<BittuneSong> songs = pl.songsOf("Favorilerim");

            assertEq("LinkedList - playlist boyutu 3", 3, songs.size());
            assertEq("LinkedList - 1. şarkı Mavi Gökyüzü",  "Mavi Gökyüzü",  songs.get(0).title);
            assertEq("LinkedList - 2. şarkı Kırmızı Akşam", "Kırmızı Akşam", songs.get(1).title);
            assertEq("LinkedList - 3. şarkı Yeşil Vadi",    "Yeşil Vadi",    songs.get(2).title);

            // Var olmayan playlist boş döner
            ArrayList<BittuneSong> empty = pl.songsOf("Yok Playlist");
            assertEq("LinkedList - olmayan playlist boş döner", 0, empty.size());
        } catch (Exception e) {
            failTest("LinkedList - beklenmedik hata: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  4. HashMap Testi — BittuneSongHashMap
    // ─────────────────────────────────────────────────────────────────
    static void testHashMap() {
        System.out.println("── 4. HashMap Testi ─────────────────────────────────");
        try {
            BittuneSongHashMap map = new BittuneSongHashMap();

            BittuneSong s1 = song("Bohemian Rhapsody", "Queen");
            BittuneSong s2 = song("Stairway to Heaven",  "Led Zeppelin");
            BittuneSong s3 = song("Hotel California",    "Eagles");

            map.put(s1);
            map.put(s2);
            map.put(s3);

            BittuneSong found1 = map.get("Bohemian Rhapsody");
            BittuneSong found2 = map.get("Stairway to Heaven");
            BittuneSong found3 = map.get("hotel california"); // küçük harf
            BittuneSong found4 = map.get("Yok Şarkı");

            assertEq("HashMap - Bohemian Rhapsody bulundu", "Bohemian Rhapsody", found1.title);
            assertEq("HashMap - Stairway to Heaven bulundu","Stairway to Heaven", found2.title);
            assertNotNull("HashMap - küçük harf arama çalışıyor", found3);
            assertNull("HashMap - olmayan şarkı null döner", found4);
            assertEq("HashMap - size 3", 3, map.size());
        } catch (Exception e) {
            failTest("HashMap - beklenmedik hata: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  5. BST Testi — BittuneSongBST
    // ─────────────────────────────────────────────────────────────────
    static void testBST() {
        System.out.println("── 5. BST InOrder Testi ─────────────────────────────");
        try {
            BittuneSongBST bst = new BittuneSongBST();

            // Alfabetik olmayan sırada ekle
            bst.insert(song("Mango",  "A"));
            bst.insert(song("Elma",   "A"));
            bst.insert(song("Portakal","A"));
            bst.insert(song("Armut",  "A"));
            bst.insert(song("Kiraz",  "A"));

            ArrayList<BittuneSong> ordered = bst.inorderList();

            assertEq("BST - inorder boyutu 5", 5, ordered.size());
            assertEq("BST - inorder[0] Armut",    "Armut",    ordered.get(0).title);
            assertEq("BST - inorder[1] Elma",     "Elma",     ordered.get(1).title);
            assertEq("BST - inorder[2] Kiraz",    "Kiraz",    ordered.get(2).title);
            assertEq("BST - inorder[3] Mango",    "Mango",    ordered.get(3).title);
            assertEq("BST - inorder[4] Portakal", "Portakal", ordered.get(4).title);

            // Arama testi
            BittuneSong found = bst.search("Kiraz");
            assertNotNull("BST - Kiraz arama bulundu", found);
            BittuneSong notFound = bst.search("Çilek");
            assertNull("BST - olmayan şarkı null döner", notFound);
        } catch (Exception e) {
            failTest("BST - beklenmedik hata: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  6. MergeSort Testi — BittuneSongSorter
    // ─────────────────────────────────────────────────────────────────
    static void testMergeSort() {
        System.out.println("── 6. MergeSort Testi ───────────────────────────────");
        try {
            ArrayList<BittuneSong> list = new ArrayList<>();
            list.add(song("Zambak",  "A"));
            list.add(song("Gül",     "A"));
            list.add(song("Lale",    "A"));
            list.add(song("Menekşe", "A"));
            list.add(song("Papatya", "A"));

            // Artan sıralama (A-Z)
            BittuneSongSorter.mergeSortByTitle(list, true);

            assertEq("MergeSort A-Z - [0] Gül",     "Gül",     list.get(0).title);
            assertEq("MergeSort A-Z - [1] Lale",    "Lale",    list.get(1).title);
            assertEq("MergeSort A-Z - [2] Menekşe", "Menekşe", list.get(2).title);
            assertEq("MergeSort A-Z - [3] Papatya", "Papatya", list.get(3).title);
            assertEq("MergeSort A-Z - [4] Zambak",  "Zambak",  list.get(4).title);

            // Azalan sıralama (Z-A)
            BittuneSongSorter.mergeSortByTitle(list, false);
            assertEq("MergeSort Z-A - [0] Zambak",  "Zambak",  list.get(0).title);
            assertEq("MergeSort Z-A - [4] Gül",     "Gül",     list.get(4).title);
        } catch (Exception e) {
            failTest("MergeSort - beklenmedik hata: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  7. Trie Prefix Search Testi — BittuneSearchTrie
    // ─────────────────────────────────────────────────────────────────
    static void testTrieSuggest() {
        System.out.println("── 7. Trie Prefix Search Testi ──────────────────────");
        try {
            BittuneSearchTrie trie = new BittuneSearchTrie();

            BittuneSong baroque = song("Baroque Dreams",    "Bach");
            BittuneSong batman  = song("Batman Themes",     "Zimmer");
            BittuneSong jazz    = song("Jazz Nights",       "Miles");
            BittuneSong bass    = song("Bass Guitar Solo",  "Flea");

            trie.insert(baroque);
            trie.insert(batman);
            trie.insert(jazz);
            trie.insert(bass);

            // "Ba" ön eki → Baroque, Batman, Bass eşleşmeli
            ArrayList<BittuneSong> results = trie.suggest("Ba");

            assertTrue("Trie - 'Ba' en az 3 sonuç döner", results.size() >= 3);
            assertTrue("Trie - Baroque Dreams sonuçlarda var",
                    results.stream().anyMatch(s -> s.title.equals("Baroque Dreams")));
            assertTrue("Trie - Batman Themes sonuçlarda var",
                    results.stream().anyMatch(s -> s.title.equals("Batman Themes")));
            assertTrue("Trie - Bass Guitar Solo sonuçlarda var",
                    results.stream().anyMatch(s -> s.title.equals("Bass Guitar Solo")));

            // Eşleşmeyen ön ek
            ArrayList<BittuneSong> noMatch = trie.suggest("XYZ");
            assertEq("Trie - 'XYZ' eşleşmez, boş döner", 0, noMatch.size());

            // Büyük/küçük harf duyarsızlığı
            ArrayList<BittuneSong> upper = trie.suggest("BA");
            assertTrue("Trie - büyük harf 'BA' da çalışır", upper.size() >= 3);
        } catch (Exception e) {
            failTest("Trie - beklenmedik hata: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  8. MaxHeap Top-N Testi — BittuneMaxHeap
    // ─────────────────────────────────────────────────────────────────
    static void testMaxHeap() {
        System.out.println("── 8. MaxHeap Top-N Testi ───────────────────────────");
        try {
            BittuneMaxHeap heap = new BittuneMaxHeap(10);

            // priority = playCount * 10 + likes
            BittuneSong low    = songWithStats("Az Çalınan",   1, 0);   // priority = 10
            BittuneSong mid    = songWithStats("Orta Çalınan", 5, 3);   // priority = 53
            BittuneSong high   = songWithStats("Çok Çalınan",  20, 10); // priority = 210
            BittuneSong medium = songWithStats("Biraz Çalınan",3, 5);   // priority = 35

            heap.insert(low);
            heap.insert(mid);
            heap.insert(high);
            heap.insert(medium);

            BittuneSong max1 = heap.extractMax();
            BittuneSong max2 = heap.extractMax();
            BittuneSong max3 = heap.extractMax();

            assertEq("MaxHeap - 1. extractMax en çok çalınan", "Çok Çalınan",   max1.title);
            assertEq("MaxHeap - 2. extractMax orta çalınan",   "Orta Çalınan",  max2.title);
            assertEq("MaxHeap - 3. extractMax biraz çalınan",  "Biraz Çalınan", max3.title);

            // topN testi — orijinal heap bozulmadan kopya üzerinde çalışır
            BittuneMaxHeap heap2 = new BittuneMaxHeap(10);
            BittuneSong a = songWithStats("A Şarkısı", 10, 0); // priority 100
            BittuneSong b = songWithStats("B Şarkısı", 8, 0);  // priority 80
            BittuneSong c = songWithStats("C Şarkısı", 5, 0);  // priority 50
            heap2.insert(a);
            heap2.insert(b);
            heap2.insert(c);

            ArrayList<BittuneSong> top2 = heap2.topN(2);
            assertEq("MaxHeap - topN(2) boyutu 2", 2, top2.size());
            assertEq("MaxHeap - topN[0] A Şarkısı", "A Şarkısı", top2.get(0).title);
            assertEq("MaxHeap - topN[1] B Şarkısı", "B Şarkısı", top2.get(1).title);
            // Orijinal heap hâlâ 3 elemanlı olmalı
            assertEq("MaxHeap - topN sonrası orijinal heap bozulmadı", 3, heap2.size());
        } catch (Exception e) {
            failTest("MaxHeap - beklenmedik hata: " + e.getMessage());
        }
    }

    // ═════════════════════════════════════════════════════════════════
    //  YARDIMCI — BittuneSong fabrika metodları
    // ═════════════════════════════════════════════════════════════════

    static BittuneSong song(String title, String artist) {
        return new BittuneSong(title, artist, "Test Albüm", 180,
                0, Color.BLUE, Color.RED, false);
    }

    static BittuneSong songWithStats(String title, int playCount, int likes) {
        BittuneSong s = new BittuneSong(title, "Test Sanatçı", "Test Albüm", 180,
                likes, Color.BLUE, Color.RED, false);
        s.playCount = playCount;
        s.likes     = likes;
        return s;
    }

    // ═════════════════════════════════════════════════════════════════
    //  YARDIMCI — Assertion metodları
    // ═════════════════════════════════════════════════════════════════

    static void assertEq(String label, Object expected, Object actual) {
        if (expected == null ? actual == null : expected.equals(actual)) {
            passTest(label);
        } else {
            failTest(label + " — beklenen: " + expected + ", alınan: " + actual);
        }
    }

    static void assertTrue(String label, boolean condition) {
        if (condition) passTest(label);
        else failTest(label + " — koşul sağlanmadı (false)");
    }

    static void assertFalse(String label, boolean condition) {
        if (!condition) passTest(label);
        else failTest(label + " — koşul sağlandı (true bekleniyordu false)");
    }

    static void assertNull(String label, Object obj) {
        if (obj == null) passTest(label);
        else failTest(label + " — null beklendi, alınan: " + obj);
    }

    static void assertNotNull(String label, Object obj) {
        if (obj != null) passTest(label);
        else failTest(label + " — null döndü, bir değer beklendi");
    }

    static void passTest(String label) {
        System.out.println("  [PASS] " + label);
        pass++;
    }

    static void failTest(String label) {
        System.out.println("  [FAIL] " + label);
        fail++;
    }
}
