import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

class BittuneSongTableModel extends AbstractTableModel {
    // Kolon başlıkları Türkçe, kalp ve menü sütunlarının başlığı boş
    private final String[] columns = {"#", "BAŞLIK", "SANATÇI", "ALBÜM", "SÜRE", ""};
    private final ArrayList<BittuneSong> data;

    BittuneSongTableModel(ArrayList<BittuneSong> data) {
        this.data = data;
    }

    public int getRowCount() { return data.size(); }
    public int getColumnCount() { return columns.length; }
    public String getColumnName(int column) { return columns[column]; }

    public Object getValueAt(int row, int column) {
        BittuneSong song = data.get(row);
        return switch (column) {
            case 0 -> row + 1;
            case 1 -> song.title;
            case 2 -> song.artist;
            case 3 -> song.album;
            case 4 -> song.durationText();
            case 5 -> song;       // BittuneLikeRenderer kullanır
            default -> "";
        };
    }
}

/** Tablonun "hoveredRow" client property'sini okur; yoksa -1 döner */
class TableHoverHelper {
    static int getHoveredRow(JTable table) {
        Object v = table.getClientProperty("hoveredRow");
        if (v instanceof int[] arr && arr.length > 0) return arr[0];
        return -1;
    }
}

interface SongFilter {
    boolean accept(BittuneSong song);
}

// Kalp simgesi — HTML renk etiketi kullanarak garantili görünürlük
class BittuneLikeRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                   boolean focus, int row, int column) {
        BittuneSong song = value instanceof BittuneSong ? (BittuneSong) value : null;
        boolean liked = song != null && song.liked;

        // HTML ile kalp — font bağımsız, her sistemde görünür
        String hexColor = liked ? "#7C3AED" : "#4A5568";
        String html = "<html><div style='text-align:center; color:" + hexColor
                + "; font-size:17pt; font-weight:bold;'>&#x2665;</div></html>";

        super.getTableCellRendererComponent(table, html, selected, focus, row, column);
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(true);
        boolean hovered = row == TableHoverHelper.getHoveredRow(table);
        setBackground(selected  ? BittuneTheme.ROW_SELECTED
                    : hovered   ? BittuneTheme.ROW_HOVER
                    : (row % 2 == 0 ? BittuneTheme.BG_TABLE : new Color(0x0F1620)));
        setBorder(new EmptyBorder(0, 0, 0, 0));
        return this;
    }
}

// ⋮ menü simgesi
class BittuneMenuRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                   boolean focus, int row, int column) {
        super.getTableCellRendererComponent(table, "⋮", selected, focus, row, column);
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(new Font("Segoe UI Symbol", Font.BOLD, 20));
        setForeground(BittuneTheme.TEXT_MUTED);
        setOpaque(true);
        setBackground(selected ? BittuneTheme.ROW_SELECTED
                : (row % 2 == 0 ? BittuneTheme.BG_TABLE : new Color(0x131922)));
        setBorder(new EmptyBorder(0, 0, 0, 0));
        return this;
    }
}
