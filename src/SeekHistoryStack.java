// ============================================================
// SeekHistoryStack — DSA: Bagli Liste Tabanli Yigit (Stack)
// Kullanicinin seek (ileri/geri sarma) gecmisini tutar.
// "Geri al" ozelliginde onceki konuma donmek icin kullanilir.
// ============================================================
class SeekHistoryStack {

    private static class Node {
        final double ratio;   // 0.0 – 1.0 arasi konum
        Node next;
        Node(double ratio) { this.ratio = ratio; }
    }

    private Node top;
    private int  size;

    /** Yeni seek konumunu yigita ekle. */
    void push(double ratio) {
        Node node = new Node(ratio);
        node.next = top;
        top  = node;
        size++;
    }

    /** En son seek konumunu al ve cikar. */
    Double pop() {
        if (top == null) return null;
        double r = top.ratio;
        top = top.next;
        size--;
        return r;
    }

    /** En son seek konumunu cikarma, sadece bak. */
    Double peek() { return top != null ? top.ratio : null; }

    void    clear()    { top = null; size = 0; }
    boolean isEmpty()  { return top == null; }
    int     size()     { return size; }
}
