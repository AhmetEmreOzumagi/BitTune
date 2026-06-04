import java.util.ArrayList;

// Sıradaki şarkılar için Queue (Kuyruk) yapısı - ELLE YAZILDI
// FIFO mantığı: İlk eklenen şarkı önce çalınır
public class SongQueue {

    // Queue için kendi node yapımız (GUI'nin erişebilmesi için package-private)
    class QueueNode {
        Song song;
        QueueNode next;

        QueueNode(Song song) {
            this.song = song;
            this.next = null;
        }
    }

    QueueNode front;  // Kuyruğun başı (çıkış)
    QueueNode rear;   // Kuyruğun sonu (giriş)
    private int size;

    public SongQueue() {
        front = null;
        rear = null;
        size = 0;
    }

    // Kuyruğa şarkı ekle (enqueue)
    public void enqueue(Song song) {
        QueueNode newNode = new QueueNode(song);

        if (rear == null) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }

        size++;
        System.out.println("\"" + song.title + "\" sıraya eklendi.");
    }

    // Kuyruktan şarkı çıkar ve döndür (dequeue)
    public Song dequeue() {
        if (isEmpty()) {
            System.out.println("Sıra boş.");
            return null;
        }

        Song song = front.song;
        front = front.next;

        if (front == null) {
            rear = null;
        }

        size--;
        return song;
    }

    // Sıradaki şarkıya bak (çıkarmadan)
    public Song peek() {
        if (isEmpty()) {
            return null;
        }
        return front.song;
    }

    // Tüm kuyruğu yazdır
    public void printQueue() {
        if (isEmpty()) {
            System.out.println("Sıra boş.");
            return;
        }

        System.out.println("\n=== Sıradaki Şarkılar ===");
        QueueNode temp = front;
        int index = 1;
        while (temp != null) {
            System.out.print(index + ". ");
            temp.song.printSong();
            temp = temp.next;
            index++;
        }
    }

    // Kuyruğu yok etmeden ArrayList'e dönüştür (GUI için)
    public ArrayList<Song> toArrayList() {
        ArrayList<Song> list = new ArrayList<>();
        QueueNode temp = front;
        while (temp != null) {
            list.add(temp.song);
            temp = temp.next;
        }
        return list;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int getSize() {
        return size;
    }
}
