import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

// ============================================================
// BittuneLoginScreen — Giris / Kayit Ekrani
// DSA: BittuneUserStore (LinkedList) + BittuneLoginHistory (Stack)
// ============================================================
public class BittuneLoginScreen extends JFrame {

    // ---------- Renkler ----------
    private static final Color BG_LEFT      = new Color(0x0E0A22);
    private static final Color BG_RIGHT     = new Color(0x10131E);
    private static final Color PURPLE       = new Color(0x7C3AED);
    private static final Color PURPLE_LIGHT = new Color(0x9D6FEF);
    private static final Color PURPLE_GLOW  = new Color(0xC4B5FD);
    private static final Color MAGENTA      = new Color(0xC026D3);
    private static final Color TEXT         = new Color(0xEEF2FF);
    private static final Color TEXT_MUTED   = new Color(0x94A3B8);
    private static final Color BORDER       = new Color(0x252D3E);
    private static final Color FIELD_BG     = new Color(0x141B2E);
    private static final Color ERROR_COLOR  = new Color(0xFCA5A5);
    private static final Color SUCCESS_COLOR = new Color(0x6EE7B7);

    // ---------- Fontlar ----------
    private static final Font FONT_LOGO   = new Font("SansSerif", Font.BOLD, 28);
    private static final Font FONT_TITLE  = new Font("SansSerif", Font.BOLD, 22);
    private static final Font FONT_BODY   = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_SMALL  = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_BOLD   = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_LINK   = new Font("SansSerif", Font.PLAIN, 13);

    // ---------- DSA Yapilari ----------
    private final BittuneUserStore     userStore;
    private final BittuneLoginHistory  loginHistory;

    // ---------- UI referanslari ----------
    private CardLayout cardLayout;
    private JPanel     cardPanel;

    // Login panel
    private JTextField     loginUserField;
    private JPasswordField loginPassField;
    private JLabel         loginErrorLabel;
    private JCheckBox      rememberMeCheck;

    // Register panel
    private JTextField     regUserField;
    private JPasswordField regPassField;
    private JPasswordField regConfirmField;
    private JLabel         regErrorLabel;
    private JLabel         regSuccessLabel;

    // Pencere suruklemesi icin
    private Point dragOrigin;

    // ============================================================
    public BittuneLoginScreen(BittuneUserStore store, BittuneLoginHistory history) {
        this.userStore    = store;
        this.loginHistory = history;
        buildFrame();
        setVisible(true);
    }

