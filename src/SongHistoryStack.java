import java.util.ArrayList;

// Önceki çalınan şarkılar için Stack (Yığın) yapısı - ELLE YAZILDI
// LIFO mantığı: Son çalınan şarkıya ilk dönülür
public class SongHistoryStack {

    // Stack için dizi tabanlı uygulama (GUI erişimi için package-private)
    Song[] stack;
    int top;
    private static final int MAX_SIZE = 50;

    public SongHistoryStack() {
        stack = new Song[MAX_SIZE];
        top = -1;  // Boş stack
    }

    // Geçmişe şarkı ekle (push)
    public void push(Song song) {
        if (top >= MAX_SIZE - 1) {
            // Stack doluysa en alttaki şarkıyı at (kaydır)
            for (int i = 0; i < MAX_SIZE - 1; i++) {
                stack[i] = stack[i + 1];
            }
            stack[top] = song;
        } else {
            stack[++top] = song;
        }
    }

    // Son çalınan şarkıyı çıkar (pop)
    public Song pop() {
        if (isEmpty()) {
            System.out.println("Geçmiş boş, önceki şarkı yok.");
            return null;
        }
        return stack[top--];
    }

    // Son çalınan şarkıya bak (çıkarmadan)
    public Song peek() {
        if (isEmpty()) {
            return null;
        }
        return stack[top];
    }

    // Geçmiş şarkıları yazdır (en son çalınandan başlayarak)
    public void printHistory() {
        if (isEmpty()) {
            System.out.println("Geçmiş boş.");
            return;
        }

        System.out.println("\n=== Çalma Geçmişi (en son çalınandan) ===");
        for (int i = top; i >= 0; i--) {
            System.out.print((top - i + 1) + ". ");
            stack[i].printSong();
        }
    }

    // Stack'i yok etmeden ArrayList'e dönüştür, en üstten başlar (GUI için)
    public ArrayList<Song> toArrayList() {
        ArrayList<Song> list = new ArrayList<>();
        for (int i = top; i >= 0; i--) {
            list.add(stack[i]);
        }
        return list;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public int getSize() {
        return top + 1;
    }
}
