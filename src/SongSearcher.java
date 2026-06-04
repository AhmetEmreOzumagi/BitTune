import java.util.ArrayList;

// Şarkı arama algoritmaları
public class SongSearcher {

    // --- Linear Search: Şarkı adına göre arama ---
    // Sırasız listede kullanılır, her elemanı tek tek kontrol eder O(n)
    public static Song linearSearchByTitle(ArrayList<Song> songs, String title) {
        for (Song song : songs) {
            if (song.title.equalsIgnoreCase(title)) {
                return song;
            }
        }
        return null;
    }

    // --- Linear Search: Sanatçı adına göre arama ---
    public static ArrayList<Song> searchByArtist(ArrayList<Song> songs, String artist) {
        ArrayList<Song> result = new ArrayList<>();
        for (Song song : songs) {
            if (song.artist.equalsIgnoreCase(artist)) {
                result.add(song);
            }
        }
        return result;
    }

    // --- Linear Search: Türe göre filtreleme ---
    public static ArrayList<Song> searchByGenre(ArrayList<Song> songs, String genre) {
        ArrayList<Song> result = new ArrayList<>();
        for (Song song : songs) {
            if (song.genre.equalsIgnoreCase(genre)) {
                result.add(song);
            }
        }
        return result;
    }

    // --- Binary Search: Alfabetik sıralı listede şarkı adına göre arama ---
    // Liste önce insertionSortByTitle ile sıralanmış olmalı! O(log n)
    public static Song binarySearchByTitle(ArrayList<Song> songs, String title) {
        int left = 0;
        int right = songs.size() - 1;

        while (left <= right) {
            int mid = (left + right) / 2;
            int comparison = songs.get(mid).title.compareToIgnoreCase(title);

            if (comparison == 0) {
                return songs.get(mid);  // Bulundu
            } else if (comparison < 0) {
                left = mid + 1;  // Sağ yarıda ara
            } else {
                right = mid - 1;  // Sol yarıda ara
            }
        }
        return null;  // Bulunamadı
    }

    // Sonuçları yazdır
    public static void printResults(ArrayList<Song> songs) {
        if (songs.isEmpty()) {
            System.out.println("Sonuç bulunamadı.");
            return;
        }
        int index = 1;
        for (Song song : songs) {
            System.out.print(index + ". ");
            song.printSong();
            index++;
        }
    }
}
