// ============================================================
// AudioBufferQueue — DSA: Dairesel Dizi Tabanlı Kuyruk
// Producer (okuyucu thread) → byte[] chunk → Consumer (çalıcı)
// ============================================================
class AudioBufferQueue {

    private static final int CAPACITY = 64;          // maksimum chunk sayısı

    private final byte[][] buffer = new byte[CAPACITY][];
    private int head = 0;   // tüketici okur
    private int tail = 0;   // üretici yazar
    private int size = 0;
    private volatile boolean closed = false;

    /** Üretici: kuyruğa chunk ekle (dolu ise bekle). */
    synchronized void enqueue(byte[] chunk) throws InterruptedException {
        while (size == CAPACITY && !closed) wait(20);
        if (closed) return;
        buffer[tail] = chunk;
        tail = (tail + 1) % CAPACITY;
        size++;
        notifyAll();
    }

    /** Tüketici: kuyruktan chunk al (boş ise bekle). null = kuyruk kapandı. */
    synchronized byte[] dequeue() throws InterruptedException {
        while (size == 0 && !closed) wait(20);
        if (size == 0) return null;   // closed + empty
        byte[] chunk = buffer[head];
        buffer[head] = null;
        head = (head + 1) % CAPACITY;
        size--;
        notifyAll();
        return chunk;
    }

    /** Kuyruğu temizle ve bekleyen thread'leri uyandır. */
    synchronized void close() {
        closed = true;
        head = tail = size = 0;
        notifyAll();
    }

    synchronized void reset() {
        closed = false;
        head = tail = size = 0;
    }

    synchronized boolean isEmpty()   { return size == 0; }
    synchronized int     size()      { return size; }
    synchronized boolean isClosed()  { return closed; }
}
