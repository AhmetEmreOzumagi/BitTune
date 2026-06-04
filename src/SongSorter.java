import java.util.ArrayList;

// Şarkı sıralama algoritmaları - elle kodlanmış
public class SongSorter {

    // --- Bubble Sort: Süreye göre artan sıralama ---
    // Her turda yan yana elemanları karşılaştırır, büyük olanı sağa iter
    public static void bubbleSortByDuration(ArrayList<Song> songs) {
        int n = songs.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (songs.get(j).duration > songs.get(j + 1).duration) {
                    // Yer değiştir
                    Song temp = songs.get(j);
                    songs.set(j, songs.get(j + 1));
                    songs.set(j + 1, temp);
                }
            }
        }
        System.out.println("Şarkılar süreye göre sıralandı (Bubble Sort).");
    }

    // --- Selection Sort: Beğeni sayısına göre azalan sıralama ---
    // Her turda en yüksek beğenili şarkıyı bulup öne taşır
    public static void selectionSortByLikesDescending(ArrayList<Song> songs) {
        int n = songs.size();
        for (int i = 0; i < n - 1; i++) {
            int maxIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (songs.get(j).likeCount > songs.get(maxIndex).likeCount) {
                    maxIndex = j;
                }
            }
            // En yüksek beğeniliyi i. pozisyona taşı
            Song temp = songs.get(i);
            songs.set(i, songs.get(maxIndex));
            songs.set(maxIndex, temp);
        }
        System.out.println("Şarkılar beğeni sayısına göre sıralandı (Selection Sort).");
    }

    // --- Insertion Sort: İsme göre alfabetik sıralama ---
    // Sıralı bölüme her seferinde bir eleman ekler
    public static void insertionSortByTitle(ArrayList<Song> songs) {
        int n = songs.size();
        for (int i = 1; i < n; i++) {
            Song key = songs.get(i);
            int j = i - 1;
            // key'den büyük olanları sağa kaydır
            while (j >= 0 && songs.get(j).title.compareToIgnoreCase(key.title) > 0) {
                songs.set(j + 1, songs.get(j));
                j--;
            }
            songs.set(j + 1, key);
        }
        System.out.println("Şarkılar isme göre alfabetik sıralandı (Insertion Sort).");
    }

    // --- Merge Sort: Süreye göre azalan sıralama ---
    // Böl ve birleştir yöntemi - daha verimli O(n log n)
    public static void mergeSortByDurationDescending(ArrayList<Song> songs, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSortByDurationDescending(songs, left, mid);
            mergeSortByDurationDescending(songs, mid + 1, right);
            merge(songs, left, mid, right);
        }
    }

    private static void merge(ArrayList<Song> songs, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Song[] leftArr = new Song[n1];
        Song[] rightArr = new Song[n2];

        for (int i = 0; i < n1; i++) leftArr[i] = songs.get(left + i);
        for (int j = 0; j < n2; j++) rightArr[j] = songs.get(mid + 1 + j);

        int i = 0, j = 0, k = left;
        // Azalan sıralama için > yerine < kullanıyoruz
        while (i < n1 && j < n2) {
            if (leftArr[i].duration >= rightArr[j].duration) {
                songs.set(k++, leftArr[i++]);
            } else {
                songs.set(k++, rightArr[j++]);
            }
        }
        while (i < n1) songs.set(k++, leftArr[i++]);
        while (j < n2) songs.set(k++, rightArr[j++]);
    }
}
