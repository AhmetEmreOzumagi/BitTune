import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

class RoundedPanel extends JPanel {
    private final Color color;
    private final int radius;

    RoundedPanel(Color color, int radius) {
        this.color = color;
        this.radius = radius;
        setOpaque(false);
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.setColor(new Color(BittuneTheme.BORDER.getRed(), BittuneTheme.BORDER.getGreen(), BittuneTheme.BORDER.getBlue(), 55));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}

class GradientPanel extends JPanel {
    private final Color start;
    private final Color end;
    private final int radius;

    GradientPanel(Color start, Color end, int radius) {
        this.start = start;
        this.end = end;
        this.radius = radius;
        setOpaque(false);
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, start, getWidth(), getHeight(), end));
        if (radius > 0) g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        else g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}

class RoundedButton extends JButton {
    private final Color normal;
    private final Color hover;
    private final int radius;
    private boolean hovered;

    RoundedButton(String text, Color normal, Color hover, int radius) {
        super(text);
        this.normal = normal;
        this.hover = hover;
        this.radius = radius;
        setForeground(BittuneTheme.TEXT);
        // SansSerif → Türkçe karakter desteği (ş,ğ,ü,ö,ç,ı)
        // "Segoe UI Symbol" sadece sembol gliflerine sahiptir, Latin+Türkçe yoktur
        setFont(new Font("SansSerif", Font.BOLD, 14));
        setBorder(new EmptyBorder(0, 14, 0, 14));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(hovered ? hover : normal);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}

class HeartButton extends JButton {
    private boolean liked;
    private boolean hovered;

    HeartButton() {
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    void setLiked(boolean liked) {
        this.liked = liked;
        repaint();
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(hovered ? new Color(0x21172F) : new Color(0x151B25));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

        int w = getWidth();
        int h = getHeight();
        float x = w / 2f;
        float y = h / 2f + 2;
        float size = Math.min(w, h) * 0.54f;

        GeneralPath heart = new GeneralPath();
        heart.moveTo(x, y + size * 0.34f);
        heart.curveTo(x - size * 0.95f, y - size * 0.18f,
                x - size * 0.58f, y - size * 0.82f,
                x - size * 0.12f, y - size * 0.52f);
        heart.curveTo(x, y - size * 0.44f,
                x + size * 0.12f, y - size * 0.52f,
                x + size * 0.12f, y - size * 0.52f);
        heart.curveTo(x + size * 0.58f, y - size * 0.82f,
                x + size * 0.95f, y - size * 0.18f,
                x, y + size * 0.34f);
        heart.closePath();

        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (liked) {
            g2.setPaint(new GradientPaint(0, 0, BittuneTheme.PURPLE_LIGHT,
                    w, h, BittuneTheme.MAGENTA));
            g2.fill(heart);
        } else {
            g2.setColor(new Color(0x7F8CA3));
            g2.draw(heart);
        }
        g2.dispose();
    }
}

class PlayIconButton extends JButton {
    private boolean hovered;

    PlayIconButton() {
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        g2.setPaint(new GradientPaint(0, 0,
                hovered ? BittuneTheme.PURPLE_LIGHT : BittuneTheme.PURPLE,
                w, h, hovered ? BittuneTheme.MAGENTA : new Color(0x4C1D95)));
        g2.fillRoundRect(0, 0, w, h, 18, 18);

        int cx = w / 2 - 1;
        int cy = h / 2;
        int size = Math.min(w, h) / 3;
        Polygon play = new Polygon(
                new int[]{cx - size / 2, cx - size / 2, cx + size},
                new int[]{cy - size, cy + size, cy},
                3
        );
        g2.setColor(Color.WHITE);
        g2.fill(play);

        g2.setColor(new Color(255, 255, 255, 45));
        g2.setStroke(new BasicStroke(1.1f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);
        g2.dispose();
    }
}

class TransportButton extends JButton {
    private final boolean next;
    private boolean hovered;

