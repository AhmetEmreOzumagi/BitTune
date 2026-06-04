// Playlist - Linked List kullanılarak uygulandı
// Şarkı ekleme/silme ve gezinme işlemleri için Linked List idealdir
public class Playlist {
    String playlistName;
    SongNode head;  // Listenin başı

    public Playlist(String playlistName) {
        this.playlistName = playlistName;
        this.head = null;
    }

    // Playlist sonuna şarkı ekle
    public void addSong(Song song) {
        SongNode newNode = new SongNode(song);

        if (head == null) {
            head = newNode;
            return;
        }

        // Sona kadar git
        SongNode temp = head;
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = newNode;
        System.out.println("\"" + song.title + "\" playlist'e eklendi.");
    }

    // Playlist'ten şarkı sil (isme göre)
    public void removeSong(String title) {
        if (head == null) {
            System.out.println("Playlist boş.");
            return;
        }

        // Başta olan şarkıyı sil
        if (head.song.title.equalsIgnoreCase(title)) {
            head = head.next;
            System.out.println("\"" + title + "\" playlist'ten silindi.");
            return;
        }

        // Ortada veya sonda olan şarkıyı bul ve sil
        SongNode temp = head;
        while (temp.next != null) {
            if (temp.next.song.title.equalsIgnoreCase(title)) {
                temp.next = temp.next.next;
                System.out.println("\"" + title + "\" playlist'ten silindi.");
                return;
            }
            temp = temp.next;
        }

        System.out.println("\"" + title + "\" playlist'te bulunamadı.");
    }

    // Playlist'teki tüm şarkıları yazdır
    public void printPlaylist() {
        if (head == null) {
            System.out.println("Playlist boş.");
            return;
        }

        System.out.println("\n=== Playlist: " + playlistName + " ===");
        SongNode temp = head;
        int index = 1;
        while (temp != null) {
            System.out.print(index + ". ");
            temp.song.printSong();
            temp = temp.next;
            index++;
        }
    }

    // Playlist toplam süresini hesapla
    public int calculateTotalDuration() {
        int total = 0;
        SongNode temp = head;
        while (temp != null) {
            total += temp.song.duration;
            temp = temp.next;
        }
        return total;
    }

    // Toplam süreyi formatlanmış şekilde yazdır
    public void printTotalDuration() {
        int total = calculateTotalDuration();
        int minutes = total / 60;
        int seconds = total % 60;
        System.out.println("Toplam süre: " + minutes + " dakika " + seconds + " saniye");
    }

    // Playlist boş mu?
    public boolean isEmpty() {
        return head == null;
    }
}
