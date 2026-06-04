import java.util.ArrayList;
import java.util.Scanner;
import java.io.PrintStream;

// Ana program - Bittune Konsol Uygulaması
// Veri Yapıları ve Algoritmalar dersi projesi
public class Main {

    // Global şarkı kütüphanesi
    static ArrayList<Song> library = new ArrayList<>();

    // Veri yapıları
    static SongQueue queue = new SongQueue();
    static SongHistoryStack history = new SongHistoryStack();
    static AudioPlayer player = new AudioPlayer();
    static UserProfile profile = new UserProfile("Kullanıcı", "ornek@email.com");

    // Playlist listesi (birden fazla playlist olabilir)
    static ArrayList<Playlist> playlists = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        // Türkçe karakter desteği için UTF-8 çıkış
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        System.setErr(new PrintStream(System.err, true, "UTF-8"));

        loadSampleSongs();

        Scanner scanner = new Scanner(System.in, "UTF-8");
        int choice;

        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║       Bittune  ♪         ║");
        System.out.println("║  Veri Yapıları & Algoritmalar ║");
        System.out.println("╚══════════════════════════════╝");

        do {
            printMenu();
            System.out.print("Seçiminiz: ");

            while (!scanner.hasNextInt()) {
                System.out.print("Geçersiz giriş. Sayı giriniz: ");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine(); // Enter temizle

            switch (choice) {
                case 1:  showAllSongs(); break;
                case 2:  searchSong(scanner); break;
                case 3:  likeSong(scanner); break;
                case 4:  createPlaylist(scanner); break;
                case 5:  addSongToPlaylist(scanner); break;
                case 6:  showPlaylist(scanner); break;
                case 7:  removeSongFromPlaylist(scanner); break;
                case 8:  sortByDuration(); break;
                case 9:  sortByLikes(); break;
                case 10: sortByTitle(); break;
                case 11: addToQueue(scanner); break;
                case 12: playNextInQueue(); break;
                case 13: showQueue(); break;
                case 14: showHistory(); break;
                case 15: previousSong(); break;
                case 16: filterByGenre(scanner); break;
                case 17: profileMenu(scanner); break;
                case 18: player.printNowPlaying(); break;
                case 0:
                    System.out.println("\nGörüşürüz! ♪");
                    break;
                default:
                    System.out.println("Geçersiz seçim.");
            }

        } while (choice != 0);

        scanner.close();
    }

    // ─────────────────────────────────────────────
    // Menü
    // ─────────────────────────────────────────────
    static void printMenu() {
        System.out.println("\n══════════ MENÜ ══════════");
        System.out.println(" 1.  Tüm şarkıları göster");
        System.out.println(" 2.  Şarkı ara");
        System.out.println(" 3.  Şarkı beğen");
        System.out.println(" 4.  Playlist oluştur");
        System.out.println(" 5.  Playlist'e şarkı ekle");
        System.out.println(" 6.  Playlist'i göster");
        System.out.println(" 7.  Playlist'ten şarkı sil");
        System.out.println(" 8.  Süreye göre sırala (Bubble Sort)");
        System.out.println(" 9.  Beğeniye göre sırala (Selection Sort)");
        System.out.println("10.  İsme göre sırala (Insertion Sort)");
        System.out.println("11.  Sıraya ekle (Queue)");
        System.out.println("12.  Sıradaki şarkıyı çal");
        System.out.println("13.  Sırayı göster");
        System.out.println("14.  Çalma geçmişini göster (Stack)");
        System.out.println("15.  Önceki şarkı (Stack)");
        System.out.println("16.  Türe göre filtrele");
        System.out.println("17.  Profil ayarları");
        System.out.println("18.  Şu an çalıyor");
        System.out.println(" 0.  Çıkış");
        System.out.println("══════════════════════════");
    }

    // ─────────────────────────────────────────────
    // 1. Tüm şarkıları göster
    // ─────────────────────────────────────────────
    static void showAllSongs() {
        System.out.println("\n=== Şarkı Kütüphanesi (" + library.size() + " şarkı) ===");
        for (int i = 0; i < library.size(); i++) {
            System.out.print((i + 1) + ". ");
            library.get(i).printSong();
        }
    }

