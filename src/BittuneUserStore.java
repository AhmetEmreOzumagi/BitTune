import java.util.ArrayList;

// ============================================================
// BittuneUser - Kullanici veri sinifi
// likedTitles: bu kullanicinin beğendigi sarki baslikları
// ============================================================
class BittuneUser {
    final String username;
    final String password;
    // DSA: kullaniciya ozel begeni listesi (ekleme sirasi korunur)
    final ArrayList<String> likedTitles = new ArrayList<>();

    BittuneUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /** Sarki begeni durumunu gunceller; true=eklendi false=cikarildi */
    void setLiked(String title, boolean liked) {
        if (liked) {
            if (!likedTitles.contains(title)) likedTitles.add(title);
        } else {
            likedTitles.remove(title);
        }
    }

    boolean hasLiked(String title) {
        return likedTitles.contains(title);
    }
}

// ============================================================
// BittuneUserStore - Ozet Bagli Liste (DSA: Linked List)
// Kayitli kullanicilari tutar; ayni kullanici adi kontrolu yapar
// ============================================================
class BittuneUserStore {
    private static class Node {
        BittuneUser data;
        Node next;
        Node(BittuneUser data) { this.data = data; }
    }

    private Node head;
    private int size;

    /** Yeni kullanici kaydeder. Kullanici adi zaten varsa false doner. */
    boolean register(String username, String password) {
        if (usernameExists(username)) return false;
        Node node = new Node(new BittuneUser(username, password));
        node.next = head;   // Basina ekle — O(1)
        head = node;
        size++;
        return true;
    }

    /** Kullanici adi + sifre eslesmesini dogrusal arama ile kontrol eder. */
    BittuneUser authenticate(String username, String password) {
        Node curr = head;
        while (curr != null) {                          // O(n) linear search
            if (curr.data.username.equalsIgnoreCase(username)
                    && curr.data.password.equals(password)) {
                return curr.data;
            }
            curr = curr.next;
        }
        return null;
    }

    /** Kullanici adina gore kullaniciyi dondurur (kucuk/buyuk harf duyarsiz). */
    BittuneUser findByUsername(String username) {
        Node curr = head;
        while (curr != null) {
            if (curr.data.username.equalsIgnoreCase(username)) return curr.data;
            curr = curr.next;
        }
        return null;
    }

    /** Kullanici adi benzersizligini kontrol eder (kucuk/buyuk harf duyarsiz). */
    boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }

    int size() { return size; }

    /** Tum kullanicilari liste olarak dondurur (accounts.properties kaydi icin). */
    ArrayList<BittuneUser> allUsers() {
        ArrayList<BittuneUser> list = new ArrayList<>();
        Node curr = head;
        while (curr != null) { list.add(curr.data); curr = curr.next; }
        return list;
    }
}

// ============================================================
// BittuneLoginHistory - Giris Gecmisi Yigiti (DSA: Stack)
// En son giris yapan kullanicilari LIFO sirasiyla tutar
// ============================================================
class BittuneLoginHistory {
    private static class Node {
        final String username;
        final long   loginTime;
        Node next;
        Node(String username) {
            this.username  = username;
            this.loginTime = System.currentTimeMillis();
        }
    }

    private Node top;
    private int  size;

    /** Giris yapan kullanicini yigita it. */
    void push(String username) {
        Node node = new Node(username);
        node.next = top;
        top  = node;
        size++;
    }

    /** En son giris yapan kullanici adini dondur (yigiti bozmadan). */
    String peek() { return top != null ? top.username : null; }

    /** En uste bak ve cikart. */
    String pop() {
        if (top == null) return null;
        String u = top.username;
        top = top.next;
        size--;
        return u;
    }

    boolean isEmpty() { return top == null; }
    int     size()    { return size; }
}
