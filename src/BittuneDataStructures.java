import java.util.ArrayList;

// ╔══════════════════════════════════════════════════════════════════════╗
// ║        BittuneDataStructures — Playlist Bağlı Liste                  ║
// ╠══════════════════════════════════════════════════════════════════════╣
// ║                                                                      ║
// ║  Bu dosya: BittunePlaylistLinkedList                                  ║
// ║                                                                      ║
// ║  Diğer DSA yapıları kendi dosyalarında:                              ║
// ║    BittuneSearchEngine.java  → Trie, Linear Search, Binary Search    ║
// ║    BittuneQueueStack.java    → Queue (FIFO), Stack (LIFO)            ║
// ║    BittuneTreeIndex.java     → HashMap, BST                          ║
// ║    BittuneHeap.java          → MaxHeap                               ║
// ║    BittuneSorter.java        → MergeSort, InsertionSort, SelectionSort║
// ║                                                                      ║
// ║  BittunePlaylistLinkedList:                                           ║
// ║    • Kullanıcının oluşturduğu playlist'leri tutan bağlı liste        ║
// ║    • Her playlist kendi içinde şarkıları bağlı liste ile tutar       ║
// ║    • add()     O(n) — isim çakışması kontrolü                        ║
// ║    • addSong() O(n) — playlist bulma + duplicate kontrolü            ║
// ║    • songsOf() O(n) — playlist şarkılarını döner                    ║
// ╚══════════════════════════════════════════════════════════════════════╝


// =============================================================
// BittunePlaylistLinkedList — Playlist Yönetimi
//
// DSA: Tek yönlü bağlı liste (playlist listesi için)
//      Her playlist içinde de tek yönlü bağlı liste (şarkılar için)
//
// Yapı:
//   head → [Playlist1] → [Playlist2] → [Playlist3] → null
//   Her playlist kendi içinde:
//   head → [Song1] → [Song2] → [Song3] → null
//
// Bağlantı: BittuneCore.showPlaylist(), BittuneStorage (kaydetme/yükleme)
// =============================================================
class BittunePlaylistLinkedList {
    private Node head;
    private Node tail;

    /**
     * Yeni playlist ekler — O(n) (isim çakışması kontrolü için).
     * Aynı isimde playlist varsa false döner (duplicate yok).
     * @return true = eklendi, false = zaten var
     */
    boolean add(String name) {
        if (find(name) != null) return false;
        Node node = new Node(new UserPlaylist(name));
        if (head == null) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        return true;
    }

    /**
     * Belirtilen playlist'e şarkı ekler — O(n).
     * Playlist bulunamazsa ya da şarkı zaten ekli ise işlem yapılmaz.
     */
    void addSong(String playlistName, BittuneSong song) {
        Node playlist = find(playlistName);
        if (playlist != null) {
            playlist.playlist.addSong(song);
        }
    }

    /**
     * Belirtilen playlist'ten şarkıyı siler — O(n).
     * DSA: İç bağlı listeden düğüm çıkarma (prev/curr ikilisi).
     * @return true = bulunup silindi, false = playlist/şarkı bulunamadı
     */
    boolean removeSong(String playlistName, BittuneSong song) {
        Node playlist = find(playlistName);
        if (playlist == null) return false;
        return playlist.playlist.removeSong(song);
    }

    /** Tüm playlist isimlerini ArrayList olarak döner — O(n). */
    ArrayList<String> toArrayList() {
        ArrayList<String> values = new ArrayList<>();
        Node temp = head;
        while (temp != null) {
            values.add(temp.playlist.name);
            temp = temp.next;
        }
        return values;
    }

    /**
     * Belirtilen playlist'in şarkılarını döner — O(n).
     * Playlist bulunamazsa boş liste döner.
     */
    ArrayList<BittuneSong> songsOf(String playlistName) {
        Node playlist = find(playlistName);
        return playlist == null ? new ArrayList<>() : playlist.playlist.songs();
    }

    /**
     * Belirtilen playlist'i listeden siler — O(n).
     * DSA: Bağlı listeden düğüm çıkarma — önceki düğümün next'i güncellenir.
     * @return true = bulunup silindi, false = bulunamadı
     */
    boolean remove(String name) {
        if (head == null) return false;
        if (head.playlist.name.equalsIgnoreCase(name)) {
            head = head.next;
            if (head == null) tail = null;
            return true;
        }
        Node prev = head, curr = head.next;
        while (curr != null) {
            if (curr.playlist.name.equalsIgnoreCase(name)) {
                prev.next = curr.next;
                if (curr == tail) tail = prev;
                return true;
            }
            prev = curr;
            curr = curr.next;
        }
        return false;
    }

    /** İsme göre playlist düğümü arar — O(n). */
    private Node find(String name) {
        Node temp = head;
        while (temp != null) {
            if (temp.playlist.name.equalsIgnoreCase(name)) return temp;
            temp = temp.next;
        }
        return null;
    }

    // ── Dış Bağlı Liste Düğümü (Playlist listesi) ────────────────
    private static class Node {
        final UserPlaylist playlist;
        Node next;
        Node(UserPlaylist playlist) { this.playlist = playlist; }
    }

    // ── Playlist — İç Bağlı Liste ile Şarkı Tutma ────────────────
    private static class UserPlaylist {
        final String name;
        SongNode head;
        SongNode tail;

        UserPlaylist(String name) { this.name = name; }

        /** Şarkıyı playlist'e ekler — duplicate kontrolü ile. */
        void addSong(BittuneSong song) {
            if (contains(song)) return;
            SongNode node = new SongNode(song);
            if (head == null) {
                head = tail = node;
            } else {
                tail.next = node;
                tail = node;
            }
        }

        /** Şarkı zaten bu playlist'te mi? — O(n). */
        boolean contains(BittuneSong song) {
            SongNode temp = head;
            while (temp != null) {
                if (temp.song == song) return true;
                temp = temp.next;
            }
            return false;
        }

        /**
         * Şarkıyı playlist'ten siler — O(n).
         * DSA: Bağlı listeden düğüm çıkarma — prev/curr ikilisi.
         */
        boolean removeSong(BittuneSong song) {
            if (head == null) return false;
            if (head.song == song) {
                head = head.next;
                if (head == null) tail = null;
                return true;
            }
            SongNode prev = head, curr = head.next;
            while (curr != null) {
                if (curr.song == song) {
                    prev.next = curr.next;
                    if (curr == tail) tail = prev;
                    return true;
                }
                prev = curr;
                curr = curr.next;
            }
            return false;
        }

        /** Playlist şarkılarını ArrayList olarak döner — O(n). */
        ArrayList<BittuneSong> songs() {
            ArrayList<BittuneSong> values = new ArrayList<>();
            SongNode temp = head;
            while (temp != null) {
                values.add(temp.song);
                temp = temp.next;
            }
            return values;
        }
    }

    // ── İç Bağlı Liste Düğümü (Şarkı listesi) ────────────────────
    private static class SongNode {
        final BittuneSong song;
        SongNode next;
        SongNode(BittuneSong song) { this.song = song; }
    }
}