    // ============================================================
    private void buildFrame() {
        setUndecorated(true);
        setSize(940, 630);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(new Color(0, 0, 0, 0));

        // Yuvarlatilmis koseler icin shape
        setShape(new RoundRectangle2D.Double(0, 0, 940, 630, 28, 28));

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                // Kose yuvarlama arka plani
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_RIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g2.dispose();
            }
        };
        root.setOpaque(false);

        root.add(buildLeftPanel(), BorderLayout.WEST);
        root.add(buildRightPanel(), BorderLayout.CENTER);

        setContentPane(root);
        installDrag(root);
    }

    // ============================================================
    // SOL PANEL — marka alani
    // ============================================================
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Temel arka plan (sol koseler yuvarlik, sag duz)
                GradientPaint base = new GradientPaint(
                    0, 0,              new Color(0x160C38),
                    getWidth(), getHeight(), new Color(0x0C1428)
                );
                g2.setPaint(base);
                g2.fillRoundRect(0, 0, getWidth() + 28, getHeight(), 28, 28);

                // Sol alt yumusak mor bulut
                RadialGradientPaint r1 = new RadialGradientPaint(
                    new Point(30, getHeight() - 40), 200,
                    new float[]{0f, 1f},
                    new Color[]{new Color(91, 33, 182, 70), new Color(12, 20, 40, 0)}
                );
                g2.setPaint(r1);
                g2.fillOval(-90, getHeight() - 240, 320, 320);

                // Sag ust yumusak camgobegi bulut
                RadialGradientPaint r2 = new RadialGradientPaint(
                    new Point(getWidth() - 10, 80), 170,
                    new float[]{0f, 1f},
                    new Color[]{new Color(14, 165, 233, 45), new Color(12, 20, 40, 0)}
                );
                g2.setPaint(r2);
                g2.fillOval(getWidth() - 180, -60, 300, 300);

                // Ortada ince bir mor parlama
                RadialGradientPaint r3 = new RadialGradientPaint(
                    new Point(getWidth() / 2, getHeight() / 2 - 20), 190,
                    new float[]{0f, 1f},
                    new Color[]{new Color(124, 58, 237, 38), new Color(12, 20, 40, 0)}
                );
                g2.setPaint(r3);
                g2.fillOval(getWidth() / 2 - 190, getHeight() / 2 - 210, 380, 380);

                // Sag kenara cok ince, yumusak bir ayrım çizgisi
                g2.setColor(new Color(124, 58, 237, 50));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(getWidth() - 1, 32, getWidth() - 1, getHeight() - 32);

                g2.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(370, 0));
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(64, 46, 52, 46));

        // Yumusak nota simgeli logo
        JPanel logoCircle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Dis yumusak parlama
                RadialGradientPaint glow = new RadialGradientPaint(
                    new Point(38, 38), 38,
                    new float[]{0f, 1f},
                    new Color[]{new Color(124, 58, 237, 90), new Color(124, 58, 237, 0)}
                );
                g2.setPaint(glow);
                g2.fillOval(-4, -4, 84, 84);
                // Ana daire — yumusak gradyan
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x8B5CF6), 76, 76, new Color(0xA855F7));
                g2.setPaint(gp);
                g2.fillOval(0, 0, 72, 72);
                // M harfi
                g2.setColor(new Color(0xEDE9FE));
                g2.setFont(new Font("SansSerif", Font.BOLD, 28));
                FontMetrics fm = g2.getFontMetrics();
                String m = "M";
                g2.drawString(m, (72 - fm.stringWidth(m)) / 2, (72 - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(80, 80); }
            @Override public Dimension getMaximumSize()   { return new Dimension(80, 80); }
        };
        logoCircle.setOpaque(false);
        logoCircle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(logoCircle);
        panel.add(Box.createVerticalStrut(22));

        JLabel title = new JLabel("Bittune");
        title.setForeground(TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);

        panel.add(Box.createVerticalStrut(8));

        JLabel tagline = new JLabel("Müziğin ritmini hisset.");
        tagline.setForeground(new Color(0xC4B5FD));
        tagline.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(tagline);

        panel.add(Box.createVerticalStrut(48));

        // Ozellikler — sade ve temiz
        String[][] features = {
            {"♫", "Kendi çalma listeni oluştur"},
            {"♥", "Beğendiğin şarkıları kaydet"},
            {"▶", "Kolayca keşfet, anında çal"},
        };

        for (String[] f : features) {
            panel.add(featureBullet(f[0], f[1]));
            panel.add(Box.createVerticalStrut(18));
        }

        panel.add(Box.createVerticalGlue());

        // Alt dipnot — sade
        JLabel version = new JLabel("Bittune");
        version.setForeground(new Color(0x3D4A60));
        version.setFont(FONT_SMALL);
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(version);

        return panel;
    }

    private JPanel featureBullet(String icon, String text) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(278, 36));

        // Kucuk soft ikon balonu
        JPanel iconBubble = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(124, 58, 237, 55));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBubble.setOpaque(false);
        iconBubble.setPreferredSize(new Dimension(32, 32));
        iconBubble.setMaximumSize(new Dimension(32, 32));
        iconBubble.setMinimumSize(new Dimension(32, 32));
        iconBubble.setLayout(new GridBagLayout());

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setForeground(PURPLE_GLOW);
        iconLbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        iconBubble.add(iconLbl);

        JLabel textLbl = new JLabel(text);
        textLbl.setForeground(new Color(0xCDD5E0));
        textLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textLbl.setBorder(new EmptyBorder(0, 12, 0, 0));

        row.add(iconBubble);
        row.add(textLbl);
        return row;
    }

    // ============================================================
    // SAG PANEL — CardLayout: login / register
    // ============================================================
    private JPanel buildRightPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                // Arkaplana cok hafif bir merkez parlama
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RadialGradientPaint r = new RadialGradientPaint(
                    new Point(getWidth() / 2, getHeight() / 2), 260,
                    new float[]{0f, 1f},
                    new Color[]{new Color(124, 58, 237, 20), new Color(16, 19, 30, 0)}
                );
                g2.setPaint(r);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        wrapper.setOpaque(false);

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout) {
            @Override protected void paintComponent(Graphics g) {
                // Yumusak cam kart efekti
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(14, 21, 34, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(new Color(61, 78, 106, 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                g2.dispose();
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setPreferredSize(new Dimension(430, 490));

        cardPanel.add(buildLoginCard(),    "LOGIN");
        cardPanel.add(buildRegisterCard(), "REGISTER");
        cardLayout.show(cardPanel, "LOGIN");

        // Kapat dugmesi — sag ust kose
        JButton closeBtn = new JButton("✕") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(0x2D1B4E));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        closeBtn.setForeground(TEXT_MUTED);
        closeBtn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> System.exit(0));
        closeBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { closeBtn.setForeground(TEXT); closeBtn.repaint(); }
            public void mouseExited(MouseEvent e)  { closeBtn.setForeground(TEXT_MUTED); closeBtn.repaint(); }
        });

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 10));
        topBar.setOpaque(false);
        topBar.add(closeBtn);

        JPanel rightInner = new JPanel(new BorderLayout());
        rightInner.setOpaque(false);
        rightInner.add(topBar, BorderLayout.NORTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(cardPanel, gbc);

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.add(topBar, BorderLayout.NORTH);
        right.add(wrapper, BorderLayout.CENTER);
        return right;
    }

    // ---------- Login karti ----------
    private JPanel buildLoginCard() {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Baslik
        JLabel heading = new JLabel("Tekrar Hoşgeldin!");
        heading.setForeground(TEXT);
        heading.setFont(FONT_TITLE);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(heading);

        card.add(Box.createVerticalStrut(6));

        JLabel sub = new JLabel("Hesabınla giriş yap.");
        sub.setForeground(TEXT_MUTED);
        sub.setFont(FONT_BODY);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(sub);

        card.add(Box.createVerticalStrut(32));

        // Kullanici adi
        card.add(fieldLabel("Kullanıcı Adı"));
        card.add(Box.createVerticalStrut(6));
        loginUserField = styledField("kullanıcı adını gir");
        card.add(loginUserField);

        card.add(Box.createVerticalStrut(16));

        // Sifre
        card.add(fieldLabel("Şifre"));
        card.add(Box.createVerticalStrut(6));
        loginPassField = styledPasswordField("••••••••");
        card.add(loginPassField);

        card.add(Box.createVerticalStrut(10));

        // Hata etiketi
        loginErrorLabel = errorLabel();
        card.add(loginErrorLabel);

        card.add(Box.createVerticalStrut(10));

        // Oturumu Acik Tut kutusu
        rememberMeCheck = buildCheckBox("Oturumu açık tut");
        card.add(rememberMeCheck);

        card.add(Box.createVerticalStrut(14));

        // Giris dugmesi
        JButton loginBtn = buildPrimaryButton("Giriş Yap");
        card.add(loginBtn);
        loginBtn.addActionListener(e -> doLogin());

        // Enter tusuna da bagla
        ActionListener enterLogin = e -> doLogin();
        loginUserField.addActionListener(enterLogin);
        loginPassField.addActionListener(enterLogin);

        card.add(Box.createVerticalStrut(20));

        // Kayit linki
        JPanel switchRow = buildSwitchRow(
            "Hesabın yok mu?", " Kayıt Ol", () -> switchCard("REGISTER")
        );
        card.add(switchRow);

        return card;
    }

    // ---------- Register karti ----------
    private JPanel buildRegisterCard() {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Hesap Oluştur");
        heading.setForeground(TEXT);
        heading.setFont(FONT_TITLE);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(heading);

        card.add(Box.createVerticalStrut(6));

        JLabel sub = new JLabel("Bittune'a katıl, müziği keşfet.");
        sub.setForeground(TEXT_MUTED);
        sub.setFont(FONT_BODY);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(sub);

        card.add(Box.createVerticalStrut(28));

        // Kullanici adi
        card.add(fieldLabel("Kullanıcı Adı"));
        card.add(Box.createVerticalStrut(6));
        regUserField = styledField("bir kullanıcı adı seç");
        card.add(regUserField);

        card.add(Box.createVerticalStrut(14));

        // Sifre
        card.add(fieldLabel("Şifre"));
        card.add(Box.createVerticalStrut(6));
        regPassField = styledPasswordField("en az 4 karakter");
        card.add(regPassField);

        card.add(Box.createVerticalStrut(14));

        // Sifre onayi
        card.add(fieldLabel("Şifreyi Onayla"));
        card.add(Box.createVerticalStrut(6));
        regConfirmField = styledPasswordField("şifreyi tekrar gir");
        card.add(regConfirmField);

        card.add(Box.createVerticalStrut(8));

        // Hata & basari etiketleri
        regErrorLabel   = errorLabel();
        regSuccessLabel = successLabel();
        card.add(regErrorLabel);
        card.add(regSuccessLabel);

        card.add(Box.createVerticalStrut(18));

        JButton regBtn = buildPrimaryButton("Kayıt Ol");
        card.add(regBtn);
        regBtn.addActionListener(e -> doRegister());

        ActionListener enterReg = e -> doRegister();
        regUserField.addActionListener(enterReg);
        regPassField.addActionListener(enterReg);
        regConfirmField.addActionListener(enterReg);

        card.add(Box.createVerticalStrut(18));

        JPanel switchRow = buildSwitchRow(
            "Zaten hesabın var mı?", " Giriş Yap", () -> switchCard("LOGIN")
        );
        card.add(switchRow);

        return card;
    }

    // ============================================================
    // Is mantigi — DSA metodlari
    // ============================================================

    /** LinkedList store uzerinden kimlik dogrulama */
    private void doLogin() {
        String username = loginUserField.getText().trim();
        String password = new String(loginPassField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            setError(loginErrorLabel, "Kullanıcı adı ve şifre boş bırakılamaz.");
            return;
        }

        // O(n) dogrusal arama — LinkedList traverse
        BittuneUser user = userStore.authenticate(username, password);
        if (user == null) {
            setError(loginErrorLabel, "Kullanıcı adı veya şifre yanlış.");
            shakeField(loginPassField);
            return;
        }

        // DSA: Stack'e push — giris gecmisine ekle
        loginHistory.push(user.username);

        // "Oturumu Acik Tut" — session dosyasini yaz veya sil
        if (rememberMeCheck != null && rememberMeCheck.isSelected()) {
            BittuneStorage.saveSession(user.username);
        } else {
            BittuneStorage.clearSession();
        }

        // Giris basarili — ana uygulamayi ac
        dispose();
        SwingUtilities.invokeLater(() -> new BittuneGUI(user.username, userStore, loginHistory));
    }

    /** LinkedList store'a yeni kullanici ekle; uniqueness O(n) ile kontrol edilir */
    private void doRegister() {
        String username = regUserField.getText().trim();
        String password = new String(regPassField.getPassword());
        String confirm  = new String(regConfirmField.getPassword());

        // Bos alan kontrolu
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            setError(regErrorLabel, "Tüm alanlar doldurulmalıdır.");
            clearSuccess(regSuccessLabel);
            return;
        }

        // Kullanici adi uzunluk
        if (username.length() < 3) {
            setError(regErrorLabel, "Kullanıcı adı en az 3 karakter olmalı.");
            clearSuccess(regSuccessLabel);
            shakeField(regUserField);
            return;
        }

        // Sifre uzunluk
        if (password.length() < 4) {
            setError(regErrorLabel, "Şifre en az 4 karakter olmalı.");
            clearSuccess(regSuccessLabel);
            shakeField(regPassField);
            return;
        }

        // Sifre eslesmesi
        if (!password.equals(confirm)) {
            setError(regErrorLabel, "Şifreler eşleşmiyor.");
            clearSuccess(regSuccessLabel);
            shakeField(regConfirmField);
            return;
        }

        // DSA: LinkedList'e ekle — varlik kontrolu O(n)
        boolean registered = userStore.register(username, password);
        if (!registered) {
            setError(regErrorLabel, "\"" + username + "\" kullanıcı adı zaten alınmış.");
            clearSuccess(regSuccessLabel);
            shakeField(regUserField);
            return;
        }

        // Basarili kayit — hesaplari diske yaz
        BittuneStorage.saveAccounts(userStore);
        clearError(regErrorLabel);
        setSuccess(regSuccessLabel, "✔ Hesap oluşturuldu! Giriş yapabilirsin.");

        // Kisa sure sonra login ekranina gec
        Timer t = new Timer(1400, ev -> {
            // Login alanini doldur
            loginUserField.setText(username);
            loginPassField.setText("");
            clearError(loginErrorLabel);
            switchCard("LOGIN");
        });
        t.setRepeats(false);
        t.start();
    }

    // ============================================================
    // UI yardimci metodlar
    // ============================================================

    private void switchCard(String name) {
        cardLayout.show(cardPanel, name);
        // Hatalari temizle
        if ("LOGIN".equals(name)) {
            clearError(loginErrorLabel);
        } else {
            clearError(regErrorLabel);
            clearSuccess(regSuccessLabel);
            regUserField.setText("");
            regPassField.setText("");
            regConfirmField.setText("");
        }
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(FONT_SMALL);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(360, 18));
        return lbl;
    }

    private JTextField styledField(String placeholder) {
        JTextField field = new JTextField(placeholder) {
            private boolean showPlaceholder = true;
            {
                setForeground(TEXT_MUTED);
                addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) {
                        if (showPlaceholder) { setText(""); setForeground(TEXT); showPlaceholder = false; }
                    }
                    public void focusLost(FocusEvent e) {
                        if (getText().isEmpty()) { setText(placeholder); setForeground(TEXT_MUTED); showPlaceholder = true; }
                    }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FIELD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (isFocusOwner()) {
                    g2.setColor(new Color(0x9D6FEF));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
                } else {
                    g2.setColor(new Color(0x2A3550));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(10, 14, 10, 14));
        field.setBackground(FIELD_BG);
        field.setCaretColor(PURPLE_LIGHT);
        field.setFont(FONT_BODY);
        field.setMaximumSize(new Dimension(360, 44));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        return field;
    }

    private JPasswordField styledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            private boolean showPlaceholder = true;
            {
                setEchoChar((char) 0);
                setText(placeholder);
                setForeground(TEXT_MUTED);
                addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) {
                        if (showPlaceholder) { setText(""); setForeground(TEXT); setEchoChar('•'); showPlaceholder = false; }
                    }
                    public void focusLost(FocusEvent e) {
                        if (getPassword().length == 0) { setEchoChar((char) 0); setText(placeholder); setForeground(TEXT_MUTED); showPlaceholder = true; }
                    }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FIELD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (isFocusOwner()) {
                    g2.setColor(new Color(0x9D6FEF));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
                } else {
                    g2.setColor(new Color(0x2A3550));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(10, 14, 10, 14));
        field.setBackground(FIELD_BG);
        field.setCaretColor(PURPLE_LIGHT);
        field.setFont(FONT_BODY);
        field.setMaximumSize(new Dimension(360, 44));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        return field;
    }

    /** Premium stil kutucuk — mor tick, koyu arka plan */
    private JCheckBox buildCheckBox(String label) {
        JCheckBox cb = new JCheckBox(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Kutucuk arka plani
                int bx = 0, by = (getHeight() - 16) / 2;
                g2.setColor(isSelected() ? PURPLE : FIELD_BG);
                g2.fillRoundRect(bx, by, 16, 16, 5, 5);
                g2.setColor(isSelected() ? PURPLE_LIGHT : BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(bx, by, 16, 16, 5, 5);
                // Tick isaretini ciz
                if (isSelected()) {
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(bx + 3, by + 8, bx + 6, by + 12);
                    g2.drawLine(bx + 6, by + 12, bx + 13, by + 4);
                }
                // Metin
                g2.setColor(TEXT_MUTED);
                g2.setFont(FONT_SMALL);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), bx + 22, by + fm.getAscent() + (16 - fm.getHeight()) / 2);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() {
                return new Dimension(200, 22);
            }
        };
        cb.setOpaque(false);
        cb.setBorderPainted(false);
        cb.setContentAreaFilled(false);
        cb.setFocusPainted(false);
        cb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cb.setAlignmentX(Component.CENTER_ALIGNMENT);
        cb.setMaximumSize(new Dimension(360, 24));
        return cb;
    }

    private JButton buildPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Yumusak mor gradyan — hover'da hafif aydinlanir
                Color c1 = getModel().isPressed()  ? new Color(0x5B21B6) :
                           getModel().isRollover()  ? new Color(0x8B5CF6) : new Color(0x7C3AED);
                Color c2 = getModel().isPressed()  ? new Color(0x7E22CE) :
                           getModel().isRollover()  ? new Color(0xA78BFA) : new Color(0x9D6FEF);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), 0, c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Yumusak hover kenari
                if (getModel().isRollover()) {
                    g2.setColor(new Color(196, 181, 253, 80));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(360, 46));
        btn.setPreferredSize(new Dimension(360, 46));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private JLabel errorLabel() {
        JLabel lbl = new JLabel(" ");
        lbl.setForeground(ERROR_COLOR);
        lbl.setFont(FONT_SMALL);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(360, 18));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    private JLabel successLabel() {
        JLabel lbl = new JLabel(" ");
        lbl.setForeground(SUCCESS_COLOR);
        lbl.setFont(FONT_SMALL);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(360, 18));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    private JPanel buildSwitchRow(String staticText, String linkText, Runnable action) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(360, 28));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel txt = new JLabel(staticText);
        txt.setForeground(TEXT_MUTED);
        txt.setFont(FONT_LINK);

        JLabel link = new JLabel(linkText);
        link.setForeground(PURPLE_LIGHT);
        link.setFont(new Font("SansSerif", Font.BOLD, 13));
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { action.run(); }
            public void mouseEntered(MouseEvent e) { link.setForeground(PURPLE_GLOW); }
            public void mouseExited(MouseEvent e)  { link.setForeground(PURPLE_LIGHT); }
        });

        row.add(txt);
        row.add(link);
        return row;
    }

    private void setError(JLabel lbl, String msg) {
        lbl.setText(msg);
        lbl.setForeground(ERROR_COLOR);
    }

    private void clearError(JLabel lbl)   { lbl.setText(" "); }
    private void clearSuccess(JLabel lbl) { lbl.setText(" "); }

    private void setSuccess(JLabel lbl, String msg) {
        lbl.setText(msg);
        lbl.setForeground(SUCCESS_COLOR);
    }

    /** Alan sallama animasyonu — hatali giriste kullaniciya geri bildirim */
    private void shakeField(JComponent field) {
        int[] offsets = {0, -8, 8, -6, 6, -4, 4, -2, 2, 0};
        Timer t = new Timer(30, null);
        int[] step = {0};
        Point origin = field.getLocation();
        t.addActionListener(e -> {
            if (step[0] >= offsets.length) {
                field.setLocation(origin);
                t.stop();
                return;
            }
            field.setLocation(origin.x + offsets[step[0]], origin.y);
            step[0]++;
        });
        t.start();
    }

    /** Surukleyerek pencereyi tasi (undecorated frame) */
    private void installDrag(JPanel root) {
        root.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragOrigin = e.getPoint();
            }
        });
        root.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (dragOrigin != null) {
                    Point loc = getLocation();
                    setLocation(
                        loc.x + e.getX() - dragOrigin.x,
                        loc.y + e.getY() - dragOrigin.y
                    );
                }
            }
        });
    }
}
