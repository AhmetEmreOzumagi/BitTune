import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

// WAV dosyası çalma işlemlerini yöneten sınıf
public class AudioPlayer {

    private Clip clip;
    private boolean isPlaying;
    private Song currentSong;

    public AudioPlayer() {
        isPlaying = false;
        currentSong = null;
    }

    // Şarkıyı çal (WAV dosyası)
    public void play(Song song) {
        stop(); // Önce mevcut şarkıyı durdur

        if (song.filePath == null || song.filePath.isEmpty()) {
            System.out.println("Now Playing: " + song.title + " - " + song.artist);
            System.out.println("[Gerçek dosya yolu bulunamadı - simülasyon modu]");
            currentSong = song;
            isPlaying = true;
            return;
        }

        File audioFile = new File(song.filePath);
        if (!audioFile.exists()) {
            // Dosya yoksa sadece göster (simülasyon)
            System.out.println("♪ Now Playing: " + song.title + " - " + song.artist);
            System.out.println("  Tür: " + song.genre + " | Süre: " + song.getFormattedDuration());
            currentSong = song;
            isPlaying = true;
            return;
        }

        // Gerçek WAV dosyasını çal
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            currentSong = song;
            isPlaying = true;
            System.out.println("♪ Now Playing: " + song.title + " - " + song.artist);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Ses dosyası çalınamadı: " + e.getMessage());
            System.out.println("♪ Now Playing (simülasyon): " + song.title + " - " + song.artist);
            currentSong = song;
            isPlaying = true;
        }
    }

    // Şarkıyı durdur
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
        isPlaying = false;
    }

    // Duraksat / devam et
    public void pauseOrResume() {
        if (clip == null) {
            System.out.println("Çalan şarkı yok.");
            return;
        }
        if (clip.isRunning()) {
            clip.stop();
            System.out.println("Duraklatıldı.");
        } else {
            clip.start();
            System.out.println("Devam ediyor.");
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void printNowPlaying() {
        if (currentSong != null) {
            System.out.println("\n♪ Şu an çalıyor: " + currentSong.title + " - " + currentSong.artist);
        } else {
            System.out.println("Şu an çalan şarkı yok.");
        }
    }
}
