import java.awt.Color;
import java.awt.Font;

final class BittuneTheme {
    // ── Arka plan katmanları (derinlik hiyerarşisi) ─────────────────
    static final Color BG_MAIN    = new Color(0x080C12);   // en derin
    static final Color BG_SIDEBAR = new Color(0x060A10);   // sidebar
    static final Color BG_CARD    = new Color(0x111827);   // kart yüzeyi
    static final Color BG_TABLE   = new Color(0x0C1118);   // tablo
    static final Color BG_ELEVATED= new Color(0x141E2C);   // yükseltilmiş yüzey

    // ── Marka renkleri ──────────────────────────────────────────────
    static final Color PURPLE       = new Color(0x6D28D9);
    static final Color PURPLE_LIGHT = new Color(0x8B5CF6);
    static final Color PURPLE_DIM   = new Color(0x4C1D95);
    static final Color MAGENTA      = new Color(0xC026D3);

    // ── Metin ───────────────────────────────────────────────────────
    static final Color TEXT        = new Color(0xEEF2FF);
    static final Color TEXT_MUTED  = new Color(0x7A94B4);
    static final Color TEXT_DIM    = new Color(0x445566);

    // ── Çizgiler & vurgular ─────────────────────────────────────────
    static final Color BORDER       = new Color(0x1C2A3E);
    static final Color ROW_SELECTED = new Color(0x1E1B38);
    static final Color ROW_HOVER    = new Color(0x131E2E);

    // ── Fontlar — SansSerif: Windows'ta Segoe UI, Türkçe tam desteği ─
    static final Font FONT_BODY  = new Font("SansSerif", Font.PLAIN, 14);
    static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    static final Font FONT_BOLD  = new Font("SansSerif", Font.BOLD,  14);

    private BittuneTheme() {}
}