    // ─────────────────────────────────────────────
    // 2. Şarkı ara
    // ─────────────────────────────────────────────
    static void searchSong(Scanner scanner) {
        System.out.println("\n--- Şarkı Arama ---");
        System.out.println("1. İsme göre ara (Linear Search)");
        System.out.println("2. İsme göre ara (Binary Search - alfabetik sıralı gerekir)");
        System.out.println("3. Sanatçıya göre ara");
        System.out.print("Seçim: ");

        int opt = scanner.nextInt();
        scanner.nextLine();

        if (opt == 1) {
            System.out.print("Şarkı adı: ");
            String title = scanner.nextLine();
            Song found = SongSearcher.linearSearchByTitle(library, title);
            if (found != null) {
                System.out.println("Bulundu → ");
                found.printSong();
            } else {
                System.out.println("Şarkı bulunamadı.");
            }
        } else if (opt == 2) {
            System.out.print("Şarkı adı: ");
            String title = scanner.nextLine();
            // Önce sırala
            ArrayList<Song> sorted = new ArrayList<>(library);
            SongSorter.insertionSortByTitle(sorted);
            Song found = SongSearcher.binarySearchByTitle(sorted, title);
            if (found != null) {
                System.out.println("Bulundu (Binary Search) → ");
                found.printSong();
            } else {
                System.out.println("Şarkı bulunamadı.");
            }
        } else if (opt == 3) {
            System.out.print("Sanatçı adı: ");
            String artist = scanner.nextLine();
            ArrayList<Song> results = SongSearcher.searchByArtist(library, artist);
            System.out.println(results.size() + " sonuç bulundu:");
            SongSearcher.printResults(results);
        }
    }

