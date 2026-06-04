// ╔══════════════════════════════════════════════════════════════╗
// ║  PROJE MİMARİSİ — Hangi sınıf ne yapar?                     ║
// ╠══════════════════════════════════════════════════════════════╣
// ║                                                              ║
// ║  BittuneGUI.java        ← BURADA (sadece UI)            ║
// ║    • Ana pencere + UI kurulumu                               ║
// ║    • Ses oynatma (audioPlayer, startAudio, togglePlayPause)  ║
// ║    • Kullanıcı etkileşimleri (tıklama, arama, beğeni)        ║
// ║    • DSA işlemleri için BittuneCore'u çağırır               ║
// ║                                                              ║
// ║  BittuneCore.java          ← TÜM DSA İŞLEMLERİ            ║
// ║    • Bölüm 1 — VERİ YAPILARI (6 yapı)                       ║
// ║        HashMap  O(1)      — başlığa anlık erişim             ║
// ║        BST      O(log n)  — sıralı arama fallback            ║
// ║        Trie     O(k)      — arama kutusu önerisi             ║
// ║        MaxHeap  O(log n)  — top-N en çok çalınan             ║
// ║        Queue    O(1)      — FIFO, "Sıraya Ekle"              ║
// ║        Stack    O(1)      — LIFO, "← Önceki" geçmişi        ║
// ║    • Bölüm 2 — İNDEKSLEME (buildIndex, updateEntry)         ║
// ║    • Bölüm 3 — FİLTRE (showAll, showLiked, showPlaylist)     ║
// ║    • Bölüm 4 — ARAMA  (search Trie→Linear, findExact H→B→L) ║
// ║    • Bölüm 5 — SIRALAMA (MergeSort / InsertSort / SelSort)   ║
// ║    • Bölüm 6 — KUYRUK FIFO (enqueue, dequeue, clear)         ║
// ║    • Bölüm 7 — YIĞIN LIFO (pushHistory, popHistory)          ║
// ║    • Bölüm 8 — NAVİGASYON (nextSong, previousSong, upNext)   ║
// ║    • Bölüm 9 — TOP-N MaxHeap (topN)                          ║
// ║                                                              ║
// ║  BittuneDataStructures.java  ← VERİ YAPISI IMPLEMENTASYONU ║
// ║    • BittunePlaylistLinkedList  — çift yönlü bağlı liste    ║
// ║    • BittuneSongQueue           — FIFO bağlı liste          ║
// ║    • BittuneSongHistoryStack    — LIFO bağlı liste          ║
// ║    • BittuneSongHashMap         — Ayrı zincirleme hash tablo║
// ║    • BittuneSongBST             — İkili arama ağacı         ║
// ║    • BittuneSearchTrie          — Ön ek ağacı (Trie)        ║
// ║    • BittuneMaxHeap             — Öncelikli kuyruk (MaxHeap)║
// ║                                                              ║
// ║  BittuneAlgorithms.java  ← ALGORİTMA IMPLEMENTASYONU       ║
// ║    • BittuneSongSorter:                                     ║
// ║        mergeSortByTitle   — O(n log n)                       ║
// ║        insertionSortByDuration — O(n²)                       ║
// ║        selectionSortByText / selectionSortByLiked — O(n²)    ║
// ║        bubbleSortByDuration — O(n²)                          ║
// ║    • BittuneSongSearcher:                                   ║
// ║        linearSearch  — O(n) fallback                         ║
// ║        binarySearchByTitle — O(log n) sıralı listede         ║
// ║                                                              ║
// ║  BittuneStorage.java   ← DISK OKUMA/YAZMA                  ║
// ║  BittuneComponents.java  ← UI BİLEŞENLERİ                  ║
// ║  BittuneLibrary.java   ← MP3 YÜKLEME (ID3 tag)             ║
// ║  BittuneAudioPlayer.java ← GERÇEK SES OYNATMA              ║
// ║  BittuneLoginScreen.java ← GİRİŞ EKRANI                    ║
// ╚══════════════════════════════════════════════════════════════╝

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class BittuneGUI extends JFrame {
    private static final Color BG_MAIN    = new Color(0x090D12);
    private static final Color BG_SIDEBAR = new Color(0x06080D);
    private static final Color BG_CARD    = new Color(0x131A23);
    private static final Color BG_TABLE   = new Color(0x0E1319);
    private static final Color BG_RIGHT   = new Color(0x100F1A);
    private static final Color PURPLE       = new Color(0x6D28D9);
    private static final Color PURPLE_LIGHT = new Color(0x8B5CF6);
    private static final Color MAGENTA      = new Color(0xC026D3);
    private static final Color TEAL         = new Color(0x0EA5E9);
    private static final Color TEXT         = new Color(0xF1F5F9);
    private static final Color TEXT_MUTED   = new Color(0x8BA3BE);
    private static final Color BORDER       = new Color(0x1E2736);
    private static final Color ROW_SELECTED = new Color(0x231F3C);

    // SansSerif → Windows'ta Segoe UI, Türkçe karakterleri tam destekler
    private static final Font FONT_TITLE   = new Font("SansSerif", Font.BOLD,  26);
    private static final Font FONT_SECTION = new Font("SansSerif", Font.BOLD,  18);
    private static final Font FONT_BODY    = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_SMALL   = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_BOLD    = new Font("SansSerif", Font.BOLD,  14);

    private BittuneUserStore     userStore;
    private BittuneLoginHistory  loginHistory;
    private BittuneUser          currentUser;        // oturum acik kullanici
    private final BittuneAudioPlayer audioPlayer = new BittuneAudioPlayer();

    private final ArrayList<BittuneSong> songs = new ArrayList<>();
    private final ArrayList<BittuneSong> visibleSongs = new ArrayList<>();
    private final ArrayList<BittuneSong> topSongPool = new ArrayList<>();
    private final BittunePlaylistLinkedList playlists = new BittunePlaylistLinkedList();
    private final ArrayList<NavItemPanel> navItems = new ArrayList<>();

    // ── DSA Merkezi — HashMap, BST, Trie, MaxHeap, Queue, Stack + Algoritmalar ─
    // GUI doğrudan hiçbir veri yapısına erişmez; tüm işlemler core üzerinden gider.
    // Detay: BittuneCore.java (9 bölüm: VERİ YAPILARI, İNDEKSLEME, FİLTRE,
    //         ARAMA, SIRALAMA, KUYRUK, YIĞIN, NAVİGASYON, TOP-N)
    private BittuneCore core;

    private BittuneSong currentSong;
    private String userName;
    private String userEmail = "murat@example.com";
    private String userStatus = "Premium desktop listener";
    private boolean playing = false;
    private boolean syncingVolume;
    private boolean muted;
    // Mevcut şarkının oynatma sayısı bu oturumda kaydedildi mi?
    // false → ilk play tıklamasında playCount artırılacak
    // true  → loadSongUI(song,true) veya ilk play'de zaten sayıldı
    private boolean currentSongCounted = false;
    private int lastVolumeBeforeMute = 70;
    private int lastSortColumn = -1;
    private boolean sortAscending = true;
    private ArrayList<BittuneSong> playContext = new ArrayList<>();
    private String playContextName = "Tüm Şarkılar";
    // Sıra dialogunda "Sıradan Kaldır" ile geçici olarak atlanan şarkılar
    private final java.util.Set<BittuneSong> queueViewSkip = new java.util.HashSet<>();

    private JTable songTable;
    private BittuneSongTableModel songModel;
    private JPanel upNextList;
    private JPanel topRepeatedCardsPanel;
    private JPanel playlistArea;
    private JTextField searchField;
    private JComponent searchShell;
    private JLabel songSectionTitle;
    private JLabel greetingLabel;
    private JLabel accountNameLabel;
    private JLabel accountAvatarLabel;

    private AlbumArtPanel bottomArt;
    private JLabel bottomTitle;
    private JLabel bottomArtist;
    private JLabel bottomDuration;
    private HeartButton bottomLikeButton;
    private RoundedButton bottomPlayButton;
    private PlayIconButton sectionPlayButton;
    private JSlider bottomVolumeSlider;
    private JLabel bottomVolumePercent;
    private SpeakerButton muteButton;
    private JSlider progressSlider;
    private JLabel progressElapsed;
    private JLabel progressTotal;
    private javax.swing.Timer playbackTimer;
    private int elapsedSeconds = 0;
    private boolean realAudioActive = false;

    public static void main(String[] args) {
        // Türkçe karakter desteği için font AA
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            // DSA: LinkedList store + Stack history
            BittuneUserStore store = new BittuneUserStore();
            BittuneLoginHistory loginHist = new BittuneLoginHistory();
            // Demo kullanicilar (her zaman kayitli)
            store.register("demo", "1234");
            store.register("murat", "Bittune");
            // Daha once kaydolmus ek kullanicilari diskten yukle
            BittuneStorage.loadAccounts(store);

            // "Oturumu Acik Tut" — session.txt kontrol
            String savedUser = BittuneStorage.loadSession();
            if (savedUser != null) {
                BittuneUser autoUser = store.findByUsername(savedUser);
                if (autoUser != null) {
                    loginHist.push(autoUser.username);
                    new BittuneGUI(autoUser.username, store, loginHist);
                    return;
                }
                // Kullanici bulunamazsa session'i temizle, giris ekranini goster
                BittuneStorage.clearSession();
            }

            new BittuneLoginScreen(store, loginHist);
        });
    }

    public BittuneGUI(String loggedInUser, BittuneUserStore store, BittuneLoginHistory history) {
        this.userName     = loggedInUser;
        this.userStore    = store;
        this.loginHistory = history;
        this.currentUser  = (store != null) ? store.findByUsername(loggedInUser) : null;
        loadData();
        // Diskten begeni + playlist yukle, sonra sarkilara uygula
        if (currentUser != null) BittuneStorage.loadInto(currentUser, songs, playlists);
        applyUserLikes(); // Bu kullanicinin beğenilerini sarkilara uygula
        buildIndex();     // DSA: HashMap + BST + Trie + MaxHeap'i doldur
        configureWindow();
        setContentPane(buildRoot());
        installSearchFocusDismissal();
        if (currentSong != null) setNowPlaying(currentSong, false);
        updateSectionPlayButton();
        playing = false; // giris yapilinca otomatik calmasin
        setVisible(true);
    }

    private void configureWindow() {
        setTitle("Bittune");
        // Pencere kapanınca play sayaçlarını + beğenileri diske yaz
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                saveAllAndExit();
            }
        });
        setSize(1400, 800);
        setMinimumSize(new Dimension(1180, 700));
        setLocationRelativeTo(null);
    }

    /** Uygulama kapanışında tüm kullanıcı verisini (beğeni + playlist + play sayacı) diske yazar. */
    private void saveAllAndExit() {
        if (currentUser != null) {
            BittuneStorage.saveUser(currentUser, playlists, songs);
            System.out.println("[Storage] Kapanışta play sayaçları kaydedildi.");
        }
        if (audioPlayer != null) audioPlayer.stop();
        if (playbackTimer != null) playbackTimer.stop();
        System.exit(0);
    }

    private JComponent buildRoot() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_MAIN);
        root.setFocusable(true);

        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(BG_MAIN);
        dashboard.add(buildSidebar(), BorderLayout.WEST);
        dashboard.add(buildMainContent(), BorderLayout.CENTER);
        dashboard.add(buildRightPanel(), BorderLayout.EAST);

        root.add(dashboard, BorderLayout.CENTER);
        root.add(buildBottomPlayer(), BorderLayout.SOUTH);
        return root;
    }

    private void installSearchFocusDismissal() {
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (!(event instanceof MouseEvent mouseEvent) || mouseEvent.getID() != MouseEvent.MOUSE_PRESSED) {
                return;
            }
            if (searchField == null || !searchField.isFocusOwner()) {
                return;
            }
            Object source = mouseEvent.getSource();
            if (!(source instanceof Component component)) {
                return;
            }
            if (component == searchField || (searchShell != null && SwingUtilities.isDescendingFrom(component, searchShell))) {
                return;
            }

            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
            SwingUtilities.invokeLater(() -> getContentPane().requestFocusInWindow());
        }, AWTEvent.MOUSE_EVENT_MASK);
    }

    private JComponent buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(0x08101E), 0, getHeight(), new Color(0x05080E)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Sağ kenar ince ayırıcı
                g2.setColor(new Color(0x1A2538));
                g2.fillRect(getWidth() - 1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(195, 0));
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(22, 18, 18, 18));

        JLabel logo = new JLabel("Bittune", SwingConstants.CENTER);
        logo.setForeground(TEXT);
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setMaximumSize(new Dimension(184, 34));
        logo.setBorder(new EmptyBorder(0, 0, 28, 0));
        sidebar.add(logo);

        sidebar.add(navItem("\u2302", "Ana Sayfa", true));
        sidebar.add(navItem("\uD83D\uDD0D", "Ara", false));
        sidebar.add(navItem("\u2630", "Çalma Listeleri", false));
        sidebar.add(navItem("\u2661", "Beğenilenler", false));
        sidebar.add(navItem("\u2261", "Sıra", false));
        sidebar.add(navItem("\uD83D\uDC64", "Profil", false));
        sidebar.add(navItem("\u2699", "Ayarlar", false));

        sidebar.add(strut(16));
        JSeparator separator = new JSeparator();
        separator.setForeground(BORDER);
        separator.setMaximumSize(new Dimension(184, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(separator);
        sidebar.add(strut(18));

        JLabel playlistLabel = new JLabel("PLAYLISTS");
        playlistLabel.setForeground(TEXT_MUTED);
        playlistLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        playlistLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        playlistLabel.setBorder(new EmptyBorder(0, 12, 10, 0));
        sidebar.add(playlistLabel);

        playlistArea = new JPanel();
        playlistArea.setOpaque(false);
        playlistArea.setLayout(new BoxLayout(playlistArea, BoxLayout.Y_AXIS));
        playlistArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        playlistArea.setMaximumSize(new Dimension(184, Integer.MAX_VALUE));
        refreshSidebarPlaylists(playlistArea);
        sidebar.add(playlistArea);

        RoundedButton createPlaylist = new RoundedButton("+ Yeni Playlist", new Color(0x121821), new Color(0x1C2430), 12);
        createPlaylist.setMaximumSize(new Dimension(184, 38));
        createPlaylist.setPreferredSize(new Dimension(184, 38));
        createPlaylist.setMinimumSize(new Dimension(184, 38));
        createPlaylist.setAlignmentX(Component.LEFT_ALIGNMENT);
        createPlaylist.setFont(new Font("SansSerif", Font.BOLD, 12));
        createPlaylist.setBorder(new EmptyBorder(0, 12, 0, 8));
        createPlaylist.setHorizontalAlignment(SwingConstants.LEFT);
        createPlaylist.addActionListener(e -> {
            String name = showTextInputDialog("Yeni Playlist", "Playlist adı");
            if (name != null && !name.trim().isEmpty()) {
                if (playlists.add(name.trim())) {
                    if (currentUser != null) BittuneStorage.saveUser(currentUser, playlists);
                    refreshSidebarPlaylists(playlistArea);
                    showInfoDialog("Playlist Oluşturuldu", "\"" + name.trim() + "\" hazır.");
                } else {
                    showInfoDialog("Playlist Zaten Var", "\"" + name.trim() + "\" adında bir playlist zaten var.");
                }
            }
        });
        sidebar.add(strut(6));
        sidebar.add(createPlaylist);
        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private void refreshSidebarPlaylists(JPanel playlistArea) {
        playlistArea.removeAll();
        ArrayList<String> userPlaylists = playlists.toArrayList();
        if (userPlaylists.isEmpty()) {
            JLabel empty = label("Henüz playlist yok", FONT_SMALL, TEXT_MUTED);
            empty.setBorder(new EmptyBorder(2, 12, 6, 0));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            empty.setMaximumSize(new Dimension(184, 24));
            playlistArea.add(empty);
        } else {
            Color[] iconColors = {
                    new Color(0x3B82F6), new Color(0xEC4899),
                    new Color(0x06B6D4), new Color(0x22C55E)
            };
            int colorIndex = 0;
            for (String name : userPlaylists) {
                playlistArea.add(playlistRow(name, iconColors[colorIndex++ % iconColors.length]));
            }
        }
        playlistArea.setPreferredSize(new Dimension(184, Math.max(120, playlistArea.getComponentCount() * 48)));
        playlistArea.revalidate();
        playlistArea.repaint();
    }

    private JComponent navItem(String icon, String text, boolean selected) {
        NavItemPanel item = new NavItemPanel(icon, text, selected);
        item.setMaximumSize(new Dimension(184, 46));
        item.setPreferredSize(new Dimension(184, 46));
        item.setBorder(new EmptyBorder(0, 12, 0, 10));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        item.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                setActiveNav(item);
                // invokeLater: önce nav seçim repaint'i işlensin, sonra veri güncellemesi gelsin
                SwingUtilities.invokeLater(() -> handleSidebarAction(text));
            }
        });
        navItems.add(item);
        return withBottomGap(item, 7);
    }

    private void setActiveNav(NavItemPanel active) {
        for (NavItemPanel item : navItems) {
            item.setSelected(item == active);
            // Wrapper panel'i de yenile (withBottomGap sarıcısı)
            if (item.getParent() != null) item.getParent().repaint();
        }
    }

    private JComponent playlistRow(String name, Color color) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(184, 42));
        row.setPreferredSize(new Dimension(184, 42));
        row.setMinimumSize(new Dimension(184, 42));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(new EmptyBorder(0, 12, 0, 10));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        RoundedPanel iconBox = new RoundedPanel(color, 8);
        iconBox.setPreferredSize(new Dimension(26, 26));
        JLabel note = new JLabel("♫", SwingConstants.CENTER);
        note.setForeground(TEXT);
        note.setFont(new Font("Segoe UI Symbol", Font.BOLD, 13));
        iconBox.setLayout(new BorderLayout());
        iconBox.add(note);

        JLabel label = new JLabel(name);
        label.setForeground(TEXT_MUTED);
        label.setFont(FONT_BODY);

        row.add(iconBox, BorderLayout.WEST);
        row.add(label, BorderLayout.CENTER);
        row.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) playPlaylist(name);
                else showPlaylistSongs(name);
            }
            public void mouseEntered(MouseEvent e) { label.setForeground(TEXT); }
            public void mouseExited(MouseEvent e)  { label.setForeground(TEXT_MUTED); }
        });
        return withBottomGap(row, 4);
    }

    private JComponent buildMainContent() {
        JPanel main = new JPanel(new BorderLayout(0, 14));
        main.setBackground(BG_MAIN);
        main.setBorder(new EmptyBorder(18, 22, 14, 10));
        main.add(buildTopBar(), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setBackground(BG_MAIN);
        content.add(buildRecentlyPlayed(), BorderLayout.NORTH);
        content.add(buildSongTableSection(), BorderLayout.CENTER);
        main.add(content, BorderLayout.CENTER);
        return main;
    }

    private JComponent buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(14, 0));
        top.setOpaque(false);

        searchField = new JTextField("Şarkı, sanatçı veya albüm ara...");
        searchField.setForeground(TEXT_MUTED);
        searchField.setCaretColor(TEXT);
        searchField.setFont(FONT_BODY);
        searchField.setOpaque(false);
        searchField.setBorder(new EmptyBorder(0, 16, 0, 16));
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Şarkı, sanatçı veya albüm ara...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Şarkı, sanatçı veya albüm ara...");
                    searchField.setForeground(TEXT_MUTED);
                }
            }
        });
        searchField.addActionListener(e -> searchOnly(searchField.getText()));
        searchField.setBackground(new Color(0x0F1520));
        // Özel arama shell — büyüteç ikonlu + focus glow
        searchShell = new JPanel(new BorderLayout(6, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x0F1520));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                boolean focused = searchField != null && searchField.isFocusOwner();
                g2.setColor(focused ? new Color(0x6D28D9, false) : new Color(0x1C2A3E));
                g2.setStroke(new BasicStroke(focused ? 1.5f : 1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.dispose();
            }
        };
        searchShell.setOpaque(false);
        searchShell.setPreferredSize(new Dimension(410, 42));
        searchShell.setBorder(new EmptyBorder(0, 14, 0, 10));
        // Büyüteç ikonu
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setForeground(TEXT_MUTED);
        searchIcon.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchShell.add(searchIcon, BorderLayout.WEST);
        searchShell.add(searchField, BorderLayout.CENTER);
        // Focus değişince border repaint
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { searchShell.repaint(); }
            public void focusLost (java.awt.event.FocusEvent e)  { searchShell.repaint(); }
        });
        top.add(searchShell, BorderLayout.CENTER);

        JPanel account = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        account.setOpaque(false);
        RoundedPanel avatar = new RoundedPanel(PURPLE, 40);
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setLayout(new BorderLayout());
        accountAvatarLabel = new JLabel(initialOf(userName), SwingConstants.CENTER);
        accountAvatarLabel.setForeground(TEXT);
        accountAvatarLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        avatar.add(accountAvatarLabel, BorderLayout.CENTER);
        avatar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        avatar.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showProfileDialog();
            }
        });
        account.add(avatar);

        accountNameLabel = label(userName, FONT_BODY, TEXT);
        accountNameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        accountNameLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showProfileDialog();
            }
        });
        account.add(accountNameLabel);
        top.add(account, BorderLayout.EAST);
        return top;
    }

    private JComponent buildRecentlyPlayed() {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setOpaque(false);

        greetingLabel = new JLabel(timeGreeting() + ", " + userName);
        greetingLabel.setForeground(TEXT);
        greetingLabel.setFont(FONT_TITLE);
        section.add(greetingLabel, BorderLayout.NORTH);

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("En Çok Çalınanlar");
        title.setForeground(TEXT);
        title.setFont(FONT_SECTION);
        header.add(title, BorderLayout.WEST);
        bottom.add(header, BorderLayout.NORTH);

        topRepeatedCardsPanel = new JPanel(new GridLayout(1, 6, 10, 0));
        topRepeatedCardsPanel.setOpaque(false);
        topRepeatedCardsPanel.setPreferredSize(new Dimension(0, 148));
        topRepeatedCardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 148));
        refreshTopRepeatedCards();
        bottom.add(topRepeatedCardsPanel, BorderLayout.CENTER);
        section.add(bottom, BorderLayout.CENTER);
        return section;
    }

    private JComponent buildSongTableSection() {
        RoundedPanel shell = new RoundedPanel(new Color(0x0F141B), 16);
        shell.setLayout(new BorderLayout(0, 14));
        shell.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        songSectionTitle = new JLabel("Tüm Şarkılar");
        songSectionTitle.setForeground(TEXT);
        songSectionTitle.setFont(FONT_SECTION);

        JPanel titleActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleActions.setOpaque(false);
        titleActions.add(songSectionTitle);

        sectionPlayButton = new PlayIconButton();
        sectionPlayButton.setPreferredSize(new Dimension(30, 28));
        sectionPlayButton.setToolTipText("Bu listeyi başlat");
        sectionPlayButton.setVisible(false);
        sectionPlayButton.addActionListener(e -> playCurrentPlaylistSection());
        titleActions.add(sectionPlayButton);

        header.add(titleActions, BorderLayout.WEST);

        JLabel sortHint = label("Sağ tık → playlist / sıra  ·  Sütun başlığı → sırala", FONT_SMALL, TEXT_MUTED);
        sortHint.setBorder(new EmptyBorder(0, 0, 0, 6));
        header.add(sortHint, BorderLayout.EAST);
        shell.add(header, BorderLayout.NORTH);

        songModel = new BittuneSongTableModel(visibleSongs);
        songTable = new JTable(songModel);
        styleTable(songTable);
        songTable.getColumnModel().getColumn(5).setCellRenderer(new BittuneLikeRenderer());
        javax.swing.table.TableColumn numberColumn = songTable.getColumnModel().getColumn(0);
        numberColumn.setMinWidth(56);
        numberColumn.setPreferredWidth(58);
        numberColumn.setMaxWidth(72);
        songTable.getColumnModel().getColumn(1).setPreferredWidth(290);
        songTable.getColumnModel().getColumn(2).setPreferredWidth(210);
        songTable.getColumnModel().getColumn(3).setPreferredWidth(250);
        songTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        songTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        // Hover satır vurgulama
        int[] hoveredRow = {-1};
        songTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                int r = songTable.rowAtPoint(e.getPoint());
                if (r != hoveredRow[0]) { hoveredRow[0] = r; songTable.repaint(); }
            }
        });
        songTable.addMouseListener(new MouseAdapter() {
            public void mouseExited(MouseEvent e) { hoveredRow[0] = -1; songTable.repaint(); }
            public void mousePressed(MouseEvent e) { maybeShowPopup(e); }
            public void mouseReleased(MouseEvent e) { maybeShowPopup(e); }
            private void maybeShowPopup(MouseEvent e) {
                if (!e.isPopupTrigger()) return;
                int row = songTable.rowAtPoint(e.getPoint());
                if (row < 0) return;
                songTable.setRowSelectionInterval(row, row);
                int modelRow = songTable.convertRowIndexToModel(row);
                showPlaylistPopup(visibleSongs.get(modelRow), e.getX(), e.getY());
            }
            public void mouseClicked(MouseEvent e) {
                if (e.isPopupTrigger()) return;
                int row = songTable.rowAtPoint(e.getPoint());
                int col = songTable.columnAtPoint(e.getPoint());
                if (row < 0) return;
                int modelRow = songTable.convertRowIndexToModel(row);
                BittuneSong BittuneSong = visibleSongs.get(modelRow);
                if (col == 5) {
                    toggleLike(BittuneSong);
                } else if (e.getClickCount() == 2) {
                    queueViewSkip.clear(); // Kullanıcı şarkı seçti — skip listesini sıfırla
                    updateNowPlaying(BittuneSong);
                }
            }
        });
        // Hover'ı renderer'a aktar
        songTable.putClientProperty("hoveredRow", hoveredRow);

        songTable.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = songTable.columnAtPoint(e.getPoint());
                sortByHeaderColumn(column);
            }
        });

        JScrollPane scroll = new JScrollPane(songTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_TABLE);
        scroll.setBackground(BG_TABLE);
        scroll.getVerticalScrollBar().setUI(new DarkScrollBarUI());
        shell.add(scroll, BorderLayout.CENTER);
        return shell;
    }

    private JComponent buildRightPanel() {
        GradientPanel right = new GradientPanel(new Color(0x191225), BG_RIGHT, 0);
        right.setPreferredSize(new Dimension(255, 0));
        right.setLayout(new BorderLayout());
        right.setBorder(new EmptyBorder(22, 22, 18, 22));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(Box.createVerticalStrut(34));

        JLabel queueTitle = label("Sıradakiler", new Font("SansSerif", Font.BOLD, 24), TEXT);
        queueTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        body.add(queueTitle);
        body.add(Box.createVerticalStrut(4));

        JLabel queueHint = label("Sıradaki şarkılar", FONT_SMALL, TEXT_MUTED);
        queueHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        body.add(queueHint);
        body.add(Box.createVerticalStrut(24));

        JPanel upHeader = new JPanel(new BorderLayout());
        upHeader.setOpaque(false);
        upHeader.setMaximumSize(new Dimension(250, 22));
        upHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        upHeader.add(sectionCaption("SIRADA"), BorderLayout.WEST);
        JLabel clear = label("Temizle", FONT_SMALL, TEXT_MUTED);
        clear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clear.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                core.clearQueue();                   // DSA: Queue temizle (FIFO)
                queueViewSkip.addAll(upNextSongs()); // paneli görsel olarak temizle
                refreshUpNext();
            }
        });
        upHeader.add(clear, BorderLayout.EAST);
        body.add(upHeader);
        body.add(Box.createVerticalStrut(12));

        upNextList = new JPanel();
        upNextList.setOpaque(false);
        upNextList.setLayout(new BoxLayout(upNextList, BoxLayout.Y_AXIS));
        JScrollPane queueScroll = new JScrollPane(upNextList);
        queueScroll.setOpaque(false);
        queueScroll.getViewport().setOpaque(false);
        queueScroll.setBorder(BorderFactory.createEmptyBorder());
        queueScroll.getVerticalScrollBar().setUI(new DarkScrollBarUI());
        queueScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        queueScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        queueScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        queueScroll.setPreferredSize(new Dimension(210, 0));
        queueScroll.setMaximumSize(new Dimension(210, Integer.MAX_VALUE));
        body.add(queueScroll);
        right.add(body, BorderLayout.CENTER);
        refreshUpNext();
        return right;
    }

    private JComponent buildBottomPlayer() {
        JPanel outerShell = new JPanel(new BorderLayout());
        outerShell.setOpaque(false);
        outerShell.setPreferredSize(new Dimension(0, 120));
        outerShell.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x1A2640)));

        GlassPanel bottom = new GlassPanel(new Color(0x090E18), 0, 0.98f);
        bottom.setLayout(new BorderLayout());
        bottom.setBorder(new EmptyBorder(10, 24, 4, 24));

        GradientPanel songInfo = new GradientPanel(new Color(0x171D27), new Color(0x111722), 18);
        songInfo.setLayout(new BorderLayout(14, 0));
        songInfo.setPreferredSize(new Dimension(340, 64));
        songInfo.setBorder(new EmptyBorder(6, 10, 6, 14));
        bottomArt = new AlbumArtPanel(currentSong, 8, false);
        bottomArt.setPreferredSize(new Dimension(48, 48));
        songInfo.add(bottomArt, BorderLayout.WEST);
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        JPanel titleStack = new JPanel();
        titleStack.setOpaque(false);
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        bottomTitle = label("Midnight Drive", FONT_BOLD, TEXT);
        bottomArtist = label("The Neon Riders", FONT_SMALL, TEXT_MUTED);
        bottomDuration = label("", FONT_SMALL, new Color(0x5B6478));
        JPanel titleLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleLine.setOpaque(false);
        titleLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLine.add(bottomTitle);
        bottomLikeButton = new HeartButton();
        bottomLikeButton.setPreferredSize(new Dimension(36, 32));
        bottomLikeButton.setToolTipText("Beğenilen şarkılara ekle");
        bottomLikeButton.addActionListener(e -> toggleCurrentLike());
        titleLine.add(bottomLikeButton);
        titleStack.add(titleLine);
        JPanel artistRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        artistRow.setOpaque(false);
        artistRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        artistRow.add(bottomArtist);
        artistRow.add(label("·", FONT_SMALL, new Color(0x3A4255)));
        artistRow.add(bottomDuration);
        titleStack.add(Box.createVerticalStrut(2));
        titleStack.add(artistRow);
        textPanel.add(titleStack);
        songInfo.add(textPanel, BorderLayout.CENTER);
        bottom.add(songInfo, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 2));
        controls.setOpaque(false);

        TransportButton previous = new TransportButton(false);
        previous.setPreferredSize(new Dimension(38, 38));
        previous.setToolTipText("Önceki");
        previous.addActionListener(e -> playPreviousSong());
        controls.add(previous);

        // Play düğmesi — daha büyük + glow efekti
        bottomPlayButton = new RoundedButton("▶", PURPLE, PURPLE_LIGHT, 56) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color glowC = new Color(109, 40, 217, 55);
                for (int i = 3; i >= 1; i--) {
                    g2.setColor(new Color(glowC.getRed(), glowC.getGreen(), glowC.getBlue(), 55 / i));
                    g2.fillOval(-i * 2, -i * 2, getWidth() + i * 4, getHeight() + i * 4);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bottomPlayButton.setPreferredSize(new Dimension(56, 56));
        bottomPlayButton.addActionListener(e -> togglePlayPause());
        controls.add(bottomPlayButton);

        TransportButton next = new TransportButton(true);
        next.setPreferredSize(new Dimension(38, 38));
        next.setToolTipText("Sonraki");
        next.addActionListener(e -> playNextSong(true));
        controls.add(next);

        bottom.add(controls, BorderLayout.CENTER);

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        tools.setOpaque(false);
        tools.setPreferredSize(new Dimension(270, 0));
        muteButton = new SpeakerButton();
        muteButton.addActionListener(e -> toggleMute());
        tools.add(muteButton);
        bottomVolumeSlider = slider(70);
        bottomVolumeSlider.setPreferredSize(new Dimension(130, 26));
        tools.add(bottomVolumeSlider);
        bottomVolumePercent = label("70%", FONT_SMALL, TEXT_MUTED);
        bottomVolumePercent.setPreferredSize(new Dimension(34, 26));
        tools.add(bottomVolumePercent);
        bottom.add(tools, BorderLayout.EAST);

        // Ilerleme cubugu
        JPanel progressPanel = new JPanel(new BorderLayout(8, 0));
        progressPanel.setOpaque(false);
        progressPanel.setBorder(new EmptyBorder(0, 22, 7, 22));
        progressElapsed = label("0:00", FONT_SMALL, TEXT_MUTED);
        progressTotal   = label("0:00", FONT_SMALL, TEXT_MUTED);
        progressSlider = slider(0);
        progressSlider.setMaximum(1000);
        progressSlider.setValue(0);
        progressPanel.add(progressElapsed, BorderLayout.WEST);
        progressPanel.add(progressSlider, BorderLayout.CENTER);
        progressPanel.add(progressTotal, BorderLayout.EAST);

        // Müzik çubuğuna tıklayınca / sürükleyince tam o pozisyona atla
        MouseAdapter seekAdapter = new MouseAdapter() {
            private void seek(MouseEvent e) {
                if (currentSong == null) return;
                int val = ((PurpleSliderUI) progressSlider.getUI()).getValueForPosition(e.getX());
                val = Math.max(0, Math.min(1000, val));
                progressSlider.setValue(val);
                elapsedSeconds = (int) Math.round((val / 1000.0) * currentSong.durationSeconds);
                progressElapsed.setText(fmtTime(elapsedSeconds));
                // Gercek ses seek — DSA SeekHistoryStack'e kaydedilir
                if (realAudioActive) audioPlayer.seek(val / 1000.0);
            }
            public void mousePressed(MouseEvent e)  { seek(e); }
            public void mouseDragged(MouseEvent e)  { seek(e); }
        };
        progressSlider.addMouseListener(seekAdapter);
        progressSlider.addMouseMotionListener(seekAdapter);

        // playbackTimer: sadece gercek ses OLMAYAN sarkiler icin calisir
        playbackTimer = new javax.swing.Timer(1000, e -> {
            if (!playing || currentSong == null || progressSlider == null) return;
            if (realAudioActive) return; // gercek ses kendi progress'ini yonetiyor
            elapsedSeconds = Math.min(elapsedSeconds + 1, currentSong.durationSeconds);
            int pos = (int)((elapsedSeconds / (double) currentSong.durationSeconds) * 1000);
            progressSlider.setValue(pos);
            progressElapsed.setText(fmtTime(elapsedSeconds));
            if (elapsedSeconds >= currentSong.durationSeconds) playNextSong();
        });
        playbackTimer.start();

        outerShell.add(bottom, BorderLayout.CENTER);
        outerShell.add(progressPanel, BorderLayout.SOUTH);

        bottomVolumeSlider.addChangeListener(e -> syncVolume(bottomVolumeSlider.getValue()));
        return outerShell;
    }

    private String fmtTime(int secs) {
        return secs / 60 + ":" + String.format("%02d", secs % 60);
    }

    private void loadData() {
        // audio/ klasorunu tara — ID3 tag'lardan otomatik yukle
        String audioDir = System.getProperty("user.dir")
                + java.io.File.separator + "audio" + java.io.File.separator;
        ArrayList<BittuneSong> loaded = BittuneLibrary.loadFromDirectory(audioDir);

        if (loaded.isEmpty()) {
            System.out.println("[loadData] audio/ klasoru bos veya bulunamadi. Demo sarkilar yukleniyor.");
            loaded.addAll(demoSongs());
        }

        songs.addAll(loaded);
        topSongPool.addAll(songs);
        visibleSongs.addAll(songs);
        playContext = new ArrayList<>(visibleSongs);

        // Ilk sarkiyi current olarak sec (varsa)
        currentSong = songs.isEmpty() ? null : songs.get(0);
    }

    private ArrayList<BittuneSong> demoSongs() {
        ArrayList<BittuneSong> demo = new ArrayList<>();
        demo.add(new BittuneSong("Midnight Drive", "The Neon Riders", "Neon Nights", 220, 42,
                new Color(0x111827), new Color(0xD946EF), true));
        demo.add(new BittuneSong("Blinding Lights", "The Weeknd", "After Hours", 200, 128,
                new Color(0xF59E0B), new Color(0x7F1D1D), true));
        demo.add(new BittuneSong("Heat Waves", "Glass Animals", "Dreamland", 238, 96,
                new Color(0x60A5FA), new Color(0xA855F7), false));
        demo.add(new BittuneSong("Photograph", "Ed Sheeran", "x (Deluxe Edition)", 259, 84,
                new Color(0x7F1D1D), new Color(0xF97316), false));
        demo.add(new BittuneSong("Someone You Loved", "Lewis Capaldi", "Divinely Uninspired", 182, 78,
                new Color(0x111827), new Color(0x64748B), false));
        demo.add(new BittuneSong("Seven (feat. Latto)", "Jung Kook", "Seven", 184, 144,
                new Color(0x111111), new Color(0x71717A), true));
        demo.add(new BittuneSong("Calm Down", "Rema", "Calm Down", 209, 101,
                new Color(0xD946EF), new Color(0x4C1D95), false));
        demo.add(new BittuneSong("Believer", "Imagine Dragons", "Evolve", 204, 91,
                new Color(0x0891B2), new Color(0x111827), false));
        demo.add(new BittuneSong("Perfect", "Ed Sheeran", "Divide", 263, 75,
                new Color(0x38BDF8), new Color(0x0F172A), false));
        return demo;
    }

    /**
     * Tüm DSA yapılarını songs listesinden besler.
     * applyUserLikes()'tan sonra çağrılmalı (beğeni durumu priority'yi etkiler).
     */
    /**
     * DSA motorunu ve kontrolcüleri başlatır.
     * applyUserLikes()'tan SONRA çağrılmalı (beğeni MaxHeap önceliğini etkiler).
     *
     * GUI bu metottan sonra hiçbir DSA yapısına doğrudan erişmez.
     * Tüm DSA işlemleri engine / filterCtrl / playback üzerinden gider.
     */
    private void buildIndex() {
        // BittuneCore — tek sınıfta tüm DSA yapıları ve algoritmalar
        // HashMap + BST + Trie + MaxHeap + Queue + Stack + Sort + Search
        // Detay: BittuneCore.java
        core = new BittuneCore(songs, visibleSongs, playlists);
        core.buildIndex(songs);
    }

    private void updateNowPlaying(BittuneSong BittuneSong) {
        if (BittuneSong == null) return;
        capturePlayContextIfNeeded(BittuneSong);
        // DSA: Stack — mevcut şarkıyı geçmiş yığınına it (← Önceki tuşu için)
        if (currentSong != null && currentSong != BittuneSong) {
            core.pushHistory(currentSong);
        }
        setNowPlaying(BittuneSong);
    }

    private void capturePlayContextIfNeeded(BittuneSong song) {
        if (visibleSongs.contains(song)) {
            ArrayList<BittuneSong> newCtx = new ArrayList<>(visibleSongs);
            // Bağlam gerçekten değiştiyse (farklı şarkı seti) skip listesini sıfırla
            if (!newCtx.equals(playContext)) {
                queueViewSkip.clear();
            }
            playContext     = newCtx;
            playContextName = baseSectionTitle();
            if (core != null) {
                core.playContext     = playContext;
                core.playContextName = playContextName;
            }
        }
    }

    /** Kullanici secimi (cift tik, next/prev) — sesi baslatir. */
    private void setNowPlaying(BittuneSong BittuneSong) {
        loadSongUI(BittuneSong, true);
        startAudio(BittuneSong);
    }

    /** Sadece UI'yi yukler, ses baslatmaz (ilk acilis icin). */
    private void setNowPlaying(BittuneSong BittuneSong, boolean countPlay) {
        loadSongUI(BittuneSong, countPlay);
        // countPlay=false ise ilk acilis — ses baslatma
        if (countPlay) startAudio(BittuneSong);
    }

    /** UI gunceller: baslik, sure, slider, like butonu. */
    private void loadSongUI(BittuneSong BittuneSong, boolean countPlay) {
        currentSong = BittuneSong;
        currentSongCounted = countPlay; // true → çift tık/next/prev, false → sadece görüntüleme
        if (countPlay) BittuneSong.playCount++;
        bottomTitle.setText(BittuneSong.title);
        bottomArtist.setText(BittuneSong.artist);
        if (bottomDuration != null) bottomDuration.setText(BittuneSong.durationText());
        bottomArt.setSong(BittuneSong);
        updateBottomLikeButton();
        refreshTopRepeatedCards();
        elapsedSeconds = 0;
        if (progressSlider  != null) progressSlider.setValue(0);
        if (progressElapsed != null) progressElapsed.setText("0:00");
        if (progressTotal   != null) progressTotal.setText(BittuneSong.durationText());
        if (!countPlay && bottomPlayButton != null) bottomPlayButton.setText("▶");
        refreshUpNext();
    }

    /** Sarkinin MP3 dosyasi varsa gercek ses baslatir, yoksa simule eder. */
    private void startAudio(BittuneSong song) {
        audioPlayer.stop();
        playing = true;
        realAudioActive = false;
        if (bottomPlayButton != null) bottomPlayButton.setText("⏸");

        if (song.hasAudio()) {
            // Gercek MP3 oynatma
            audioPlayer.setVolume(muted ? 0 : bottomVolumeSlider.getValue());
            realAudioActive = true;
            audioPlayer.play(
                song.filePath,
                song.durationSeconds,
                progress -> {
                    // Progress callback — EDT'de gelir
                    if (currentSong != song) return;
                    if (progressSlider != null)  progressSlider.setValue((int)(progress * 1000));
                    elapsedSeconds = (int)(progress * song.durationSeconds);
                    if (progressElapsed != null) progressElapsed.setText(fmtTime(elapsedSeconds));
                },
                () -> {
                    // Tamamlandi — EDT'de gelir
                    if (currentSong == song) playNextSong();
                },
                error -> {
                    if (currentSong != song) return;
                    realAudioActive = false;
                    System.err.println("[BittuneAudioPlayer] Simulasyon moduna gecildi: " + error);
                }
            );
        }
        // Dosyasiz sarkilar icin mevcut playbackTimer devam eder
    }

    private void refreshTopRepeatedCards() {
        if (topRepeatedCardsPanel == null) return;
        topRepeatedCardsPanel.removeAll();
        ArrayList<BittuneSong> top = topRepeatedSongs();
        for (int i = 0; i < Math.min(6, top.size()); i++) {
            topRepeatedCardsPanel.add(new SongCardPanel(top.get(i), this::updateNowPlaying));
        }
        topRepeatedCardsPanel.revalidate();
        topRepeatedCardsPanel.repaint();
    }

    /**
     * En çok çalınan top-6 şarkıyı döndürür.
     * DSAEngine.topN() → MaxHeap ile anlık playCount'a göre O(n log 6).
     * GUI doğrudan MaxHeap ile çalışmaz; engine üzerinden sorgular.
     */
    private ArrayList<BittuneSong> topRepeatedSongs() {
        return core.topN(songs, 6);
    }

    private void playNextSong() { playNextSong(false); }

    private void playNextSong(boolean userInitiated) {
        // DSA: Queue — önce kuyruğa bak (FIFO, O(1))
        BittuneSong next = core.dequeueOrNull();

        // Kuyruk boşsa aktif bağlamdan devam et
        if (next == null) next = nextFromPlayContext();

        // Sırada şarkı yok — dur
        if (next == null) {
            stopPlayback();
            if (userInitiated) {
                showToast("Kuyruk boş — sırada başka şarkı yok.");
            }
            return;
        }

        updateNowPlaying(next);
        refreshUpNext();
    }

    /** Oynatmayi durdurur, UI'yi "durduruldu" moduna alir. */
    private void stopPlayback() {
        audioPlayer.stop();
        realAudioActive = false;
        playing = false;
        if (bottomPlayButton != null) bottomPlayButton.setText("▶");
        if (progressSlider   != null) progressSlider.setValue(0);
        if (progressElapsed  != null) progressElapsed.setText("0:00");
        elapsedSeconds = 0;
    }

    private boolean shouldFollowCurrentContext() {
        return playContext != null
                && !playContext.isEmpty()
                && !"Tüm Şarkılar".equals(playContextName);
    }

    private BittuneSong nextFromPlayContext() {
        ArrayList<BittuneSong> source = playContext == null || playContext.isEmpty() ? songs : playContext;
        if (source.isEmpty()) return null;
        int index = source.indexOf(currentSong);
        if (index < 0) {
            index = songs.indexOf(currentSong);
            source = songs;
            if (source.isEmpty()) return null;
        }
        // Sona gelince dur — hiçbir bağlamda başa dönme (wrap-around yok)
        for (int nextIdx = index + 1; nextIdx < source.size(); nextIdx++) {
            BittuneSong candidate = source.get(nextIdx);
            if (!queueViewSkip.contains(candidate)) return candidate;
        }
        return null;
    }

    private BittuneSong previousFromPlayContext() {
        ArrayList<BittuneSong> source = playContext == null || playContext.isEmpty() ? songs : playContext;
        if (source.isEmpty()) return null;
        int index = source.indexOf(currentSong);
        if (index < 0) {
            index = songs.indexOf(currentSong);
            source = songs;
            if (source.isEmpty()) return null;
        }
        return source.get((index - 1 + source.size()) % source.size());
    }

    private void playPreviousSong() {
        // DSA: Stack — geçmiş yığınından önceki şarkıyı al (LIFO, O(1))
        BittuneSong previous = core.popHistory();
        if (previous == null) {
            previous = previousFromPlayContext();
        }
        if (previous == null) {
            showInfoDialog("Önceki Şarkı", "Çalınacak önceki şarkı yok.");
        } else {
            setNowPlaying(previous);
        }
    }

    private void playRandomSong() {
        if (songs.isEmpty()) return;
        BittuneSong randomSong = songs.get(new Random().nextInt(songs.size()));
        updateNowPlaying(randomSong);
    }

    private void repeatCurrentSong() {
        if (currentSong == null) return;
        setNowPlaying(currentSong);
        showInfoDialog("Tekrar", currentSong.title + " yeniden başlatıldı.");
    }

    private void togglePlayPause() {
        playing = !playing;
        bottomPlayButton.setText(playing ? "\u23F8" : "\u25B6");
        if (currentSong == null) return;

        // \u0130lk play t\u0131klamas\u0131nda (hen\u00FCz say\u0131lmam\u0131\u015Fsa) playCount art\u0131r
        if (playing && !currentSongCounted) {
            currentSong.playCount++;
            currentSongCounted = true;          // tekrar say\u0131lmas\u0131n
            core.updateEntry(currentSong);      // HashMap + BST g\u00FCncelle
            refreshTopRepeatedCards();
            songModel.fireTableDataChanged();
        }

        if (realAudioActive) {
            // Ses zaten \u00E7al\u0131yor \u2014 sadece devam ettir / durdur
            if (playing) audioPlayer.resume();
            else         audioPlayer.pause();
        } else if (playing && currentSong.hasAudio()) {
            // \u0130lk t\u0131klamada ses hi\u00E7 ba\u015Flat\u0131lmam\u0131\u015F: \u015Fimdi ba\u015Flat
            startAudio(currentSong);
        }
        // Demo \u015Fark\u0131 (filePath yok): timer zaten tickliyor, sim\u00FClasyon devam eder
    }

    private void startPlayback() {
        playing = true;
        if (bottomPlayButton != null) {
            bottomPlayButton.setText("\u23F8");
        }
    }

    private void toggleSelectedLike() {
        BittuneSong BittuneSong = selectedSong();
        if (BittuneSong == null) {
            showToast("Önce bir şarkı seç.");
            return;
        }
        toggleLike(BittuneSong);
    }

    private void addSelectedToQueue() {
        BittuneSong BittuneSong = selectedSong();
        if (BittuneSong == null) {
            showToast("Önce bir şarkı seç.");
            return;
        }
        queueViewSkip.remove(BittuneSong); // Skip listesinden çıkar — panelde görünsün
        core.enqueue(BittuneSong);         // DSA: Queue FIFO ekle
        refreshUpNext();
        showToast(BittuneSong.title + " sıraya eklendi.");
    }

    private void showPlaylistPopup(BittuneSong song, int x, int y) {
        final int PW = 282; // popup genislik

        JWindow popup = new JWindow(this);
        popup.setBackground(new Color(0, 0, 0, 0));

        // Custom-painted arka plan paneli
        JPanel wrap = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Golge
                g2.setColor(new Color(0, 0, 0, 65));
                g2.fillRoundRect(4, 4, getWidth() - 4, getHeight() - 4, 18, 18);
                // Arka plan gradient
                g2.setPaint(new GradientPaint(0, 0, new Color(0x16202E), 0, getHeight(), new Color(0x0C1118)));
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 18, 18);
                // Cerceve
                g2.setColor(new Color(0x243348));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 18, 18);
                g2.dispose();
            }
        };
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBorder(new EmptyBorder(8, 0, 8, 5)); // sag+alt golge boslugu

        // --- Sarki baslik alani ---
        JPanel songHeader = new JPanel();
        songHeader.setLayout(new BoxLayout(songHeader, BoxLayout.X_AXIS));
        songHeader.setOpaque(false);
        songHeader.setBorder(new EmptyBorder(6, 14, 10, 14));
        songHeader.setMaximumSize(new Dimension(PW, 58));

        AlbumArtPanel miniArt = new AlbumArtPanel(song, 7, false);
        miniArt.setPreferredSize(new Dimension(40, 40));
        miniArt.setMinimumSize(new Dimension(40, 40));
        miniArt.setMaximumSize(new Dimension(40, 40));
        songHeader.add(miniArt);
        songHeader.add(Box.createHorizontalStrut(12));

        JPanel titleStack = new JPanel();
        titleStack.setOpaque(false);
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        JLabel titleLbl = new JLabel(song.title.length() > 27 ? song.title.substring(0, 26) + "…" : song.title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        titleLbl.setForeground(new Color(0xF1F5F9));
        JLabel artistLbl = new JLabel(song.artist);
        artistLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        artistLbl.setForeground(new Color(0x6B8099));
        titleStack.add(titleLbl);
        titleStack.add(Box.createVerticalStrut(3));
        titleStack.add(artistLbl);
        songHeader.add(titleStack);
        wrap.add(songHeader);

        // Ayrac
        wrap.add(buildPopupDivider(PW));

        // --- Aksiyonlar ---
        wrap.add(buildPopupRow(popup, PW, "▶", "Hemen Cal",
                new Color(0xA3CFFF), false, () -> updateNowPlaying(song)));

        boolean liked = song.liked;
        String likeLabel = liked ? "Begenıyi Kaldır" : "Begenilenlere Ekle";
        String likeIcon  = liked ? "♡" : "♥";
        Color  likeClr   = liked ? new Color(0x6B7280) : new Color(0xC084FC);
        wrap.add(buildPopupRow(popup, PW, likeIcon, likeLabel, likeClr, false, () -> toggleLike(song)));

        wrap.add(buildPopupRow(popup, PW, "≡", "Sıraya Ekle",
                new Color(0x94A3B8), false, () -> {
            queueViewSkip.remove(song); // Skip listesinden çıkar — panelde görünsün
            core.enqueue(song);         // DSA: Queue FIFO
            refreshUpNext();
            showToast(song.title + " sıraya eklendi");
        }));

        // --- Playlist'ten kaldır (sadece playlist görüntülenirken) ---
        if (playlists.toArrayList().contains(playContextName)) {
            wrap.add(buildPopupDivider(PW));
            wrap.add(buildPopupRow(popup, PW, "✕", "Playlist'ten Kaldır",
                    new Color(0xF87171), false, () -> {
                boolean removed = playlists.removeSong(playContextName, song);
                if (removed) {
                    if (currentUser != null) BittuneStorage.saveUser(currentUser, playlists);
                    showPlaylistSongs(playContextName); // tabloyu yenile
                    showToast("\"" + song.title + "\" listeden kaldırıldı");
                }
            }));
        }

        // --- Playlist bolumu ---
        wrap.add(buildPopupDivider(PW));
        wrap.add(buildPopupSectionLabel("PLAYLIST'E EKLE", PW));

        ArrayList<String> playlistNames = playlists.toArrayList();
        for (String plName : playlistNames) {
            wrap.add(buildPopupRow(popup, PW, "+", plName,
                    new Color(0xC4B5FD), false, () -> {
                // Duplicate kontrolü — şarkı zaten bu playlist'te mi?
                if (playlists.songsOf(plName).contains(song)) {
                    showToast("\"" + song.title + "\" zaten \"" + plName + "\" listesinde var");
                    return;
                }
                playlists.addSong(plName, song);
                if (currentUser != null) BittuneStorage.saveUser(currentUser, playlists);
                showToast("\"" + plName + "\" listesine eklendi");
            }));
        }

        wrap.add(buildPopupRow(popup, PW, "+", "Yeni Playlist Olustur",
                new Color(0x7C3AED), true, () -> {
            String name = showTextInputDialog("Yeni Playlist", "Playlist adı");
            if (name != null && !name.trim().isEmpty()) {
                if (playlists.add(name.trim())) {
                    playlists.addSong(name.trim(), song);
                    if (currentUser != null) BittuneStorage.saveUser(currentUser, playlists);
                    refreshSidebarPlaylists(playlistArea);
                    showToast("\"" + name.trim() + "\" oluşturuldu");
                } else {
                    showToast("Bu isimde playlist zaten var");
                }
            }
        }));

        popup.setContentPane(wrap);
        popup.pack();

        // Ekrandan tasma kontrolu
        Point screenPt = songTable.getLocationOnScreen();
        int px = screenPt.x + x;
        int py = screenPt.y + y;
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        if (px + popup.getWidth()  > screen.width)  px = screen.width  - popup.getWidth()  - 10;
        if (py + popup.getHeight() > screen.height - 60) py = py - popup.getHeight();
        popup.setLocation(px, py);
        popup.setVisible(true);

        // Popup dışına tıklanınca kapat (global AWTEventListener)
        java.awt.event.AWTEventListener closer = new java.awt.event.AWTEventListener() {
            public void eventDispatched(java.awt.AWTEvent event) {
                if (event instanceof MouseEvent) {
                    MouseEvent me = (MouseEvent) event;
                    if (me.getID() == MouseEvent.MOUSE_PRESSED) {
                        Point pt = me.getLocationOnScreen();
                        if (!popup.getBounds().contains(pt)) {
                            popup.dispose();
                            java.awt.Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                        }
                    }
                }
            }
        };
        java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(closer, java.awt.AWTEvent.MOUSE_EVENT_MASK);

        // Popup kapanınca listener'ı temizle
        popup.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent e) {
                java.awt.Toolkit.getDefaultToolkit().removeAWTEventListener(closer);
            }
        });

        // Escape tuşuyla kapat
        wrap.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "closePopup");
        wrap.getActionMap().put("closePopup", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { popup.dispose(); }
        });
        popup.setFocusableWindowState(true);
        popup.requestFocus();
    }

    private JPanel buildPopupRow(JWindow popup, int width, String icon, String label,
                                  Color baseColor, boolean bold, Runnable action) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(width, 38));
        row.setPreferredSize(new Dimension(width, 38));
        row.setBorder(new EmptyBorder(0, 14, 0, 14));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 13));
        iconLbl.setForeground(baseColor);
        iconLbl.setPreferredSize(new Dimension(22, 22));
        iconLbl.setMinimumSize(new Dimension(22, 22));
        iconLbl.setMaximumSize(new Dimension(22, 22));

        // Metin: uzun playlist adlarını truncate et, max genislik = row - icon - padding
        int textMaxW = width - 14 - 22 - 10 - 14 - 5;
        String displayLabel = label;
        if (label.length() > 26) displayLabel = label.substring(0, 25) + "…";
        JLabel textLbl = new JLabel(displayLabel);
        textLbl.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 13));
        textLbl.setForeground(baseColor);
        textLbl.setMaximumSize(new Dimension(textMaxW, 22));

        row.add(iconLbl);
        row.add(Box.createHorizontalStrut(10));
        row.add(textLbl);
        row.add(Box.createHorizontalGlue());

        row.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                row.setOpaque(true);
                row.setBackground(new Color(0x1C2E45));
                iconLbl.setForeground(new Color(0xA78BFA));
                textLbl.setForeground(new Color(0xF1F5F9));
                row.repaint();
            }
            public void mouseExited(MouseEvent e) {
                row.setOpaque(false);
                iconLbl.setForeground(baseColor);
                textLbl.setForeground(baseColor);
                row.repaint();
            }
            public void mousePressed(MouseEvent e) {
                popup.dispose();
                action.run();
            }
        });
        return row;
    }

    private JPanel buildPopupDivider(int width) {
        JPanel div = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(0x1E2D42));
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        div.setOpaque(false);
        div.setPreferredSize(new Dimension(width, 7));
        div.setMaximumSize(new Dimension(width, 7));
        return div;
    }

    private JLabel buildPopupSectionLabel(String text, int width) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(new Color(0x3D5470));
        lbl.setBorder(new EmptyBorder(4, 16, 4, 0));
        lbl.setMaximumSize(new Dimension(width, 26));
        return lbl;
    }

    private void toggleCurrentLike() {
        if (currentSong == null) return;
        toggleLike(currentSong);
    }

    private void updateBottomLikeButton() {
        if (bottomLikeButton == null || currentSong == null) return;
        bottomLikeButton.setLiked(currentSong.liked);
    }

    private BittuneSong selectedSong() {
        int row = songTable.getSelectedRow();
        if (row < 0) return null;
        return visibleSongs.get(songTable.convertRowIndexToModel(row));
    }

    /**
     * Arama işlemi — çok katmanlı DSA stratejisi:
     * 1. BittuneCore.search() → Trie ön ek O(k), sonra Linear fallback O(n)
     * 2. İlk tam eşleşme için core.findExact() → HashMap O(1) → BST O(log n) → Linear O(n)
     */
    /** Enter'a basınca sadece filtrele — şarkı başlatma. */
    private void searchOnly(String query) {
        String placeholder = "Şarkı, sanatçı veya albüm ara...";
        if (query == null || query.trim().isEmpty() || query.equals(placeholder)) {
            showAllSongs();
            return;
        }
        String sectionTitle = core.search(query);
        playContext     = core.playContext;
        playContextName = core.playContextName;
        songSectionTitle.setText(sectionTitle);
        songModel.fireTableDataChanged();
        updateSectionPlayButton();
        if (visibleSongs.isEmpty()) {
            showInfoDialog("Arama", "\"" + query.trim() + "\" için eşleşen şarkı bulunamadı.");
            return;
        }
        // İlk eşleşen satırı seç ama çalma
        songTable.setRowSelectionInterval(0, 0);
        songTable.scrollRectToVisible(songTable.getCellRect(0, 0, true));
    }

    private void searchAndPlay(String query) {
        String placeholder = "Şarkı, sanatçı veya albüm ara...";
        if (query == null || query.trim().isEmpty() || query.equals(placeholder)) {
            showAllSongs();
            return;
        }
        // core: Trie + Linear ile visibleSongs'u günceller
        String sectionTitle = core.search(query);
        playContext     = core.playContext;
        playContextName = core.playContextName;
        songSectionTitle.setText(sectionTitle);
        songModel.fireTableDataChanged();
        updateSectionPlayButton();

        if (visibleSongs.isEmpty()) {
            showInfoDialog("Arama", "\"" + query.trim() + "\" için eşleşen şarkı bulunamadı.");
            return;
        }
        // Tam eşleşme: HashMap O(1) → BST O(log n) → Linear O(n)
        BittuneSong exact = core.findExact(query.trim());
        if (exact == null) exact = visibleSongs.get(0);

        int row = visibleSongs.indexOf(exact);
        if (row >= 0) {
            songTable.setRowSelectionInterval(row, row);
            songTable.scrollRectToVisible(songTable.getCellRect(row, 0, true));
        }
        updateNowPlaying(exact);
    }

    private void showAllSongs() {
        String title = core.showAll();
        playContext     = core.playContext;
        playContextName = core.playContextName;
        songSectionTitle.setText(title);
        songModel.fireTableDataChanged();
        updateSectionPlayButton();
        if (!visibleSongs.isEmpty()) songTable.setRowSelectionInterval(0, 0);
    }

    private void showLikedSongs() {
        String title = core.showLiked();
        playContext     = core.playContext;
        playContextName = core.playContextName;
        songSectionTitle.setText(title);
        songModel.fireTableDataChanged();
        updateSectionPlayButton();
        if (visibleSongs.isEmpty()) {
            showInfoDialog("Beğenilen Şarkılar", "Henüz beğenilen şarkın yok.");
        } else {
            songTable.setRowSelectionInterval(0, 0);
        }
    }

    /** Genel amaçlı filtre — popup/popup sonrası için hâlâ kullanılabilir. */
    private void filterSongs(String title, SongFilter filter) {
        visibleSongs.clear();
        for (BittuneSong s : songs) if (filter.accept(s)) visibleSongs.add(s);
        songSectionTitle.setText(title);
        songModel.fireTableDataChanged();
        updateSectionPlayButton();
        if (!visibleSongs.isEmpty()) songTable.setRowSelectionInterval(0, 0);
    }

    private void showPlaylistSongs(String playlistName) {
        core.showPlaylist(playlistName);
        playContext     = core.playContext;
        playContextName = core.playContextName;
        songSectionTitle.setText(playlistName);
        songModel.fireTableDataChanged();
        updateSectionPlayButton();
        if (visibleSongs.isEmpty()) {
            showInfoDialog("Playlist Boş",
                "\"" + playlistName + "\" içinde henüz şarkı yok.\n" +
                "Şarkı eklemek için tablodaki menü tuşunu kullan.");
        } else {
            songTable.setRowSelectionInterval(0, 0);
        }
    }

    private void playPlaylist(String playlistName) {
        ArrayList<BittuneSong> playlistSongs = playlists.songsOf(playlistName);
        if (playlistSongs.isEmpty()) {
            showInfoDialog("Playlist Boş", "\"" + playlistName + "\" içinde henüz şarkı yok.\nÖnce tablodaki menüden şarkı ekle.");
            return;
        }
        queueViewSkip.clear(); // Kullanıcı yeni bağlam başlatıyor — skip listesini sıfırla
        visibleSongs.clear();
        visibleSongs.addAll(playlistSongs);
        songSectionTitle.setText(playlistName);
        songModel.fireTableDataChanged();
        playContext = new ArrayList<>(playlistSongs);
        playContextName = playlistName;
        updateSectionPlayButton();
        songTable.setRowSelectionInterval(0, 0);
        updateNowPlaying(playlistSongs.get(0));
        startPlayback();
    }

    private void playCurrentPlaylistSection() {
        if (visibleSongs.isEmpty()) {
            showInfoDialog("Liste Boş", "Bu listede çalınacak şarkı yok.");
            return;
        }

        queueViewSkip.clear(); // Kullanıcı yeni oynatma başlatıyor — skip listesini sıfırla

        if (playContextName != null && playlists.toArrayList().contains(playContextName)) {
            playPlaylist(playContextName);
            return;
        }

        playContext = new ArrayList<>(visibleSongs);
        playContextName = baseSectionTitle();
        if (core != null) {
            core.playContext = playContext;
            core.playContextName = playContextName;
        }
        songTable.setRowSelectionInterval(0, 0);
        updateNowPlaying(visibleSongs.get(0));
        startPlayback();
    }

    private void updateSectionPlayButton() {
        if (sectionPlayButton == null) return;
        boolean hasSongs = !visibleSongs.isEmpty();
        sectionPlayButton.setVisible(hasSongs);
        sectionPlayButton.setEnabled(hasSongs);
        sectionPlayButton.setToolTipText(visibleSongs.isEmpty()
                ? "Bu listede henüz şarkı yok"
                : baseSectionTitle() + " listesini başlat");
    }

    /**
     * Sütun başlığına tıklanınca sıralama yapar.
     * Algoritma seçimi BittuneCore.sortColumn() üzerinden yapılır:
     *   Başlık  → Merge Sort O(n log n)
     *   Sanatçı → Selection Sort O(n²)
     *   Albüm   → Selection Sort O(n²)
     *   Süre    → Insertion Sort O(n²)
     *   Beğeni  → Selection Sort O(n²)
     */
    private void sortByHeaderColumn(int column) {
        String newTitle = core.sortColumn(column, baseSectionTitle());
        // Durum senkronizasyonu (yön bilgisi core'da tutuluyor)
        lastSortColumn = core.lastSortColumn;
        sortAscending  = core.sortAscending;
        songSectionTitle.setText(newTitle);
        songModel.fireTableDataChanged();
    }

    private String baseSectionTitle() {
        String title = songSectionTitle.getText();
        int suffix = title.indexOf("  /  ");
        return suffix >= 0 ? title.substring(0, suffix) : title;
    }

    private boolean matches(BittuneSong song, String query) {
        // Türkçe locale ile normalize — filterCtrl ile tutarlı
        String needle = BittuneSearchTrie.normalize(query);
        return BittuneSearchTrie.normalize(song.title).contains(needle)
            || BittuneSearchTrie.normalize(song.artist).contains(needle)
            || BittuneSearchTrie.normalize(song.album).contains(needle);
    }

    private void handleSidebarAction(String action) {
        switch (action) {
            case "Ana Sayfa" -> showAllSongs();
            case "Ara" -> {
                // Search field'ı temizle ve direkt focus ver
                searchField.setText("");
                searchField.setForeground(TEXT);
                showAllSongs();
                searchField.requestFocusInWindow();
            }
            case "Çalma Listeleri" -> showPlaylistDialog();
            case "Beğenilenler" -> showLikedSongs();
            case "Sıra" -> showQueueView();
            case "Profil" -> showProfileDialog();
            case "Ayarlar" -> showSettingsDialog();
            default -> showAllSongs();
        }
    }

    private void showPlaylistDialog() {
        JDialog dialog = new JDialog(this, "Çalma Listeleri", true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        Color BG = new Color(0x12101E);
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG);
        root.setOpaque(true);
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("ESCAPE"), "close");
        root.getActionMap().put("close", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { dialog.dispose(); }
        });

        // ── Başlık ────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout(0, 0));
        header.setBackground(new Color(0x1A1630));
        header.setOpaque(true);
        header.setBorder(new EmptyBorder(14, 20, 14, 16));
        JLabel titleLbl = label("Çalma Listeleri", new Font("SansSerif", Font.BOLD, 15), TEXT);
        header.add(titleLbl, BorderLayout.CENTER);

        JPanel closeBtn = new JPanel() {
            boolean hov = false;
            { setOpaque(false); setPreferredSize(new Dimension(28, 28));
              setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
              addMouseListener(new MouseAdapter() {
                  public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                  public void mouseExited (MouseEvent e) { hov = false; repaint(); }
                  public void mouseClicked(MouseEvent e) { dialog.dispose(); }
              });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(hov ? TEXT : new Color(0x64748B));
                g2.drawLine(8, 8, 20, 20); g2.drawLine(20, 8, 8, 20);
                g2.dispose();
            }
        };
        header.add(closeBtn, BorderLayout.EAST);

        JPanel div = new JPanel(); div.setBackground(new Color(0x2A1E40));
        div.setPreferredSize(new Dimension(0, 1)); div.setOpaque(true);
        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(header, BorderLayout.CENTER);
        north.add(div, BorderLayout.SOUTH);
        root.add(north, BorderLayout.NORTH);

        // ── İçerik: yenilenebilir playlist listesi ────────────────
        Runnable[] refresh = {null};
        refresh[0] = () -> {
            JPanel body = new JPanel();
            body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
            body.setBackground(BG);
            body.setOpaque(true);
            body.setBorder(new EmptyBorder(12, 16, 12, 16));

            ArrayList<String> list = playlists.toArrayList();
            if (list.isEmpty()) {
                JLabel empty = label("Henüz playlist yok.", FONT_BODY, TEXT_MUTED);
                empty.setAlignmentX(CENTER_ALIGNMENT);
                body.add(Box.createVerticalStrut(20));
                body.add(empty);
            } else {
                for (String name : list) {
                    int songCount = playlists.songsOf(name).size();
                    JPanel row = new JPanel(new BorderLayout(10, 0));
                    row.setOpaque(true);
                    row.setBackground(new Color(0x1D1538));
                    row.setBorder(new EmptyBorder(10, 14, 10, 10));
                    row.setMaximumSize(new Dimension(Short.MAX_VALUE, 52));
                    row.setPreferredSize(new Dimension(380, 52));
                    row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    // Sol: ♫ ikonlu kutu — sabit 34x34, dikey ortalı sarmalayıcıda
                    JPanel iconBox = new JPanel(new BorderLayout());
                    iconBox.setBackground(PURPLE);
                    iconBox.setOpaque(true);
                    iconBox.setPreferredSize(new Dimension(34, 34));
                    iconBox.setMinimumSize(new Dimension(34, 34));
                    iconBox.setMaximumSize(new Dimension(34, 34));
                    JLabel ico = new JLabel("♫", SwingConstants.CENTER);
                    ico.setForeground(TEXT);
                    ico.setFont(new Font("Segoe UI Symbol", Font.BOLD, 14));
                    iconBox.add(ico);

                    JPanel iconWrap = new JPanel();
                    iconWrap.setOpaque(false);
                    iconWrap.setLayout(new BoxLayout(iconWrap, BoxLayout.Y_AXIS));
                    iconWrap.add(Box.createVerticalGlue());
                    iconWrap.add(iconBox);
                    iconWrap.add(Box.createVerticalGlue());

                    // Orta: yalnızca playlist adı
                    JPanel txt = new JPanel();
                    txt.setOpaque(false);
                    txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
                    JLabel nameLbl = label(name, new Font("SansSerif", Font.BOLD, 13), TEXT);
                    nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                    txt.add(Box.createVerticalGlue());
                    txt.add(nameLbl);
                    txt.add(Box.createVerticalGlue());

                    // Sağ: × sil butonu
                    JPanel delBtn = new JPanel() {
                        boolean hov = false;
                        { setOpaque(true); setBackground(new Color(0x3B1F52));
                          setPreferredSize(new Dimension(26, 26));
                          setMinimumSize(new Dimension(26, 26));
                          setMaximumSize(new Dimension(26, 26));
                          setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                          addMouseListener(new MouseAdapter() {
                              public void mouseEntered(MouseEvent e) { hov = true;  setBackground(new Color(0x7F1D1D)); repaint(); }
                              public void mouseExited (MouseEvent e) { hov = false; setBackground(new Color(0x3B1F52)); repaint(); }
                              public void mouseClicked(MouseEvent e) {
                                  e.consume();
                                  // Onay dialogu
                                  JDialog confirm = new JDialog(dialog, "", true);
                                  confirm.setUndecorated(true);
                                  confirm.setBackground(new Color(0,0,0,0));
                                  JPanel cp = new JPanel(new BorderLayout(0, 0));
                                  cp.setBackground(new Color(0x1E1535));
                                  cp.setOpaque(true);
                                  cp.setBorder(BorderFactory.createCompoundBorder(
                                      BorderFactory.createLineBorder(new Color(0x3D2060), 1),
                                      new EmptyBorder(20, 24, 16, 24)));
                                  JLabel msg = label("\"" + name + "\" silinecek. Emin misiniz?",
                                      new Font("SansSerif", Font.BOLD, 13), TEXT);
                                  cp.add(msg, BorderLayout.CENTER);
                                  JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
                                  btns.setOpaque(false);
                                  btns.setBorder(new EmptyBorder(14, 0, 0, 0));
                                  RoundedButton hayir = new RoundedButton("Hayır", new Color(0x1E1535), new Color(0x2D1F4A), 10);
                                  hayir.setForeground(TEXT_MUTED);
                                  hayir.setPreferredSize(new Dimension(80, 32));
                                  hayir.addActionListener(ae -> confirm.dispose());
                                  RoundedButton evet = new RoundedButton("Evet", new Color(0x7F1D1D), new Color(0xB91C1C), 10);
                                  evet.setForeground(new Color(0xFCA5A5));
                                  evet.setPreferredSize(new Dimension(80, 32));
                                  evet.addActionListener(ae -> {
                                      confirm.dispose();
                                      playlists.remove(name);
                                      if (currentUser != null) BittuneStorage.saveUser(currentUser, playlists);
                                      refreshSidebarPlaylists(playlistArea);
                                      showToast("\"" + name + "\" silindi");
                                      refresh[0].run();
                                  });
                                  btns.add(hayir); btns.add(evet);
                                  cp.add(btns, BorderLayout.SOUTH);
                                  confirm.setContentPane(cp);
                                  confirm.pack();
                                  confirm.setLocationRelativeTo(dialog);
                                  confirm.setVisible(true);
                              }
                          });
                        }
                        @Override protected void paintComponent(Graphics g) {
                            super.paintComponent(g);
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                            g2.setColor(hov ? new Color(0xFCA5A5) : new Color(0xC084FC));
                            int p = 7;
                            g2.drawLine(p, p, getWidth()-p, getHeight()-p);
                            g2.drawLine(getWidth()-p, p, p, getHeight()-p);
                            g2.dispose();
                        }
                    };

                    // Sağ panel: [N şarkı] [boşluk] [× butonu] — dikey ortalı
                    JLabel cntLbl = label(songCount + " şarkı", FONT_SMALL, TEXT_MUTED);

                    JPanel delWrap = new JPanel();
                    delWrap.setOpaque(false);
                    delWrap.setLayout(new BoxLayout(delWrap, BoxLayout.Y_AXIS));
                    delWrap.add(Box.createVerticalGlue());
                    delWrap.add(delBtn);
                    delWrap.add(Box.createVerticalGlue());

                    JPanel eastPanel = new JPanel();
                    eastPanel.setOpaque(false);
                    eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.X_AXIS));
                    eastPanel.add(cntLbl);
                    eastPanel.add(Box.createHorizontalStrut(10));
                    eastPanel.add(delWrap);

                    row.add(iconWrap, BorderLayout.WEST);
                    row.add(txt, BorderLayout.CENTER);
                    row.add(eastPanel, BorderLayout.EAST);
                    row.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) { showPlaylistSongs(name); dialog.dispose(); }
                        public void mouseEntered(MouseEvent e) { row.setBackground(new Color(0x271C44)); }
                        public void mouseExited (MouseEvent e) { row.setBackground(new Color(0x1D1538)); }
                    });

                    body.add(row);
                    body.add(Box.createVerticalStrut(6));
                }
            }

            // Scroll pane
            JScrollPane sc = new JScrollPane(body);
            sc.getViewport().setBackground(BG);
            sc.getViewport().setOpaque(true);
            sc.setBorder(BorderFactory.createEmptyBorder());
            sc.getVerticalScrollBar().setUI(new DarkScrollBarUI());
            sc.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
            sc.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            // Scroll pane'i ortaya koy
            if (root.getComponentCount() > 1) root.remove(1);
            root.add(sc, BorderLayout.CENTER);
            root.revalidate(); root.repaint();
        };
        refresh[0].run();

        dialog.setContentPane(root);
        ArrayList<String> pl = playlists.toArrayList();
        int h = Math.max(320, Math.min(100 + pl.size() * 70, 520));
        showDialog(dialog, 450, h);
    }

    /**
     * "Sıra" sidebar öğesi — popup dialog olarak sıradaki şarkıları gösterir.
     * Ana tablo değişmez. Kuyruk şarkıları çöp simgesiyle kaldırılabilir.
     */
    private void showQueueView() {
        // Non-modal: ana pencere dialog açıkken paint edilebilir → anlık güncelleme
        JDialog dialog = new JDialog(this, "Sıradakiler", false);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        // ── Kök panel ─────────────────────────────────────────────
        Color BG = new Color(0x12101E);
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG);
        root.setOpaque(true);
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("ESCAPE"), "close");
        root.getActionMap().put("close",
            new javax.swing.AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) { dialog.dispose(); }
            });

        // ── Başlık ────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout(0, 0));
        header.setBackground(new Color(0x1A1630));
        header.setOpaque(true);
        header.setBorder(new EmptyBorder(16, 20, 16, 16));

        JPanel titleCol = new JPanel();
        titleCol.setOpaque(false);
        titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));
        JLabel titleLbl = label("Sıradakiler", new Font("SansSerif", Font.BOLD, 16), TEXT);
        JLabel subLbl   = label("", new Font("SansSerif", Font.PLAIN, 11), new Color(0x6B7280));
        titleCol.add(titleLbl);
        titleCol.add(Box.createVerticalStrut(2));
        titleCol.add(subLbl);
        header.add(titleCol, BorderLayout.CENTER);

        // Kapat — custom çizimli, karakter sorunu yok
        JPanel closePnl = new JPanel() {
            boolean hover = false;
            { setOpaque(false); setPreferredSize(new Dimension(30, 30));
              setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
              addMouseListener(new MouseAdapter() {
                  public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                  public void mouseExited (MouseEvent e) { hover = false; repaint(); }
                  public void mouseClicked(MouseEvent e) { dialog.dispose(); }
              });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hover) { g2.setColor(new Color(0x2D1F44)); g2.fillRoundRect(2,2,26,26,8,8); }
                g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(hover ? new Color(0xE2E8F0) : new Color(0x64748B));
                int m = 9;
                g2.drawLine(m, m, 30-m, 30-m);
                g2.drawLine(30-m, m, m, 30-m);
                g2.dispose();
            }
        };
        header.add(closePnl, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // ── Ayraç ─────────────────────────────────────────────────
        JPanel div = new JPanel(); div.setBackground(new Color(0x2A1E40));
        div.setPreferredSize(new Dimension(0, 1)); div.setOpaque(true);
        root.add(div, BorderLayout.NORTH); // temp; gerçek ekleme aşağıda

        JPanel northAll = new JPanel(new BorderLayout());
        northAll.setOpaque(false);
        northAll.add(header, BorderLayout.CENTER);
        northAll.add(div, BorderLayout.SOUTH);
        root.add(northAll, BorderLayout.NORTH);

        // ── İçerik alanı — selfRef ile kendi kendini yenileyebilir ─
        JScrollPane[] scrollRef = {null};
        Runnable[] selfRef = {null};
        selfRef[0] = () -> {
            // Panel her zaman noWrap=true: çalınmış şarkılar tekrar görünmez
            ArrayList<BittuneSong> queued   = core.queueSnapshot();
            ArrayList<BittuneSong> upcoming = core.upNextSongs(currentSong, playContext, true);
            ArrayList<BittuneSong> rest     = new ArrayList<>();
            for (BittuneSong s : upcoming) if (!queued.contains(s) && !queueViewSkip.contains(s)) rest.add(s);

            int total = queued.size() + rest.size();
            subLbl.setText(total == 0       ? "Kuyruk boş"
                         : queued.isEmpty() ? rest.size() + " şarkı"
                         : queued.size() + " kuyrukta  ·  " + rest.size() + " sıradaki");

            // Body — opaque + aynı arka plan → scroll artifact yok
            JPanel body = new JPanel();
            body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
            body.setBackground(BG);
            body.setOpaque(true);
            body.setBorder(new EmptyBorder(12, 14, 14, 14));

            if (total == 0) {
                // Boş durum
                JPanel empty = new JPanel();
                empty.setOpaque(false);
                empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
                JLabel ico  = label("=", new Font("SansSerif", Font.BOLD, 36), new Color(0x2A1E42));
                JLabel msg  = label("Kuyruk bos", new Font("SansSerif", Font.BOLD, 13), new Color(0x4B5563));
                JLabel hint = label("Sarkiya sag tik  >  Siraya Ekle",
                                    new Font("SansSerif", Font.PLAIN, 11), new Color(0x374151));
                for (JLabel l : new JLabel[]{ico, msg, hint}) l.setAlignmentX(CENTER_ALIGNMENT);
                empty.add(Box.createVerticalStrut(36));
                empty.add(ico);   empty.add(Box.createVerticalStrut(10));
                empty.add(msg);   empty.add(Box.createVerticalStrut(4));
                empty.add(hint);
                body.add(empty);
            } else {
                // KUYRUK bölümü
                if (!queued.isEmpty()) {
                    body.add(queueSectionHeader("KUYRUK  (FIFO)", new Color(0x7C3AED)));
                    body.add(Box.createVerticalStrut(6));
                    for (BittuneSong s : queued) {
                        Runnable onRm = () -> { core.removeFromQueue(s); refreshUpNext(); selfRef[0].run(); };
                        body.add(queueDialogRow(s, true, onRm, BG));
                        body.add(Box.createVerticalStrut(4));
                    }
                }
                // SIRADAKILER bölümü
                if (!rest.isEmpty()) {
                    if (!queued.isEmpty()) body.add(Box.createVerticalStrut(12));
                    body.add(queueSectionHeader("SIRADAKILER", new Color(0x475569)));
                    body.add(Box.createVerticalStrut(6));
                    for (BittuneSong s : rest) {
                        Runnable onRm = () -> { queueViewSkip.add(s); refreshUpNext(); selfRef[0].run(); };
                        body.add(queueDialogRow(s, false, onRm, BG));
                        body.add(Box.createVerticalStrut(4));
                    }
                }
            }

            // Scroll pane
            if (scrollRef[0] != null) root.remove(scrollRef[0]);
            JScrollPane sc = new JScrollPane(body);
            sc.setOpaque(false);
            sc.getViewport().setBackground(BG);
            sc.getViewport().setOpaque(true);
            sc.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
            sc.setBorder(BorderFactory.createEmptyBorder());
            sc.getVerticalScrollBar().setUI(new DarkScrollBarUI());
            sc.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
            sc.getVerticalScrollBar().setUnitIncrement(14);
            sc.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            sc.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scrollRef[0] = sc;
            root.add(sc, BorderLayout.CENTER);
            root.revalidate();
            root.repaint();
        };

        selfRef[0].run();

        dialog.setContentPane(root);
        int rows = core.queueSnapshot().size() + core.upNextSongs(currentSong, playContext).size();
        int h = rows == 0 ? 240 : Math.min(108 + rows * 56, 540);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent e) { refreshUpNext(); }
        });
        showDialog(dialog, 430, h);
    }

    /**
     * AlbumArtPanel'i off-screen BufferedImage'a çizer → JLabel ImageIcon olarak döner.
     * Scroll pane içinde non-opaque custom panel kullanmak artifact yaratır;
     * bu yöntem o sorunu tamamen ortadan kaldırır.
     */
    private ImageIcon renderArtToIcon(BittuneSong song, int size) {
        AlbumArtPanel tmp = new AlbumArtPanel(song, 5, false);
        tmp.setSize(size, size);
        java.awt.image.BufferedImage img =
            new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        tmp.paint(g2);
        g2.dispose();
        return new ImageIcon(img);
    }

    /** Bölüm başlığı — küçük caps + ince yatay çizgi. */
    private JPanel queueSectionHeader(String text, Color accent) {
        JPanel wrap = new JPanel(new BorderLayout(8, 0));
        wrap.setOpaque(false);
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        JLabel lbl = label(text, new Font("SansSerif", Font.BOLD, 10), accent);
        wrap.add(lbl, BorderLayout.WEST);
        JPanel line = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40));
                g.fillRect(0, getHeight() / 2, getWidth(), 1);
            }
        };
        line.setOpaque(false);
        wrap.add(line, BorderLayout.CENTER);
        return wrap;
    }

    /**
     * Kuyruk dialog satırı.
     * Album kapağı BufferedImage'a render edilir → scroll artifact yok.
     * Sil butonu JLabel tabanlı → font bağımsız.
     *
     * @param onRemove null → sil butonu gösterilmez
     * @param bg       dialog arka plan rengi
     */
    private JPanel queueDialogRow(BittuneSong song, boolean fromQueue,
                                  Runnable onRemove, Color bg) {
        Color bgBase  = fromQueue ? new Color(0x1D1538) : new Color(0x161228);
        Color bgHover = fromQueue ? new Color(0x271C44) : new Color(0x1C1535);

        // Ana kart — BoxLayout yatay, sabit 52px, tam opaque
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.X_AXIS));
        card.setBackground(bgBase);
        card.setOpaque(true);
        card.setPreferredSize(new Dimension(380, 52));
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 52));
        card.setMinimumSize(new Dimension(100, 52));

        // Sol mor şerit
        if (fromQueue) {
            JPanel stripe = new JPanel();
            stripe.setBackground(new Color(0x7C3AED));
            stripe.setPreferredSize(new Dimension(3, 52));
            stripe.setMaximumSize(new Dimension(3, 52));
            stripe.setMinimumSize(new Dimension(3, 52));
            stripe.setOpaque(true);
            card.add(stripe);
        }

        card.add(Box.createHorizontalStrut(fromQueue ? 10 : 12));

        // Album kapağı — BufferedImage → JLabel (scroll artifact YOK)
        JLabel artLbl = new JLabel(renderArtToIcon(song, 36));
        artLbl.setPreferredSize(new Dimension(36, 36));
        artLbl.setMinimumSize(new Dimension(36, 36));
        artLbl.setMaximumSize(new Dimension(36, 36));
        card.add(artLbl);

        card.add(Box.createHorizontalStrut(10));

        // Başlık + Sanatçı (dikey)
        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel tLbl = label(song.title,  new Font("SansSerif", Font.BOLD, 12), TEXT);
        JLabel aLbl = label(song.artist, new Font("SansSerif", Font.PLAIN, 11), new Color(0x64748B));
        tLbl.setMaximumSize(new Dimension(200, 18));
        aLbl.setMaximumSize(new Dimension(200, 16));
        txt.add(Box.createVerticalGlue());
        txt.add(tLbl);
        txt.add(Box.createVerticalStrut(2));
        txt.add(aLbl);
        txt.add(Box.createVerticalGlue());
        card.add(txt);

        // Esnek boşluk
        card.add(Box.createHorizontalGlue());

        // Süre
        JLabel dur = label(song.durationText(), new Font("SansSerif", Font.PLAIN, 11),
                           fromQueue ? new Color(0xA78BFA) : new Color(0x475569));
        dur.setMinimumSize(dur.getPreferredSize());
        card.add(dur);

        // Sil butonu (sadece kuyruk öğelerinde)
        if (onRemove != null) {
            card.add(Box.createHorizontalStrut(10));
            JPanel rmBtn = new JPanel() {
                boolean hov = false;
                {
                    setOpaque(true);
                    setBackground(new Color(0x3B1F52));
                    setPreferredSize(new Dimension(26, 26));
                    setMinimumSize(new Dimension(26, 26));
                    setMaximumSize(new Dimension(26, 26));
                    setAlignmentY(CENTER_ALIGNMENT);
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    addMouseListener(new MouseAdapter() {
                        public void mouseEntered(MouseEvent e) { hov = true;  setBackground(new Color(0x7F1D1D)); repaint(); }
                        public void mouseExited (MouseEvent e) { hov = false; setBackground(new Color(0x3B1F52)); repaint(); }
                        public void mouseClicked(MouseEvent e) { onRemove.run(); }
                    });
                }
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(hov ? new Color(0xFCA5A5) : new Color(0xC084FC));
                    int pad = 7;
                    g2.drawLine(pad, pad, getWidth()-pad, getHeight()-pad);
                    g2.drawLine(getWidth()-pad, pad, pad, getHeight()-pad);
                    g2.dispose();
                }
            };
            card.add(rmBtn);
        }

        card.add(Box.createHorizontalStrut(12));

        // Hover
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { card.setBackground(bgHover); }
            public void mouseExited (MouseEvent e) { card.setBackground(bgBase);  }
        });

        return card;
    }

    private void showProfileDialog() {
        JDialog dialog = themedDialog("Profil");

        GradientPanel root = new GradientPanel(new Color(0x13101E), new Color(0x0B0E18), 0);
        root.setLayout(new BorderLayout());

        // ESC kapat
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("ESCAPE"), "close");
        root.getActionMap().put("close",
            new javax.swing.AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) { dialog.dispose(); }
            });

        // ++ Ust bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(14, 20, 10, 14));

        JLabel titleLbl = label("Profil", new Font("SansSerif", Font.BOLD, 16), TEXT);
        topBar.add(titleLbl, BorderLayout.WEST);

        JPanel closeBtn = new JPanel() {
            boolean hovered = false;
            {
                setPreferredSize(new Dimension(30, 30));
                setOpaque(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                    public void mouseClicked(MouseEvent e) { dialog.dispose(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? TEXT : TEXT_MUTED);
                g2.setStroke(new java.awt.BasicStroke(1.8f,
                    java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                int m = 8;
                g2.drawLine(m, m, getWidth()-m, getHeight()-m);
                g2.drawLine(getWidth()-m, m, m, getHeight()-m);
                g2.dispose();
            }
        };
        topBar.add(closeBtn, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);

        // ++ Merkez icerik
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(0, 20, 10, 20));

        // Avatar
        JPanel avatarCircle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(0x7C3AED), getWidth(), getHeight(), new Color(0xA21CAF)));
                g2.fillOval(0, 0, getWidth(), getHeight());
                String letter = userName == null || userName.isEmpty() ? "M"
                    : userName.substring(0, 1).toUpperCase(new java.util.Locale("tr", "TR"));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 22));
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(letter,
                    (getWidth() - fm.stringWidth(letter)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(64, 64); }
        };
        avatarCircle.setOpaque(false);
        avatarCircle.setMaximumSize(new Dimension(64, 64));
        avatarCircle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLbl = label(userName, new Font("SansSerif", Font.BOLD, 18), TEXT);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLbl = label(userStatus, FONT_SMALL, PURPLE_LIGHT);
        statusLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(Box.createVerticalStrut(4));
        center.add(avatarCircle);
        center.add(Box.createVerticalStrut(8));
        center.add(nameLbl);
        center.add(Box.createVerticalStrut(3));
        center.add(statusLbl);
        center.add(Box.createVerticalStrut(14));

        // Stat kartlari (3 yan yana)
        JPanel statsGrid = new JPanel(new GridLayout(1, 3, 10, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        Color[] statAccents = {new Color(0xEC4899), new Color(0x8B5CF6), new Color(0x0EA5E9)};
        String[] statCounts = {String.valueOf(likedSongCount()),
                               String.valueOf(playlists.toArrayList().size()),
                               String.valueOf(songs.size())};
        String[] statLabels = {"Begenilen", "Playlist", "Sarki"};
        for (int i = 0; i < 3; i++) {
            final Color acc = statAccents[i];
            final String cnt = statCounts[i];
            final String lbl = statLabels[i];
            JPanel card = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0x1A1035));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            card.setOpaque(false);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(new EmptyBorder(10, 14, 10, 14));
            JLabel valLbl = new JLabel(cnt);
            valLbl.setFont(new Font("SansSerif", Font.BOLD, 20));
            valLbl.setForeground(acc);
            JLabel labLbl = new JLabel(lbl);
            labLbl.setFont(FONT_SMALL);
            labLbl.setForeground(TEXT_MUTED);
            card.add(valLbl);
            card.add(Box.createVerticalStrut(2));
            card.add(labLbl);
            statsGrid.add(card);
        }
        center.add(statsGrid);
        center.add(Box.createVerticalStrut(14));

        // "Su an caliyor" satiri
        JPanel nowRow = new JPanel(new BorderLayout());
        nowRow.setOpaque(false);
        nowRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        nowRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        nowRow.add(label("Su an caliyor", FONT_SMALL, TEXT_MUTED), BorderLayout.WEST);
        String nowVal = currentSong != null ? currentSong.title + " - " + currentSong.artist : "-";
        nowRow.add(label(nowVal, FONT_BODY, TEXT), BorderLayout.EAST);
        center.add(nowRow);
        center.add(Box.createVerticalStrut(14));

        // Duzenleme alanlari
        JPanel editSection = new JPanel();
        editSection.setOpaque(false);
        editSection.setLayout(new BoxLayout(editSection, BoxLayout.Y_AXIS));
        editSection.setBorder(new EmptyBorder(8, 0, 0, 0));
        editSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField nameField   = themedTextField(userName);
        JTextField statusField = themedTextField(userStatus);

        JPanel nameRow2 = new JPanel();
        nameRow2.setOpaque(false);
        nameRow2.setLayout(new BoxLayout(nameRow2, BoxLayout.X_AXIS));
        nameRow2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        nameRow2.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel adLbl = label("Ad", FONT_SMALL, TEXT_MUTED);
        adLbl.setPreferredSize(new Dimension(60, 20));
        adLbl.setMinimumSize(new Dimension(60, 20));
        adLbl.setMaximumSize(new Dimension(60, 20));
        nameRow2.add(adLbl);
        nameRow2.add(Box.createHorizontalStrut(10));
        nameRow2.add(nameField);

        JPanel statusRow2 = new JPanel();
        statusRow2.setOpaque(false);
        statusRow2.setLayout(new BoxLayout(statusRow2, BoxLayout.X_AXIS));
        statusRow2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        statusRow2.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel durumLbl = label("Durum", FONT_SMALL, TEXT_MUTED);
        durumLbl.setPreferredSize(new Dimension(60, 20));
        durumLbl.setMinimumSize(new Dimension(60, 20));
        durumLbl.setMaximumSize(new Dimension(60, 20));
        statusRow2.add(durumLbl);
        statusRow2.add(Box.createHorizontalStrut(10));
        statusRow2.add(statusField);

        editSection.add(nameRow2);
        editSection.add(Box.createVerticalStrut(8));
        editSection.add(statusRow2);
        center.add(editSection);

        root.add(center, BorderLayout.CENTER);

        // ++ Alt buton cubugu
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x1E2736)),
            new EmptyBorder(12, 20, 14, 20)
        ));

        RoundedButton logoutBtn = new RoundedButton("Cikis Yap", new Color(0x3B0A0A), new Color(0x7F1D1D), 14);
        logoutBtn.setForeground(new Color(0xFCA5A5));
        logoutBtn.addActionListener(e -> { dialog.dispose(); doLogout(); });

        RoundedButton saveBtn = new RoundedButton("Kaydet", PURPLE, PURPLE_LIGHT, 14);
        saveBtn.addActionListener(e -> {
            if (!nameField.getText().trim().isEmpty())   userName   = nameField.getText().trim();
            if (!statusField.getText().trim().isEmpty()) userStatus = statusField.getText().trim();
            refreshUserLabels();
            if (currentUser != null) BittuneStorage.saveUser(currentUser, playlists);
            showToast("Kaydedildi");
            dialog.dispose();
        });

        bottomBar.add(logoutBtn, BorderLayout.WEST);
        bottomBar.add(saveBtn,   BorderLayout.EAST);
        root.add(bottomBar, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        showDialog(dialog, 460, 440);
    }

    // ++ Ayarlar Dialogu
    private void showSettingsDialog() {
        JDialog dialog = themedDialog("Ayarlar");

        GradientPanel root = new GradientPanel(new Color(0x13101E), new Color(0x0B0E18), 0);
        root.setLayout(new BorderLayout());

        // ESC kapat
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("ESCAPE"), "close");
        root.getActionMap().put("close",
            new javax.swing.AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) { dialog.dispose(); }
            });

        // ++ Ust bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(14, 20, 10, 14));

        JLabel titleLbl = label("Ayarlar", new Font("SansSerif", Font.BOLD, 16), TEXT);
        topBar.add(titleLbl, BorderLayout.WEST);

        JPanel closeBtn = new JPanel() {
            boolean hovered = false;
            {
                setPreferredSize(new Dimension(30, 30));
                setOpaque(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                    public void mouseClicked(MouseEvent e) { dialog.dispose(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? TEXT : TEXT_MUTED);
                g2.setStroke(new java.awt.BasicStroke(1.8f,
                    java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                int m = 8;
                g2.drawLine(m, m, getWidth()-m, getHeight()-m);
                g2.drawLine(getWidth()-m, m, m, getHeight()-m);
                g2.dispose();
            }
        };
        topBar.add(closeBtn, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);

        // ++ Icerik
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Ses Seviyesi bolumu
        JPanel volHeader = new JPanel(new BorderLayout());
        volHeader.setOpaque(false);
        volHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        volHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        volHeader.add(label("Ses Seviyesi", new Font("SansSerif", Font.BOLD, 13), TEXT), BorderLayout.WEST);
        int initVol = bottomVolumeSlider.getValue();
        JLabel volPct = label(initVol + "%", FONT_SMALL, TEXT_MUTED);
        volHeader.add(volPct, BorderLayout.EAST);
        content.add(volHeader);
        content.add(Box.createVerticalStrut(6));

        JSlider volSlider = slider(initVol);
        volSlider.setMinimum(0);
        volSlider.setMaximum(100);
        volSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        volSlider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        volSlider.addChangeListener(e -> {
            int val = volSlider.getValue();
            syncVolume(val);
            volPct.setText(val + "%");
        });
        content.add(volSlider);
        content.add(Box.createVerticalStrut(20));

        // Hesap bolumu
        JLabel hesapHeader = label("Hesap", new Font("SansSerif", Font.BOLD, 13), TEXT);
        hesapHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(hesapHeader);
        content.add(Box.createVerticalStrut(10));

        JPanel kulRow = new JPanel(new BorderLayout());
        kulRow.setOpaque(false);
        kulRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        kulRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        kulRow.add(label("Kullanici", FONT_SMALL, TEXT_MUTED), BorderLayout.WEST);
        kulRow.add(label(userName, FONT_BODY, TEXT), BorderLayout.EAST);
        content.add(kulRow);
        content.add(Box.createVerticalStrut(6));

        JPanel emailRow = new JPanel(new BorderLayout());
        emailRow.setOpaque(false);
        emailRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        emailRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailRow.add(label("E-posta", FONT_SMALL, TEXT_MUTED), BorderLayout.WEST);
        emailRow.add(label(userEmail, FONT_BODY, TEXT), BorderLayout.EAST);
        content.add(emailRow);

        root.add(content, BorderLayout.CENTER);

        // ++ Alt buton cubugu
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x1E2736)),
            new EmptyBorder(10, 20, 12, 20)
        ));

        RoundedButton closeDialogBtn = new RoundedButton("Kapat", new Color(0x1E1535), PURPLE_LIGHT, 14);
        closeDialogBtn.addActionListener(e -> dialog.dispose());
        bottomBar.add(closeDialogBtn, BorderLayout.EAST);
        root.add(bottomBar, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        showDialog(dialog, 420, 300);
    }

    private JTextField themedTextField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(FONT_BODY);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(0, 12, 0, 12));
        return field;
    }

    private String timeGreeting() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        if (hour >= 6  && hour < 12) return "Günaydın";
        if (hour >= 12 && hour < 18) return "İyi Günler";
        if (hour >= 18 && hour < 23) return "İyi Akşamlar";
        return "İyi Geceler";
    }

    private void refreshUserLabels() {
        if (accountNameLabel != null) accountNameLabel.setText(userName);
        if (accountAvatarLabel != null) accountAvatarLabel.setText(initialOf(userName));
        if (greetingLabel != null) greetingLabel.setText(timeGreeting() + ", " + userName);
    }

    /** Oturumu kapatir ve giris ekranina doner */
    private void doLogout() {
        if (currentUser != null) BittuneStorage.saveUser(currentUser, playlists, songs);
        BittuneStorage.clearSession();
        audioPlayer.stop();
        if (playbackTimer != null) playbackTimer.stop();
        dispose();
        SwingUtilities.invokeLater(() -> new BittuneLoginScreen(userStore, loginHistory));
    }

    private String initialOf(String value) {
        String cleaned = value == null || value.trim().isEmpty() ? "M" : value.trim();
        return cleaned.substring(0, 1).toUpperCase(new java.util.Locale("tr", "TR"));
    }

    private JComboBox<String> themedCombo(String[] values) {
        JComboBox<String> combo = new JComboBox<>(values);
        styleCombo(combo);
        combo.setPreferredSize(new Dimension(150, 34));
        return combo;
    }

    private JCheckBox themedCheckBox(String text, boolean selected) {
        JCheckBox box = new JCheckBox(text, selected);
        box.setOpaque(false);
        box.setForeground(TEXT_MUTED);
        box.setFont(FONT_SMALL);
        box.setFocusPainted(false);
        box.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return box;
    }

    /**
     * Kullanicinin kaydedilmis begeni listesini sarkilara uygular.
     * Her oturum acilisinda loadData()'dan sonra cagrilir.
     */
    private void applyUserLikes() {
        if (currentUser == null) return;
        long ts = 1;
        for (BittuneSong s : songs) {
            boolean liked = currentUser.hasLiked(s.title);
            s.liked  = liked;
            s.likedAt = liked ? ts++ : 0;          // ekleme sirasini koru
        }
    }

    /**
     * Merkezi like toggle metodu.
     * Hem sarki nesnesini hem kullanici profilini gunceller.
     */
    private void toggleLike(BittuneSong song) {
        song.liked  = !song.liked;
        song.likes += song.liked ? 1 : -1;
        song.likedAt = song.liked ? System.currentTimeMillis() : 0;
        // DSA: core.updateEntry → HashMap + BST güncelle (beğeni değişti)
        core.updateEntry(song);
        // DSA: kullanıcının ArrayList'ini güncelle
        if (currentUser != null) {
            currentUser.setLiked(song.title, song.liked);
            BittuneStorage.saveUser(currentUser, playlists);  // kalıcı kayıt
        }
        // UI guncelle
        if ("Beğenilenler".equals(songSectionTitle.getText())) showLikedSongs();
        else songModel.fireTableDataChanged();
        if (song == currentSong) updateBottomLikeButton();
        showToast(song.liked ? "♥  Beğenilenlere eklendi" : "♡  Beğenilenlerden çıkarıldı");
    }

    private int likedSongCount() {
        int count = 0;
        for (BittuneSong BittuneSong : songs) {
            if (BittuneSong.liked) count++;
        }
        return count;
    }

    private void toggleMute() {
        if (bottomVolumeSlider.getValue() == 0) {
            bottomVolumeSlider.setValue(lastVolumeBeforeMute);
            muted = false;
        } else {
            lastVolumeBeforeMute = bottomVolumeSlider.getValue();
            bottomVolumeSlider.setValue(0);
            muted = true;
        }
        if (muteButton != null) {
            muteButton.setMuted(muted);
        }
    }

    private void refreshUpNext() {
        if (upNextList == null) return;
        upNextList.removeAll();
        ArrayList<BittuneSong> nextSongs = upNextSongs();
        if (nextSongs.isEmpty()) {
            // Boş kuyruk — görsel empty state
            JPanel emptyState = new JPanel();
            emptyState.setOpaque(false);
            emptyState.setLayout(new BoxLayout(emptyState, BoxLayout.Y_AXIS));
            emptyState.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel icon = new JLabel("♫", SwingConstants.CENTER);
            icon.setFont(new Font("SansSerif", Font.PLAIN, 32));
            icon.setForeground(new Color(0x2A3850));
            icon.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel msg = label("Sıra boş", FONT_SMALL, new Color(0x3A5070));
            msg.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel hint = label("Şarkıya sağ tık → Sıraya ekle", new Font("SansSerif", Font.PLAIN, 11), new Color(0x293848));
            hint.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyState.add(Box.createVerticalStrut(20));
            emptyState.add(icon);
            emptyState.add(Box.createVerticalStrut(8));
            emptyState.add(msg);
            emptyState.add(Box.createVerticalStrut(4));
            emptyState.add(hint);
            upNextList.add(emptyState);
        } else {
            for (BittuneSong BittuneSong : nextSongs) {
                upNextList.add(queueRow(BittuneSong));
                upNextList.add(Box.createVerticalStrut(10));
            }
        }
        upNextList.revalidate();
        upNextList.repaint();
    }

    // DSA: upNextSongs
    // Kuyrukta şarkı varsa → sadece kuyruğu göster (context karışmaz)
    // Kuyruk boşsa       → context'teki sonraki şarkıları göster (noWrap)
    private ArrayList<BittuneSong> upNextSongs() {
        ArrayList<BittuneSong> queued = new ArrayList<>(core.queueSnapshot());
        if (!queued.isEmpty()) {
            if (queueViewSkip.isEmpty()) return queued;
            ArrayList<BittuneSong> f = new ArrayList<>();
            for (BittuneSong s : queued) if (!queueViewSkip.contains(s)) f.add(s);
            return f;
        }
        // Kuyruk boş — context şarkılarını göster
        ArrayList<BittuneSong> all = core.upNextSongs(currentSong, playContext, true);
        if (queueViewSkip.isEmpty()) return all;
        ArrayList<BittuneSong> filtered = new ArrayList<>();
        for (BittuneSong s : all) if (!queueViewSkip.contains(s)) filtered.add(s);
        return filtered;
    }

    private JComponent queueRow(BittuneSong BittuneSong) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(250, 44));
        row.setPreferredSize(new Dimension(250, 44));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        AlbumArtPanel mini = new AlbumArtPanel(BittuneSong, 6, false);
        mini.setPreferredSize(new Dimension(36, 36));
        row.add(mini, BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(label(BittuneSong.title, new Font("SansSerif", Font.BOLD, 12), TEXT));
        text.add(label(BittuneSong.artist, FONT_SMALL, TEXT_MUTED));
        row.add(text, BorderLayout.CENTER);
        row.add(label(BittuneSong.durationText(), FONT_SMALL, TEXT), BorderLayout.EAST);
        return row;
    }

    private void syncVolume(int value) {
        if (syncingVolume) return;
        syncingVolume = true;
        bottomVolumeSlider.setValue(value);
        bottomVolumePercent.setText(value + "%");
        muted = value == 0;
        if (value > 0) {
            lastVolumeBeforeMute = value;
        }
        if (muteButton != null) {
            muteButton.setMuted(muted);
        }
        audioPlayer.setVolume(value);
        syncingVolume = false;
    }

    private void showToast(String message) {
        boolean likedToast = message.contains("Beğenilen");
        boolean removeToast = message.contains("çıkarıldı");

        JWindow toast = new JWindow(this);
        toast.setBackground(new Color(0, 0, 0, 0));

        // Custom-painted panel — yuvarlatılmış köşeler + gradient
        JPanel wrap = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Dış gölge efekti (ince siyah katman)
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRoundRect(3, 4, getWidth() - 4, getHeight() - 2, 22, 22);
                // Ana gradient arka plan
                g2.setPaint(new GradientPaint(0, 0, new Color(0x1E1040), getWidth(), getHeight(), new Color(0x130C2A)));
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 22, 22);
                // İnce mor çerçeve
                g2.setColor(new Color(0x6D28D9, false));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 22, 22);
                g2.dispose();
            }
        };
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(0, 0, 4, 4)); // gölge boşluğu

        // İkon + metin yatay sıralı
        String icon = likedToast ? (removeToast ? "♡" : "♥") : "+";
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Symbol", Font.BOLD, 16));
        iconLbl.setForeground(likedToast
                ? (removeToast ? new Color(0x6B7280) : new Color(0xA78BFA))
                : new Color(0x67E8F9));
        iconLbl.setBorder(new EmptyBorder(11, 18, 11, 6));

        // Mesajdan ikon kısmını çıkar (zaten iconLbl ile gösteriyoruz)
        String text = message.replaceAll("^[♥♡+]\\s*", "").trim();
        JLabel textLbl = new JLabel(text);
        textLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textLbl.setForeground(new Color(0xDDD6FE));
        textLbl.setBorder(new EmptyBorder(11, 4, 11, 22));

        wrap.add(iconLbl, BorderLayout.WEST);
        wrap.add(textLbl, BorderLayout.CENTER);

        toast.setContentPane(wrap);
        toast.pack();

        // Pencerenin ortasına, alt player'ın hemen üstüne
        int wx = getLocationOnScreen().x + getWidth() / 2 - toast.getWidth() / 2;
        int wy = getLocationOnScreen().y + getHeight() - 150;
        toast.setLocation(wx, wy);
        toast.setVisible(true);

        new javax.swing.Timer(2200, e -> toast.dispose()) {{
            setRepeats(false);
            start();
        }};
    }

    private void showInfoDialog(String title, String message) {
        JDialog dialog = themedDialog(title);
        JPanel content = dialogContent(title, message);
        addDialogCloseButton(dialog, content, "Tamam");
        dialog.setContentPane(content);
        showDialog(dialog, 380, 210);
    }

    private void addDialogCloseButton(JDialog dialog, JPanel content, String text) {
        RoundedButton ok = new RoundedButton(text, PURPLE, PURPLE_LIGHT, 18);
        ok.setPreferredSize(new Dimension(92, 36));
        ok.addActionListener(e -> dialog.dispose());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(ok);
        content.add(actions, BorderLayout.SOUTH);
    }

    private String showTextInputDialog(String title, String placeholder) {
        JDialog dialog = themedDialog(title);
        JPanel content = dialogContent(title, "");

        JTextField input = new JTextField();
        input.setFont(FONT_BODY);
        input.setForeground(TEXT);
        input.setCaretColor(TEXT);
        input.setBackground(new Color(0x111827));
        input.setBorder(new EmptyBorder(0, 12, 0, 12));

        RoundedPanel fieldShell = new RoundedPanel(new Color(0x111827), 14);
        fieldShell.setLayout(new BorderLayout());
        fieldShell.setPreferredSize(new Dimension(320, 42));
        fieldShell.add(input, BorderLayout.CENTER);
        JLabel hint = label(placeholder, FONT_SMALL, TEXT_MUTED);

        JPanel middle = new JPanel(new BorderLayout(0, 8));
        middle.setOpaque(false);
        middle.add(hint, BorderLayout.NORTH);
        middle.add(fieldShell, BorderLayout.CENTER);
        content.add(middle, BorderLayout.CENTER);

        final String[] result = {null};
        RoundedButton cancel = new RoundedButton("İptal", new Color(0x202633), new Color(0x2A3342), 18);
        cancel.setPreferredSize(new Dimension(94, 36));
        cancel.addActionListener(e -> dialog.dispose());

        RoundedButton create = new RoundedButton("Oluştur", PURPLE, PURPLE_LIGHT, 18);
        create.setPreferredSize(new Dimension(94, 36));
        create.addActionListener(e -> {
            result[0] = input.getText().trim();
            dialog.dispose();
        });
        input.addActionListener(e -> create.doClick());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(cancel);
        actions.add(create);
        content.add(actions, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        SwingUtilities.invokeLater(input::requestFocusInWindow);
        showDialog(dialog, 390, 235);
        return result[0];
    }

    private JDialog themedDialog(String title) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        return dialog;
    }

    private JPanel dialogContent(String title, String message) {
        GradientPanel content = new GradientPanel(new Color(0x1C142A), new Color(0x101620), 22);
        content.setLayout(new BorderLayout(0, 18));
        content.setBorder(new EmptyBorder(22, 24, 20, 24));

        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);
        JLabel titleLabel = label(title, new Font("SansSerif", Font.BOLD, 19), TEXT);
        top.add(titleLabel, BorderLayout.WEST);
        DialogCloseButton close = new DialogCloseButton();
        close.setPreferredSize(new Dimension(34, 30));
        top.add(close, BorderLayout.EAST);
        content.add(top, BorderLayout.NORTH);

        JTextArea body = new JTextArea(message);
        body.setOpaque(false);
        body.setEditable(false);
        body.setFocusable(false);
        body.setLineWrap(true);
        body.setWrapStyleWord(true);
        body.setForeground(TEXT_MUTED);
        body.setFont(FONT_BODY);
        content.add(body, BorderLayout.CENTER);

        close.addActionListener(e -> SwingUtilities.getWindowAncestor(content).dispose());
        return content;
    }

    private void showDialog(JDialog dialog, int width, int height) {
        dialog.setSize(width, height);
        Rectangle owner = getBounds();
        int x = owner.x + (owner.width - width) / 2;
        int y = owner.y + (owner.height - height) / 2;
        dialog.setLocation(Math.max(0, x), Math.max(0, y));
        dialog.setVisible(true);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(48);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setBackground(BG_TABLE);
        table.setForeground(TEXT);
        table.setSelectionBackground(ROW_SELECTED);
        table.setSelectionForeground(TEXT);
        table.setFont(FONT_BODY);
        table.setFocusable(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0x0A1018));
        header.setForeground(TEXT_MUTED);
        header.setFont(new Font("SansSerif", Font.BOLD, 10));
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x1A2538)));
        header.setReorderingAllowed(false);
        header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, val, sel, foc, r, c);
                lbl.setOpaque(true);
                lbl.setBackground(new Color(0x0A1018));
                lbl.setForeground(new Color(0x5A7090));
                lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
                // Sütun başlığı: üst tracking efekti için letter-spacing yok ama ALL CAPS zaten var
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x1A2538)),
                    new EmptyBorder(0, 16, 0, 8)
                ));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                return lbl;
            }
        });

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean selected,
                                                           boolean focus, int row, int col) {
                super.getTableCellRendererComponent(tbl, value, selected, focus, row, col);
                int[] hovered = (int[]) tbl.getClientProperty("hoveredRow");
                boolean isHovered = hovered != null && hovered[0] == row;
                setOpaque(true);
                int leftPad = col == 0 ? 0 : 14;
                setBorder(new EmptyBorder(0, leftPad, 0, 8));
                if (selected) {
                    setBackground(ROW_SELECTED);
                    setForeground(col == 1 ? TEXT : TEXT_MUTED);
                } else if (isHovered) {
                    setBackground(new Color(0x16202E));   // belirgin hover
                    setForeground(col == 1 ? TEXT : new Color(0xB0C4D8));
                } else {
                    // Hafif zebra — çok ince fark
                    setBackground(row % 2 == 0 ? BG_TABLE : new Color(0x0E1520));
                    setForeground(col == 1 ? TEXT : TEXT_MUTED);
                }
                if (col == 1) {
                    setFont(new Font("SansSerif", Font.BOLD, 14));
                } else if (col == 0) {
                    setText(String.valueOf(value));
                    setFont(new Font("SansSerif", Font.PLAIN, 13));
                    setForeground(selected || isHovered ? TEXT_MUTED : new Color(0x5A6A7E));
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setFont(FONT_BODY);
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private void styleCombo(JComboBox<String> combo) {
        final Color CB_BG     = new Color(0x111827);
        final Color CB_BORDER = new Color(0x2D3748);
        final Color CB_SEL    = new Color(0x4C1D95);
        final Color CB_HOVER  = new Color(0x1A2236);

        combo.setPreferredSize(new Dimension(160, 36));
        combo.setForeground(TEXT);
        combo.setBackground(CB_BG);
        combo.setFont(FONT_BODY);
        combo.setFocusable(false);
        combo.setBorder(BorderFactory.createEmptyBorder());

        // Dropdown liste renderer \u2014 her sat\u0131ra \u00F6zel stil
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean hasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
                lbl.setFont(FONT_BODY);
                lbl.setForeground(TEXT);
                lbl.setBackground(isSelected ? CB_SEL : CB_BG);
                lbl.setBorder(new EmptyBorder(8, 14, 8, 14));
                lbl.setOpaque(true);
                return lbl;
            }
        });

        // Popup liste renkleri
        combo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                Object popup = combo.getUI().getAccessibleChild(combo, 0);
                if (popup instanceof javax.swing.plaf.basic.ComboPopup cp) {
                    JList<?> list = cp.getList();
                    list.setBackground(CB_BG);
                    list.setForeground(TEXT);
                    list.setSelectionBackground(CB_SEL);
                    list.setSelectionForeground(TEXT);
                    list.setFont(FONT_BODY);
                    // Scroll pane border
                    Container parent = list.getParent();
                    while (parent != null) {
                        if (parent instanceof JScrollPane sp) {
                            sp.setBorder(BorderFactory.createLineBorder(CB_BORDER, 1));
                            sp.getViewport().setBackground(CB_BG);
                            break;
                        }
                        parent = parent.getParent();
                    }
                }
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });

        // Tam \u00F6zel UI \u2014 g\u00F6vdeyi yuvarlak \u00E7izer, ok butonunu temizler
        combo.setUI(new BasicComboBoxUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean open = combo.isPopupVisible();
                // G\u00F6vde
                g2.setColor(open ? CB_HOVER : CB_BG);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                // Kenarl\u0131k \u2014 a\u00E7\u0131ksa mor, kapal\u0131ysa koyu gri
                g2.setColor(open ? PURPLE : CB_BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 10, 10);
                g2.dispose();
                super.paint(g, c);
            }

            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                        g2.setColor(CB_BG);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        // Ok \u00FC\u00E7geni \u2014 kalem ile \u00E7iz
                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;
                        int[] xp = {cx - 5, cx + 5, cx};
                        int[] yp = {cy - 2, cy - 2, cy + 3};
                        g2.setColor(TEXT_MUTED);
                        g2.fillPolygon(xp, yp, 3);
                        g2.dispose();
                    }
                };
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setPreferredSize(new Dimension(30, 10));
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return btn;
            }

            @Override
            public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
                // Windows L&F'nin renderer'ını tamamen bypass et — doğrudan metin çiz
                // Böylece seçili öğenin arka planı daima CB_BG olur
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                g2.setColor(CB_BG);
                g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                Object sel = combo.getSelectedItem();
                if (sel != null) {
                    g2.setFont(FONT_BODY);
                    g2.setColor(TEXT);
                    FontMetrics fm = g2.getFontMetrics();
                    int ty = bounds.y + (bounds.height + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(sel.toString(), bounds.x + 2, ty);
                }
                g2.dispose();
            }

            @Override
            protected void installDefaults() {
                super.installDefaults();
                comboBox.setBackground(CB_BG);
                comboBox.setForeground(TEXT);
                comboBox.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 4));
            }
        });
    }

    private JSlider slider(int value) {
        JSlider slider = new JSlider(0, 100, value);
        slider.setOpaque(false);
        slider.setForeground(PURPLE);
        slider.setBackground(new Color(0x2B2F3A));
        slider.setFocusable(false);
        PurpleSliderUI ui = new PurpleSliderUI(slider);
        slider.setUI(ui);
        // Tıklanan noktaya tam atla (varsayılan blok adımı yerine)
        slider.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                slider.setValue(ui.getValueForPosition(e.getX()));
            }
        });
        slider.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int v = ui.getValueForPosition(e.getX());
                v = Math.max(slider.getMinimum(), Math.min(slider.getMaximum(), v));
                slider.setValue(v);
            }
        });
        return slider;
    }

    private RoundedButton circleButton(String text, int size) {
        RoundedButton button = new RoundedButton(text, new Color(0x151C26), new Color(0x1E2A38), size);
        button.setPreferredSize(new Dimension(size, size));
        button.setFont(new Font("SansSerif", Font.PLAIN, 16));
        return button;
    }

    private RoundedButton iconButton(String text) {
        RoundedButton button = new RoundedButton(text, new Color(0, 0, 0, 0), new Color(0x202633), 34);
        button.setPreferredSize(new Dimension(36, 36));
        button.setForeground(TEXT);
        button.setFont(new Font("Segoe UI Symbol", Font.BOLD, 23));
        return button;
    }

    private JComponent flatIcon(String icon) {
        JLabel label = new JLabel(icon, SwingConstants.CENTER);
        label.setForeground(TEXT_MUTED);
        label.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 21));
        label.setPreferredSize(new Dimension(28, 28));
        return label;
    }

    private RoundedButton controlButton(String icon, String tooltip, Runnable action) {
        RoundedButton button = new RoundedButton(icon, new Color(0, 0, 0, 0), new Color(0x202633), 34);
        button.setPreferredSize(new Dimension(36, 36));
        button.setForeground(TEXT_MUTED);
        button.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 20));
        button.setToolTipText(tooltip);
        button.addActionListener(e -> action.run());
        return button;
    }

    private JComponent avatar(String text, int size) {
        RoundedPanel avatar = new RoundedPanel(PURPLE, size);
        avatar.setPreferredSize(new Dimension(size, size));
        avatar.setLayout(new BorderLayout());
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, 17));
        avatar.add(label);
        return avatar;
    }

    private JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private JLabel sectionCaption(String text) {
        JLabel label = label(text, new Font("SansSerif", Font.BOLD, 12), TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /** BoxLayout Y_AXIS için sol hizalı dikey boşluk — Box.createVerticalStrut CENTER döndürür */
    private static Component strut(int height) {
        Box.Filler f = new Box.Filler(
            new Dimension(0, height), new Dimension(0, height), new Dimension(Short.MAX_VALUE, height));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private JComponent withBottomGap(JComponent component, int gap) {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BorderLayout());
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);   // BoxLayout karışıklığını önler
        wrapper.setMaximumSize(new Dimension(190, component.getPreferredSize().height + gap));
        wrapper.add(component, BorderLayout.NORTH);
        wrapper.setBorder(new EmptyBorder(0, 0, gap, 0));
        return wrapper;
    }
}

