import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

// ============================================================
// BittuneLibrary
// audio/ klasorunu tarar. mp3agic jar'i classpath'te varsa ID3
// metadata/kapak okur; yoksa dosya adindan temiz bilgi uretir.
// ============================================================
class BittuneLibrary {

    private static final Color[][] ALBUM_COLORS = {
            {new Color(0x1E0A00), new Color(0xC2410C)},
            {new Color(0x0A001E), new Color(0x7C3AED)},
            {new Color(0x001A0A), new Color(0x059669)},
            {new Color(0x001020), new Color(0x0EA5E9)},
            {new Color(0x1A0010), new Color(0xDB2777)},
            {new Color(0x0D0D00), new Color(0xD97706)}
    };

    static ArrayList<BittuneSong> loadFromDirectory(String audioDir) {
        ArrayList<BittuneSong> result = new ArrayList<>();
        File dir = new File(audioDir);

        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("[BittuneLibrary] Klasor bulunamadi: " + audioDir);
            return result;
        }

        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".mp3"));
        if (files == null || files.length == 0) {
            System.err.println("[BittuneLibrary] Hic MP3 bulunamadi: " + audioDir);
            return result;
        }

        Arrays.sort(files, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        HashMap<String, Color[]> albumColorMap = new HashMap<>();
        int colorIdx = 0;

        for (File file : files) {
            SongMeta meta = readMetadata(file);
            if (meta.title == null || meta.title.isBlank()) {
                applyFileNameFallback(file, meta);
            }
            if (meta.artist == null || meta.artist.isBlank()) meta.artist = inferArtist(file.getName());
            meta.artist = polishArtist(meta.artist);
            if (meta.album == null || meta.album.isBlank()) meta.album = "Local Files";
            meta.title = polishTitleForArtist(meta.title, meta.artist);
            meta.album = polishAlbum(meta.album);

            albumColorMap.putIfAbsent(meta.album, ALBUM_COLORS[colorIdx++ % ALBUM_COLORS.length]);
            Color[] colors = albumColorMap.get(meta.album);

            BittuneSong song = new BittuneSong(
                    meta.title,
                    meta.artist,
                    meta.album,
                    Math.max(1, meta.durationSeconds),
                    0,
                    colors[0],
                    colors[1],
                    false,
                    file.getAbsolutePath()
            );
            song.coverArt = meta.coverArt;
            result.add(song);

            System.out.println("[BittuneLibrary] Yuklendi: " + song.title
                    + " - " + song.artist
                    + " | Album: " + song.album
                    + " | Sure: " + song.durationText()
                    + " | Kapak: " + (song.coverArt != null ? "var" : "yok"));
        }

        System.out.println("[BittuneLibrary] Toplam " + result.size() + " sarki yuklendi.");
        return result;
    }

    private static SongMeta readMetadata(File file) {
        SongMeta meta = new SongMeta();
        meta.durationSeconds = estimateDurationSeconds(file);

        try {
            Class<?> mp3Class = Class.forName("com.mpatric.mp3agic.Mp3File");
            Object mp3 = mp3Class.getConstructor(String.class).newInstance(file.getAbsolutePath());
            meta.durationSeconds = (int) invokeLong(mp3, "getLengthInSeconds", meta.durationSeconds);

            if (invokeBoolean(mp3, "hasId3v2Tag")) {
                Object tag = invokeObject(mp3, "getId3v2Tag");
                readTagFields(tag, meta);
                readCoverArt(tag, meta);
            }
            if (invokeBoolean(mp3, "hasId3v1Tag")) {
                Object tag = invokeObject(mp3, "getId3v1Tag");
                readTagFields(tag, meta);
            }
        } catch (ClassNotFoundException ignored) {
            // mp3agic yoksa dosya adi fallback'i kullanilir.
        } catch (Exception ex) {
            System.err.println("[BittuneLibrary] Metadata okunamadi (" + file.getName() + "): " + ex.getMessage());
        }

        return meta;
    }

    private static void readTagFields(Object tag, SongMeta meta) {
        if (tag == null) return;
        if (meta.title == null) meta.title = clean(invokeString(tag, "getTitle"));
        if (meta.artist == null) meta.artist = clean(invokeString(tag, "getArtist"));
        if (meta.album == null) meta.album = clean(invokeString(tag, "getAlbum"));
    }

    private static void readCoverArt(Object tag, SongMeta meta) {
        if (tag == null) return;
        try {
            Method method = tag.getClass().getMethod("getAlbumImage");
            byte[] artBytes = (byte[]) method.invoke(tag);
            if (artBytes != null && artBytes.length > 0) {
                meta.coverArt = ImageIO.read(new ByteArrayInputStream(artBytes));
            }
        } catch (Exception ignored) {
        }
    }

    private static void applyFileNameFallback(File file, SongMeta meta) {
        String base = stripExtension(file.getName());
        base = base.replace('_', ' ');
        String[] parts = base.split("\\s+-\\s+", 2);
        if (parts.length == 2) {
            meta.artist = clean(parts[0]);
            meta.title = clean(parts[1]);
        } else {
            meta.title = clean(base);
        }
    }

    private static String inferArtist(String fileName) {
        String base = stripExtension(fileName);
        String[] parts = base.split("\\s+-\\s+", 2);
        if (parts.length == 2) return polishArtist(parts[0]);
        if (base.toLowerCase().contains("sagopa")) return "Sagopa Kajmer";
        if (base.toLowerCase().contains("ceza")) return "Ceza";
        return "Bilinmiyor";
    }

    private static String polishTitle(String value) {
        String title = clean(value);
        if (title == null) return "Bilinmeyen Sarki";
        title = stripExtension(title);
        title = title.replace('_', ' ');
        title = title.replaceAll("\\[[^\\]]*]", "");
        title = title.replaceAll("(?i)\\(official video\\)", "");
        title = title.replaceAll("(?i)official video", "");
        title = title.replaceAll("(?i)lyrics?", "");
        title = title.replaceAll("\\s+", " ").trim();
        return title.isEmpty() ? "Bilinmeyen Sarki" : title;
    }

    private static String polishTitleForArtist(String value, String artist) {
        String title = polishTitle(value);
        String cleanArtist = polishArtist(artist);
        String lowerTitle = title.toLowerCase();
        String lowerArtist = cleanArtist.toLowerCase();

        if (lowerTitle.endsWith(" - " + lowerArtist)) {
            title = title.substring(0, title.length() - cleanArtist.length() - 3).trim();
        }

        if (lowerTitle.startsWith(lowerArtist + " - ")) {
            title = title.substring(cleanArtist.length() + 3).trim();
        }

        String[] parts = title.split("\\s+-\\s+");
        if (parts.length >= 2) {
            String left = parts[0].toLowerCase();
            if (left.contains("sagopa") || left.contains("ceza")) {
                title = parts[parts.length - 1].trim();
            }
        }

        return title.isEmpty() ? "Bilinmeyen Sarki" : title;
    }

    private static String polishAlbum(String value) {
        String album = polishTitle(value);
        return album.equals("Bilinmeyen Sarki") ? "Local Files" : album;
    }

    private static String polishArtist(String value) {
        String artist = clean(value);
        if (artist == null) return "Bilinmiyor";
        artist = artist.replace('-', ' ');
        artist = artist.replaceAll("(?i)\\btopic\\b", "");
        artist = artist.replaceAll("\\s+", " ").trim();
        if (artist.equalsIgnoreCase("Sagopa Kajmer")) return "Sagopa Kajmer";
        if (artist.equalsIgnoreCase("Sagopa")) return "Sagopa Kajmer";
        return artist.isEmpty() ? "Bilinmiyor" : artist;
    }

    private static String stripExtension(String value) {
        return value == null ? null : value.replaceAll("(?i)\\.mp3$", "");
    }

    private static int estimateDurationSeconds(File file) {
        long bytes = Math.max(1L, file.length());
        int seconds = (int) Math.round(bytes / 16000.0);
        return Math.max(60, Math.min(480, seconds));
    }

    private static String clean(String value) {
        if (value == null) return null;
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }

    private static String invokeString(Object target, String method) {
        try {
            Object value = target.getClass().getMethod(method).invoke(target);
            return value == null ? null : String.valueOf(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Object invokeObject(Object target, String method) {
        try {
            return target.getClass().getMethod(method).invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static boolean invokeBoolean(Object target, String method) {
        try {
            Object value = target.getClass().getMethod(method).invoke(target);
            return value instanceof Boolean && (Boolean) value;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static long invokeLong(Object target, String method, long fallback) {
        try {
            Object value = target.getClass().getMethod(method).invoke(target);
            return value instanceof Number ? ((Number) value).longValue() : fallback;
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static class SongMeta {
        String title;
        String artist;
        String album;
        int durationSeconds;
        BufferedImage coverArt;
    }
}