    TransportButton(boolean next) {
        this.next = next;
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int cy = h / 2;
        g2.setColor(hovered ? new Color(0x232B38) : new Color(0x171D27));
        g2.fillRoundRect(0, 0, w, h, 18, 18);
        g2.setColor(BittuneTheme.TEXT);

        int dir = next ? 1 : -1;
        int centerX = w / 2 + (next ? -2 : 2);
        int triW = 9;
        int triH = 8;

        Polygon first = triangle(centerX - dir * 5, cy, triW, triH, dir);
        Polygon second = triangle(centerX + dir * 4, cy, triW, triH, dir);
        g2.fill(first);
        g2.fill(second);

        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int barX = centerX + dir * 13;
        g2.drawLine(barX, cy - triH, barX, cy + triH);

        g2.setColor(new Color(255, 255, 255, 28));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);
        g2.dispose();
    }

    private Polygon triangle(int x, int y, int width, int height, int dir) {
        if (dir > 0) {
            return new Polygon(
                    new int[]{x - width / 2, x - width / 2, x + width / 2},
                    new int[]{y - height, y + height, y},
                    3
            );
        }
        return new Polygon(
                new int[]{x + width / 2, x + width / 2, x - width / 2},
                new int[]{y - height, y + height, y},
                3
        );
    }
}

class NavItemPanel extends JPanel {
    private final JLabel iconLabel;
    private final JLabel textLabel;
    private boolean selected;
    private boolean hovered;

    NavItemPanel(String icon, String text, boolean selected) {
        this.selected = selected;
        setOpaque(false);
        setLayout(new BorderLayout(12, 0));
        iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18));
        textLabel = new JLabel(text);
        textLabel.setFont(BittuneTheme.FONT_BODY);
        add(iconLabel, BorderLayout.WEST);
        add(textLabel, BorderLayout.CENTER);
        refreshColors();
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    void setSelected(boolean selected) {
        this.selected = selected;
        refreshColors();
        repaint();
    }