    // ─────────────────────────────────────────────
    // 3. Şarkı beğen
    // ─────────────────────────────────────────────
    static void likeSong(Scanner scanner) {
        showAllSongs();
        System.out.print("Beğenmek istediğiniz şarkı numarası: ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine();

        if (index >= 0 && index < library.size()) {
            library.get(index).like();
            System.out.println("\"" + library.get(index).title + "\" beğenildi! Toplam beğeni: " + library.get(index).likeCount);
        } else {
            System.out.println("Geçersiz numara.");
        }
    }

    // ─────────────────────────────────────────────
    // 4. Playlist oluştur
    // ─────────────────────────────────────────────
    static void createPlaylist(Scanner scanner) {
        System.out.print("Playlist adı: ");
        String name = scanner.nextLine();
        playlists.add(new Playlist(name));
        System.out.println("\"" + name + "\" playlist'i oluşturuldu.");
    }

    // ─────────────────────────────────────────────
    // 5. Playlist'e şarkı ekle
    // ─────────────────────────────────────────────
    static void addSongToPlaylist(Scanner scanner) {
        if (playlists.isEmpty()) {
            System.out.println("Önce bir playlist oluşturun (Menü: 4).");
            return;
        }

        System.out.println("Playlist seçin:");
        for (int i = 0; i < playlists.size(); i++) {
            System.out.println((i + 1) + ". " + playlists.get(i).playlistName);
        }
        System.out.print("Seçim: ");
        int pIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        if (pIndex < 0 || pIndex >= playlists.size()) {
            System.out.println("Geçersiz seçim.");
            return;
        }

        showAllSongs();
        System.out.print("Eklenecek şarkı numarası: ");
        int sIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        if (sIndex >= 0 && sIndex < library.size()) {
            playlists.get(pIndex).addSong(library.get(sIndex));
        } else {
            System.out.println("Geçersiz numara.");
        }
    }

    // ─────────────────────────────────────────────
    // 6. Playlist'i göster
    // ─────────────────────────────────────────────
    static void showPlaylist(Scanner scanner) {
        if (playlists.isEmpty()) {
            System.out.println("Playlist yok.");
            return;
        }

        System.out.println("Hangi playlist?");
        for (int i = 0; i < playlists.size(); i++) {
            System.out.println((i + 1) + ". " + playlists.get(i).playlistName);
        }
        System.out.print("Seçim: ");
        int pIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        if (pIndex >= 0 && pIndex < playlists.size()) {
            playlists.get(pIndex).printPlaylist();
            playlists.get(pIndex).printTotalDuration();
        } else {
            System.out.println("Geçersiz seçim.");
        }
    }

    // ─────────────────────────────────────────────
    // 7. Playlist'ten şarkı sil
    // ─────────────────────────────────────────────
    static void removeSongFromPlaylist(Scanner scanner) {
        if (playlists.isEmpty()) {
            System.out.println("Playlist yok.");
            return;
        }

        System.out.println("Hangi playlist?");
        for (int i = 0; i < playlists.size(); i++) {
            System.out.println((i + 1) + ". " + playlists.get(i).playlistName);
        }
        System.out.print("Seçim: ");
        int pIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        if (pIndex >= 0 && pIndex < playlists.size()) {
            playlists.get(pIndex).printPlaylist();
            System.out.print("Silinecek şarkı adı: ");
            String title = scanner.nextLine();
            playlists.get(pIndex).removeSong(title);
        }
    }

    // ─────────────────────────────────────────────
    // 8-10. Sıralama
    // ─────────────────────────────────────────────
    static void sortByDuration() {
        SongSorter.bubbleSortByDuration(library);
        showAllSongs();
    }

    static void sortByLikes() {
        SongSorter.selectionSortByLikesDescending(library);
        showAllSongs();
    }

    static void sortByTitle() {
        SongSorter.insertionSortByTitle(library);
        showAllSongs();
    }

    // ─────────────────────────────────────────────
    // 11. Sıraya ekle (Queue)
    // ─────────────────────────────────────────────
    static void addToQueue(Scanner scanner) {
        showAllSongs();
        System.out.print("Sıraya eklenecek şarkı numarası: ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine();

        if (index >= 0 && index < library.size()) {
            queue.enqueue(library.get(index));
        } else {
            System.out.println("Geçersiz numara.");
        }
    }

    // ─────────────────────────────────────────────
    // 12. Sıradaki şarkıyı çal
    // ─────────────────────────────────────────────
    static void playNextInQueue() {
        Song next = queue.dequeue();
        if (next != null) {
            history.push(next);  // Geçmişe ekle
            player.play(next);
        }
    }

    // ─────────────────────────────────────────────
    // 13. Sırayı göster
    // ─────────────────────────────────────────────
    static void showQueue() {
        queue.printQueue();
    }

    // ─────────────────────────────────────────────
    // 14. Geçmişi göster (Stack)
    // ─────────────────────────────────────────────
    static void showHistory() {
        history.printHistory();
    }

    // ─────────────────────────────────────────────
    // 15. Önceki şarkı (Stack pop)
    // ─────────────────────────────────────────────
    static void previousSong() {
        Song prev = history.pop();
        if (prev != null) {
            System.out.println("Önceki şarkıya dönüldü:");
            player.play(prev);
        }
    }

    // ─────────────────────────────────────────────
    // 16. Türe göre filtrele
    // ─────────────────────────────────────────────
    static void filterByGenre(Scanner scanner) {
        System.out.println("Mevcut türler: Pop, Rock, Rap, Classical, Soundtrack");
        System.out.print("Tür adı: ");
        String genre = scanner.nextLine();

        ArrayList<Song> results = SongSearcher.searchByGenre(library, genre);
        System.out.println("\n=== " + genre + " şarkıları (" + results.size() + " sonuç) ===");
        SongSearcher.printResults(results);
    }

    // ─────────────────────────────────────────────
    // 17. Profil menüsü
    // ─────────────────────────────────────────────
    static void profileMenu(Scanner scanner) {
        System.out.println("\n--- Profil Ayarları ---");
        System.out.println("1. Profil bilgilerini göster");
        System.out.println("2. Kullanıcı adını değiştir");
        System.out.println("3. E-posta değiştir");
        System.out.println("4. Sesi artır");
        System.out.println("5. Sesi azalt");
        System.out.println("6. Ses seviyesi ayarla");
        System.out.println("7. Tema değiştir (Dark/Light)");
        System.out.print("Seçim: ");

        int opt = scanner.nextInt();
        scanner.nextLine();

        switch (opt) {
            case 1: profile.printProfile(); break;
            case 2:
                System.out.print("Yeni kullanıcı adı: ");
                profile.setUsername(scanner.nextLine());
                break;
            case 3:
                System.out.print("Yeni e-posta: ");
                profile.setEmail(scanner.nextLine());
                break;
            case 4: profile.increaseVolume(); break;
            case 5: profile.decreaseVolume(); break;
            case 6:
                System.out.print("Ses seviyesi (0-100): ");
                profile.setVolume(scanner.nextInt());
                scanner.nextLine();
                break;
            case 7: profile.toggleDarkMode(); break;
            default: System.out.println("Geçersiz seçim.");
        }
    }

    // ─────────────────────────────────────────────
    // Örnek veri seti - 10 şarkı
    // ─────────────────────────────────────────────
    static void loadSampleSongs() {
        library.add(new Song("Blinding Lights",    "The Weeknd",       "Pop",       200, 45, "audio/blinding_lights.wav"));
        library.add(new Song("Believer",            "Imagine Dragons",  "Rock",      204, 39, "audio/believer.wav"));
        library.add(new Song("Numb",                "Linkin Park",      "Rock",      187, 55, "audio/numb.wav"));
        library.add(new Song("Shape of You",        "Ed Sheeran",       "Pop",       233, 60, "audio/shape_of_you.wav"));
        library.add(new Song("Lose Yourself",       "Eminem",           "Rap",       326, 72, "audio/lose_yourself.wav"));
        library.add(new Song("Clair de Lune",       "Debussy",          "Classical", 300, 20, "audio/clair_de_lune.wav"));
        library.add(new Song("Bad Guy",             "Billie Eilish",    "Pop",       194, 41, "audio/bad_guy.wav"));
        library.add(new Song("Bohemian Rhapsody",   "Queen",            "Rock",      354, 80, "audio/bohemian_rhapsody.wav"));
        library.add(new Song("Interstellar Theme",  "Hans Zimmer",      "Soundtrack",250, 67, "audio/interstellar.wav"));
        library.add(new Song("Billie Jean",         "Michael Jackson",  "Pop",       294, 75, "audio/billie_jean.wav"));

        System.out.println("10 şarkı yüklendi.");
    }
}
