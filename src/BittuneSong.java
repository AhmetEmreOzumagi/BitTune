import java.awt.Color;
import java.awt.image.BufferedImage;

class BittuneSong {
    final String      title;
    final String      artist;
    final String      album;
    final int         durationSeconds;
    final Color       colorA;
    final Color       colorB;
    final String      filePath;   // MP3 dosya yolu; null = gercek dosya yok
    BufferedImage     coverArt;   // ID3 tag'dan cekilen kapak resmi; null = yok
    int     likes;
    int     playCount;
    boolean liked;
    long    likedAt;

    /** Dosyasiz sarki. */
    BittuneSong(String title, String artist, String album, int durationSeconds,
                 int likes, Color colorA, Color colorB, boolean liked) {
        this(title, artist, album, durationSeconds, likes, colorA, colorB, liked, null);
    }

    /** Gercek MP3 dosyali sarki. */
    BittuneSong(String title, String artist, String album, int durationSeconds,
                 int likes, Color colorA, Color colorB, boolean liked, String filePath) {
        this.title           = title;
        this.artist          = artist;
        this.album           = album;
        this.durationSeconds = durationSeconds;
        this.likes           = likes;
        this.colorA          = colorA;
        this.colorB          = colorB;
        this.liked           = liked;
        this.filePath        = filePath;
    }

    boolean hasAudio()    { return filePath != null && !filePath.isEmpty(); }
    boolean hasCoverArt() { return coverArt != null; }

    String durationText() {
        return durationSeconds / 60 + ":" + String.format("%02d", durationSeconds % 60);
    }
}