    private void refreshColors() {
        Color c = selected ? BittuneTheme.TEXT : BittuneTheme.TEXT_MUTED;
        iconLabel.setForeground(c);
        textLabel.setForeground(c);
        textLabel.setFont(selected ? BittuneTheme.FONT_BOLD : BittuneTheme.FONT_BODY);
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (selected) {
            // Çok hafif mor bg tonu
            g2.setColor(new Color(109, 40, 217, 32));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            // Sol kenar aksanı — pill şekli
            g2.setPaint(new GradientPaint(0, 8, BittuneTheme.PURPLE_LIGHT, 0, getHeight() - 8, BittuneTheme.PURPLE));
            g2.fillRoundRect(0, 9, 3, getHeight() - 18, 3, 3);
        } else if (hovered) {
            g2.setColor(new Color(255, 255, 255, 7));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}

class SpeakerButton extends JButton {
    private boolean muted;
    private boolean hovered;

    SpeakerButton() {
        setPreferredSize(new Dimension(38, 38));
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    void setMuted(boolean muted) {
        this.muted = muted;
        repaint();
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(hovered ? new Color(0x241B38) : new Color(0x171D27));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
        g2.setColor(muted ? BittuneTheme.MAGENTA : BittuneTheme.TEXT_MUTED);
        int cx = 11;
        int cy = getHeight() / 2;
        Polygon speaker = new Polygon(
                new int[]{cx - 3, cx + 3, cx + 10, cx + 10, cx + 3, cx - 3},
                new int[]{cy - 5, cy - 5, cy - 11, cy + 11, cy + 5, cy + 5},
                6
        );
        g2.fill(speaker);
        g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (muted) {
            g2.drawLine(25, 14, 31, 24);
            g2.drawLine(31, 14, 25, 24);
        } else {
            g2.drawArc(20, 12, 9, 14, -45, 90);
            g2.drawArc(22, 9, 12, 20, -45, 90);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}

class DialogCloseButton extends JButton {
    private boolean hovered;

    DialogCloseButton() {
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(hovered ? new Color(0x2A2138) : new Color(0, 0, 0, 0));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
        g2.setColor(BittuneTheme.TEXT_MUTED);
        g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        g2.drawLine(cx - 5, cy - 5, cx + 5, cy + 5);
        g2.drawLine(cx + 5, cy - 5, cx - 5, cy + 5);
        g2.dispose();
        super.paintComponent(g);
    }
}

class AlbumArtPanel extends JPanel {
    private BittuneSong song;
    private final int radius;
    private final boolean large;

    AlbumArtPanel(BittuneSong song, int radius, boolean large) {
        this.song = song;
        this.radius = radius;
        this.large = large;
        setOpaque(false);
    }

    void setSong(BittuneSong song) {
        this.song = song;
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        Color a = song != null ? song.colorA : BittuneTheme.PURPLE;
        Color b = song != null ? song.colorB : BittuneTheme.MAGENTA;
        int w = getWidth(), h = getHeight();
        Shape clip = new RoundRectangle2D.Float(0, 0, w, h, radius, radius);
        g2.setClip(clip);

        // Arka plan gradyanı
        g2.setPaint(new GradientPaint(0, 0, a, w, h, b));
        g2.fillRect(0, 0, w, h);

        // Şarkıya özgü geometrik desen (hash tabanlı)
        int style = song == null ? 0 : Math.abs(song.title.hashCode()) % 6;
        paintArtStyle(g2, style, w, h, a, b);

        // Parlak üst-sol kenar vurgusu
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
        g2.setPaint(new GradientPaint(0, 0, Color.WHITE, w * 0.6f, h * 0.6f, new Color(0, 0, 0, 0)));
        g2.fillRect(0, 0, w, h);

        // Çerçeve parıltısı
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(new Color(255, 255, 255, 45));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);
        g2.dispose();
    }

    private void paintArtStyle(Graphics2D g2, int style, int w, int h, Color a, Color b) {
        switch (style) {
            case 0 -> { // Eşmerkezli halkalar
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.22f));
                g2.setStroke(new BasicStroke(large ? 3f : 2f));
                g2.setColor(Color.WHITE);
                int steps = large ? 5 : 3;
                for (int i = 1; i <= steps; i++) {
                    int r2 = (int)(w * 0.15f * i);
                    g2.drawOval(w / 2 - r2, h / 2 - r2, r2 * 2, r2 * 2);
                }
            }
            case 1 -> { // Çapraz şeritler
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(large ? 4f : 2.5f));
                int step = large ? 18 : 12;
                for (int i = -h; i < w + h; i += step * 2) {
                    g2.drawLine(i, 0, i + h, h);
                }
            }
            case 2 -> { // Vinil plak
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.28f));
                g2.setColor(new Color(0, 0, 0));
                g2.fillOval(w / 2 - w / 3, h / 2 - h / 3, w * 2 / 3, h * 2 / 3);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2.setPaint(new GradientPaint(w / 2 - 8, h / 2 - 8, a, w / 2 + 8, h / 2 + 8, b));
                g2.fillOval(w / 2 - (large ? 10 : 6), h / 2 - (large ? 10 : 6),
                        large ? 20 : 12, large ? 20 : 12);
            }
            case 3 -> { // Dalga çizgileri
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.20f));
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(large ? 2f : 1.5f));
                int waves = large ? 6 : 4;
                for (int wi = 0; wi <= waves; wi++) {
                    int y0 = (int)(h * wi / (float) waves);
                    java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
                    path.moveTo(0, y0);
                    int pts = 8;
                    for (int p = 0; p <= pts; p++) {
                        float px = w * p / (float) pts;
                        float py = y0 + (float)(Math.sin(p * Math.PI / 2) * h * 0.07f);
                        path.lineTo(px, py);
                    }
                    g2.draw(path);
                }
            }
            case 4 -> { // Elmaslar / baklava
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(large ? 2f : 1.2f));
                int dSize = large ? 28 : 16;
                for (int dx = -dSize; dx < w + dSize; dx += dSize) {
                    for (int dy = -dSize; dy < h + dSize; dy += dSize) {
                        Polygon diamond = new Polygon(
                            new int[]{dx, dx + dSize / 2, dx + dSize, dx + dSize / 2},
                            new int[]{dy + dSize / 2, dy, dy + dSize / 2, dy + dSize}, 4);
                        g2.draw(diamond);
                    }
                }
            }
            default -> { // Dağınık daireler
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.20f));
                g2.setColor(Color.WHITE);
                java.util.Random rng = new java.util.Random(song != null ? song.title.hashCode() : 42);
                int count = large ? 10 : 5;
                for (int i = 0; i < count; i++) {
                    int cx = rng.nextInt(w), cy = rng.nextInt(h);
                    int cr = (int)(w * (0.08f + rng.nextFloat() * 0.22f));
                    g2.drawOval(cx - cr / 2, cy - cr / 2, cr, cr);
                }
            }
        }
    }

    private static String initials(String title) {
        String[] parts = title.split(" ");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }
}

