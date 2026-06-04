import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

// ============================================================
// BittuneStorage — Kalici veri katmani
//
// Kaydedilen veriler  (proje dizini altinda  data/  klasoru):
//   data/accounts.properties  — kayitli kullanici adi + sifre
//   data/{username}.properties — o kullanicinin begeni + playlist
//   data/session.txt          — "oturumu acik tut" icin kullanici adi
//
// Tum dosyalar UTF-8 kodlamasinda tutulur.
// Playlist adiyla kosulsuz guvenli depolama icin indeks tabanli
// anahtar formati kullanilir ("playlist.0.name", "playlist.0.songs").
// Song basliklarinda "|" ayirici kullanilir; "|" iceren basliklar
// ic duzeltmeden dolayi yuklenirken atlanir.
// ============================================================
class BittuneStorage {

    private static final String DATA_DIR     = "data";
    private static final String ACCOUNTS_FILE = DATA_DIR + File.separator + "accounts.properties";
    private static final String SESSION_FILE  = DATA_DIR + File.separator + "session.txt";
    private static final String SEP           = "|";   // deger ayirici

    // ── Hesap kaliciligi ─────────────────────────────────────────────────

    /**
     * Storedaki tum kullanicilari accounts.properties'e yazar.
     * Yeni bir kullanici kaydolunca cagrilmali.
     */
    static void saveAccounts(BittuneUserStore store) {
        ensureDir();
        Properties p = new Properties();
        for (BittuneUser u : store.allUsers())
            p.setProperty(u.username, u.password);
        write(ACCOUNTS_FILE, p);
    }

    /**
     * accounts.properties'teki kullanicilari store'a yukler.
     * Zaten var olan kullanicilari atlar (register false doner, sessizce devam eder).
     */
    static void loadAccounts(BittuneUserStore store) {
        Properties p = read(ACCOUNTS_FILE);
        if (p == null) return;
        for (String key : p.stringPropertyNames())
            store.register(key, p.getProperty(key));   // tekrar kayit cagrilirsa false donup atlar
    }

    // ── Kullanici verisi ─────────────────────────────────────────────────

    /**
     * Kullanicinin begeni listesini, playlistlerini ve play sayaçlarını diske yazar.
     * toggleLike(), playlist degisiklikleri ve uygulama kapanisinda cagrilmali.
     *
     * @param user      Begeni bilgileri (likedTitles)
     * @param playlists Playlist DSA yapisi (sarkiler buradan alinir)
     * @param allSongs  Play sayaçlarını kaydetmek için tüm şarkı listesi (null → sayaç atlanır)
     */
    static void saveUser(BittuneUser user, BittunePlaylistLinkedList playlists,
                         ArrayList<BittuneSong> allSongs) {
        ensureDir();
        Properties p = new Properties();

        // Begeniler
        p.setProperty("liked", join(user.likedTitles));

        // Playlistler — indeks tabanli: playlist.0.name, playlist.0.songs ...
        ArrayList<String> names = playlists.toArrayList();
        p.setProperty("playlist.count", String.valueOf(names.size()));
        for (int i = 0; i < names.size(); i++) {
            String pl = names.get(i);
            p.setProperty("playlist." + i + ".name", pl);
            ArrayList<String> titles = new ArrayList<>();
            for (BittuneSong s : playlists.songsOf(pl))
                titles.add(s.title);
            p.setProperty("playlist." + i + ".songs", join(titles));
        }

        // Play sayaçları — yalnızca count > 0 olan şarkılar kaydedilir
        if (allSongs != null) {
            int cnt = 0;
            for (BittuneSong s : allSongs) {
                if (s.playCount > 0) {
                    p.setProperty("play." + cnt + ".title", s.title);
                    p.setProperty("play." + cnt + ".count", String.valueOf(s.playCount));
                    cnt++;
                }
            }
            p.setProperty("play.count", String.valueOf(cnt));
        }

        write(userFile(user.username), p);
    }

    /**
     * Geriye dönük uyumluluk — play sayacı olmadan (sadece beğeni + playlist).
     */
    static void saveUser(BittuneUser user, BittunePlaylistLinkedList playlists) {
        saveUser(user, playlists, null);
    }