class SongCardPanel extends JPanel {
    private boolean hovered;

    SongCardPanel(BittuneSong song, Consumer<BittuneSong> playAction) {
        setLayout(new BorderLayout(0, 6));
        setOpaque(false);
        setBorder(new EmptyBorder(7, 7, 7, 7));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // setPreferredSize YOK: GridLayout h\u00FCcre boyutuna s\u0131\u011Fs\u0131n, ta\u015Fmas\u0131n

        // \u2500\u2500 Alb\u00FCm Sanat\u0131 + Play Butonu \u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500
        // JLayeredPane: art altta (DEFAULT_LAYER), play \u00FCstte (PALETTE_LAYER)
        // null layout olmadan bounds-based konumland\u0131rma sa\u011Flar, overflow olmaz
        JLayeredPane artLayer = new JLayeredPane() {
            @Override public Dimension getPreferredSize() { return new Dimension(86, 86); }
            @Override public Dimension getMinimumSize()   { return new Dimension(86, 86); }
            @Override public Dimension getMaximumSize()   { return new Dimension(86, 86); }
        };

        AlbumArtPanel art = new AlbumArtPanel(song, 12, false);
        art.setBounds(0, 0, 86, 86);
        artLayer.add(art, JLayeredPane.DEFAULT_LAYER);

        RoundedButton play = new RoundedButton("\u25B6", BittuneTheme.PURPLE, BittuneTheme.PURPLE_LIGHT, 26);
        play.setBounds(58, 58, 26, 26);
        play.setFont(new Font("SansSerif", Font.BOLD, 11));
        play.addActionListener(e -> playAction.accept(song));
        artLayer.add(play, JLayeredPane.PALETTE_LAYER);

        // Sanat\u0131 yatay ortala (kart geni\u015Fli\u011Fi de\u011Fi\u015Firse kenarlar e\u015Fit kal\u0131r)
        JPanel artCenter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        artCenter.setOpaque(false);
        artCenter.add(artLayer);
        add(artCenter, BorderLayout.CENTER);

        // \u2500\u2500 \u015Eark\u0131 Bilgisi \u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel titleLbl = label(song.title, new Font("SansSerif", Font.BOLD, 11), BittuneTheme.TEXT);
        titleLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
        info.add(titleLbl);
        info.add(Box.createVerticalStrut(2));

        String countText = song.playCount > 0 ? song.playCount + " \u00E7alma" : "Hen\u00FCz \u00E7al\u0131nmad\u0131";
        info.add(label(countText, new Font("SansSerif", Font.PLAIN, 10), BittuneTheme.TEXT_MUTED));
        add(info, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { playAction.accept(song); }
            public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
        });
    }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color bg = hovered ? new Color(0x19253A) : new Color(0x111827);
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
        g2.setColor(hovered ? new Color(0x2E3F58) : new Color(0x1C2A3E));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 14, 14);
        g2.dispose();
        super.paintComponent(g);
    }

    private JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }
}

class PurpleSliderUI extends BasicSliderUI {
    PurpleSliderUI(JSlider slider) {
        super(slider);
    }

    protected Dimension getThumbSize() {
        return new Dimension(15, 15);
    }

    public void paintTrack(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int cy = trackRect.y + trackRect.height / 2 - 3;
        int start = trackRect.x;
        int end = trackRect.x + trackRect.width;
        int fill = xPositionForValue(slider.getValue());
        g2.setColor(new Color(0x2B3140));
        g2.fillRoundRect(start, cy, end - start, 6, 8, 8);
        g2.setPaint(new GradientPaint(start, cy, BittuneTheme.PURPLE, fill, cy, BittuneTheme.PURPLE_LIGHT));
        g2.fillRoundRect(start, cy, Math.max(8, fill - start), 6, 8, 8);
        g2.dispose();
    }

    public void paintThumb(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BittuneTheme.PURPLE_LIGHT);
        g2.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
        g2.setColor(new Color(255, 255, 255, 60));
        g2.drawOval(thumbRect.x, thumbRect.y, thumbRect.width - 1, thumbRect.height - 1);
        g2.dispose();
    }

    public void paintFocus(Graphics g) {
    }

    /** Tıklanan piksel x koordinatını slider değerine çevirir. */
    public int getValueForPosition(int x) {
        return valueForXPosition(x);
    }
}

class DarkScrollBarUI extends BasicScrollBarUI {
    protected void configureScrollBarColors() {
        thumbColor = new Color(0x303747);
        trackColor = BittuneTheme.BG_TABLE;
    }

    protected JButton createDecreaseButton(int orientation) {
        return invisibleButton();
    }

    protected JButton createIncreaseButton(int orientation) {
        return invisibleButton();
    }

    private JButton invisibleButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }
}

// Glassmorphism panel — yarı saydam koyu cam efekti
class GlassPanel extends JPanel {
    private final Color tint;
    private final int radius;
    private final float alpha;

    GlassPanel(Color tint, int radius, float alpha) {
        this.tint = tint;
        this.radius = radius;
        this.alpha = alpha;
        setOpaque(false);
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Ana dolgu
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(tint);
        if (radius > 0) g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        else g2.fillRect(0, 0, getWidth(), getHeight());
        // Üst kısım — hafif parlak cam katmanı
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
        g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight() * 0.5f, new Color(0,0,0,0)));
        g2.fillRect(0, 0, getWidth(), getHeight());
        // Üst mor aksanı çizgisi
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setPaint(new GradientPaint(0, 0, new Color(109, 40, 217, 90),
                                      getWidth() / 2, 0, new Color(139, 92, 246, 30)));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(0, 0, getWidth(), 0);
        g2.dispose();
        super.paintComponent(g);
    }
}

class ToggleSwitch extends JPanel {
    private boolean on;
    private float thumbX;           // animasyon pozisyonu (pixel)
    private float trackAlpha = 0f;  // iz rengi geçiş (0.0 kapalı → 1.0 açık)
    private javax.swing.Timer anim;
    private static final int W = 46, H = 24;

    ToggleSwitch(boolean initialState) {
        this.on     = initialState;
        this.thumbX = initialState ? W - H : 0;
        this.trackAlpha = initialState ? 1f : 0f;
        setPreferredSize(new Dimension(W, H));
        setMinimumSize(new Dimension(W, H));
        setMaximumSize(new Dimension(W, H));
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { toggle(); }
        });
    }

    /** Programatik geçiş (dışarıdan da çağrılabilir). */
    void toggle() {
        on = !on;
        float target = on ? W - H : 0;
        if (anim != null) anim.stop();
        anim = new javax.swing.Timer(12, null);
        anim.addActionListener(ev -> {
            // ease-out: hızlı başla, yavaş bitir
            thumbX  += (target - thumbX)  * 0.28f;
            trackAlpha += ((on ? 1f : 0f) - trackAlpha) * 0.28f;
            if (Math.abs(thumbX - target) < 0.6f) {
                thumbX     = target;
                trackAlpha = on ? 1f : 0f;
                anim.stop();
            }
            repaint();
        });
        anim.start();
    }

    boolean isOn() { return on; }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // İz (track): kapalı = koyu gri, açık = mor — aralarında animasyonlu geçiş
        Color off  = new Color(0x2D3748);
        Color onC  = BittuneTheme.PURPLE_LIGHT;
        int r = (int)(off.getRed()   + (onC.getRed()   - off.getRed())   * trackAlpha);
        int gr= (int)(off.getGreen() + (onC.getGreen() - off.getGreen()) * trackAlpha);
        int b = (int)(off.getBlue()  + (onC.getBlue()  - off.getBlue())  * trackAlpha);
        g2.setColor(new Color(r, gr, b));
        g2.fillRoundRect(0, 0, W, H, H, H);

        // Beyaz yuvarlak düğme — hafif gölge
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillOval((int) thumbX + 4, 4, H - 6, H - 6);
        g2.setColor(Color.WHITE);
        g2.fillOval((int) thumbX + 3, 3, H - 6, H - 6);
        g2.dispose();
    }
}