    /**
     * Kaydedilmis begeni ve playlist bilgilerini bellege yukler.
     * Hem BittuneUser.likedTitles hem de BittunePlaylistLinkedList guncellenir.
     * BittuneGUI kurucusunda loadData() + applyUserLikes()'den once cagrilmali.
     *
     * @param user      Begeni listesinin doldurulacagi kullanici nesnesi
     * @param allSongs  Tum sarki listesi (baslik → nesne eslemesi icin)
     * @param playlists Boş DSA yapisi; playlistler ve sarkiler buraya eklenir
     */
    static void loadInto(BittuneUser user, ArrayList<BittuneSong> allSongs,
                         BittunePlaylistLinkedList playlists) {
        Properties p = read(userFile(user.username));
        if (p == null) return;

        // Begeniler
        user.likedTitles.clear();
        for (String t : split(p.getProperty("liked", "")))
            if (!t.isEmpty()) user.likedTitles.add(t);

        // Playlistler
        int n = parseInt(p.getProperty("playlist.count", "0"));
        for (int i = 0; i < n; i++) {
            String pl = p.getProperty("playlist." + i + ".name", "").trim();
            if (pl.isEmpty()) continue;

            playlists.add(pl);  // zaten varsa false doner, sorun yok

            for (String t : split(p.getProperty("playlist." + i + ".songs", ""))) {
                if (t.isEmpty()) continue;
                for (BittuneSong s : allSongs) {
                    if (s.title.equalsIgnoreCase(t)) {
                        playlists.addSong(pl, s);
                        break;
                    }
                }
            }
        }

        // Play sayaçları — title eşleşmesiyle allSongs'a geri yaz
        int playCnt = parseInt(p.getProperty("play.count", "0"));
        int restoredCnt = 0;
        for (int i = 0; i < playCnt; i++) {
            String title = p.getProperty("play." + i + ".title", "").trim();
            int    count = parseInt(p.getProperty("play." + i + ".count", "0"));
            if (title.isEmpty() || count <= 0) continue;
            for (BittuneSong s : allSongs) {
                if (s.title.equalsIgnoreCase(title)) {
                    s.playCount = count;
                    restoredCnt++;
                    break;
                }
            }
        }

        System.out.printf("[Storage] %s yuklendi: %d begeni, %d playlist, %d play sayaci%n",
            user.username, user.likedTitles.size(), n, restoredCnt);
    }

    // ── Oturum (Remember Me) ─────────────────────────────────────────────

    /** "Oturumu acik tut" kutucugu isaretliyse kullanici adini diske yaz. */
    static void saveSession(String username) {
        ensureDir();
        try {
            Files.writeString(Path.of(SESSION_FILE), username, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("[Storage] Oturum kaydedilemedi: " + e.getMessage());
        }
    }

    /**
     * Kaydedilmis kullanici adini doner; dosya yoksa null.
     * main() baslarken otomatik giris icin kullanilir.
     */
    static String loadSession() {
        try {
            Path path = Path.of(SESSION_FILE);
            if (!Files.exists(path)) return null;
            String u = Files.readString(path, StandardCharsets.UTF_8).trim();
            return u.isEmpty() ? null : u;
        } catch (IOException e) {
            return null;
        }
    }

    /** Kaydedilmis oturumu siler — cikis yapilinca cagrilmali. */
    static void clearSession() {
        try { Files.deleteIfExists(Path.of(SESSION_FILE)); }
        catch (IOException ignored) {}
    }

    // ── Dahili yardimcilar ───────────────────────────────────────────────

    private static String userFile(String username) {
        return DATA_DIR + File.separator + username.toLowerCase() + ".properties";
    }

    private static String join(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return String.join(SEP, list);
    }

    private static String[] split(String s) {
        if (s == null || s.trim().isEmpty()) return new String[0];
        return s.split("\\|", -1);
    }

    private static int parseInt(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private static void write(String path, Properties p) {
        try (Writer w = new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8)) {
            p.store(w, "Bittune Storage — otomatik olusturuldu");
        } catch (IOException e) {
            System.err.println("[Storage] Yazma hatasi (" + path + "): " + e.getMessage());
        }
    }

    private static Properties read(String path) {
        File f = new File(path);
        if (!f.exists()) return null;
        Properties p = new Properties();
        try (Reader r = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
            p.load(r);
            return p;
        } catch (IOException e) {
            System.err.println("[Storage] Okuma hatasi (" + path + "): " + e.getMessage());
            return null;
        }
    }

    private static void ensureDir() {
        new File(DATA_DIR).mkdirs();
    }
}
