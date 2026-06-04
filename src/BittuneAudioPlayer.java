import javax.sound.sampled.*;
import java.io.File;
import java.util.function.Consumer;

// ============================================================
// BittuneAudioPlayer — Gercek MP3 Oynatici
// DSA: AudioBufferQueue (dairesel kuyruk) + SeekHistoryStack
// ============================================================
class BittuneAudioPlayer {

    // ── DSA yapilari ──────────────────────────────────────────
    final AudioBufferQueue bufferQueue = new AudioBufferQueue();
    final SeekHistoryStack seekHistory = new SeekHistoryStack();

    // ── Durum ─────────────────────────────────────────────────
    private Thread           playerThread;
    private SourceDataLine   line;
    private volatile boolean paused   = false;
    private volatile boolean stopped  = true;
    private volatile double  seekTo   = -1;    // -1 = bekleyen seek yok
    private volatile float   volume   = 0.8f;  // 0.0 – 1.0

    // Calınan dosya bilgisi
    private String  currentFile;
    private int     durationSeconds;

    // Geri bildirimler (EDT'de cagirilir)
    private Consumer<Double> onProgress;   // 0.0 – 1.0
    private Consumer<String> onError;
    private Runnable         onComplete;

    // ── Genel API ─────────────────────────────────────────────

    /** Yeni sarki oynat. Onceki varsa durdurur. */
    void play(String filePath, int durationSec,
              Consumer<Double> progressCb, Runnable completeCb,
              Consumer<String> errorCb) {
        stop();
        currentFile     = filePath;
        durationSeconds = durationSec;
        onProgress      = progressCb;
        onComplete      = completeCb;
        onError         = errorCb;
        paused          = false;
        stopped         = false;
        seekTo          = -1;
        seekHistory.clear();
        bufferQueue.reset();

        playerThread = new Thread(() -> runPlayer(0.0), "MelodyPlayer");
        playerThread.setDaemon(true);
        playerThread.start();
    }

    /** Oynatmayı duraklat. */
    void pause() {
        paused = true;
        if (line != null && line.isRunning()) line.stop();
    }

    /** Devam et. */
    synchronized void resume() {
        paused = false;
        if (line != null && line.isOpen() && !line.isRunning()) line.start();
        notifyAll();
    }

    /** Tamamen durdur, kaynakları serbest birak. */
    void stop() {
        stopped = true;
        paused  = false;
        bufferQueue.close();
        closeLine();
        if (playerThread != null && playerThread.isAlive()) {
            playerThread.interrupt();
            try { playerThread.join(600); } catch (InterruptedException ignored) {}
        }
        playerThread = null;
    }

    /**
     * Belirtilen orana atla (0.0 = bas, 1.0 = son).
     * SeekHistoryStack'e kaydedilir → seekBack() ile geri alinabilir.
     */
    void seek(double ratio) {
        ratio = Math.max(0.0, Math.min(1.0, ratio));
        seekHistory.push(ratio);
        seekTo = ratio;
        // Duraklatilmissa uyandır
        if (paused) { paused = false; }
        synchronized (this) { notifyAll(); }
    }

    /** DSA: Stack'ten onceki konuma don. */
    void seekBack() {
        seekHistory.pop();                    // su anki konumu cikar
        Double prev = seekHistory.peek();
        if (prev != null) {
            seekTo = prev;
            if (paused) { paused = false; }
            synchronized (this) { notifyAll(); }
        }
    }

    /** Ses seviyesini ayarla (0 – 100). */
    void setVolume(int vol0to100) {
        volume = vol0to100 / 100f;
        applyVolume();
    }

    boolean isPaused()  { return paused; }
    boolean isStopped() { return stopped; }

    // ── Ic oynatici dongusu ───────────────────────────────────

    private void runPlayer(double startRatio) {
        while (!stopped) {
            try {
                double next = playFromRatio(startRatio);
                if (next < 0) {
                    // Dogal tamamlandi
                    if (!stopped && onComplete != null)
                        javax.swing.SwingUtilities.invokeLater(onComplete);
                    break;
                }
                startRatio = next;   // seek talep edildi — yeni konumdan basla
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // Dosya bulunamadı veya codec hatası
                System.err.println("[BittuneAudioPlayer] " + e.getMessage());
                if (!stopped && onError != null) {
                    String message = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
                    javax.swing.SwingUtilities.invokeLater(() -> onError.accept(message));
                }
                break;
            }
        }
    }

    /**
     * startRatio'dan baslayarak oynatir.
     * @return  seek istegi gelirse yeni ratio; tamamlandiysa -1.
     */
    private double playFromRatio(double startRatio) throws Exception {
        File audioFile = new File(currentFile);
        if (!audioFile.exists()) {
            System.err.println("[BittuneAudioPlayer] Dosya bulunamadi: " + currentFile);
            return -1;
        }

        // MP3 akisini ac
        AudioInputStream mp3In  = AudioSystem.getAudioInputStream(audioFile);
        AudioFormat      base   = mp3In.getFormat();

        // PCM formatina donustur (16-bit signed, little-endian)
        AudioFormat pcm = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            base.getSampleRate(), 16,
            base.getChannels(), base.getChannels() * 2,
            base.getSampleRate(), false
        );
        AudioInputStream pcmIn = AudioSystem.getAudioInputStream(pcm, mp3In);

        // Toplam frame tahmini (durationSeconds ile)
        long frameRate   = (long) pcm.getFrameRate();
        long totalFrames = frameRate * durationSeconds;
        int  frameSize   = pcm.getFrameSize();

        // Baslangic konumuna atla (oku ve at — seek)
        long framesPlayed = 0;
        if (startRatio > 0.001) {
            long targetFrame  = (long)(startRatio * totalFrames);
            byte[] skipBuf    = new byte[4096];
            long   skipped    = 0;
            long   skipBytes  = targetFrame * frameSize;
            while (skipped < skipBytes && !stopped) {
                int toRead = (int) Math.min(skipBuf.length, skipBytes - skipped);
                int n = pcmIn.read(skipBuf, 0, toRead);
                if (n < 0) break;
                skipped += n;
            }
            framesPlayed = targetFrame;
        }

        // SourceDataLine ac
        closeLine();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, pcm);
        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(pcm, 8192);
        line.start();
        applyVolume();
        seekTo = -1;

        byte[] buf = new byte[4096];
        int    n;

        while (!stopped && (n = pcmIn.read(buf, 0, buf.length)) != -1) {

            // Duraklatma bekle
            while (paused && !stopped && seekTo < 0) {
                synchronized (this) { try { wait(50); } catch (InterruptedException ie) { break; } }
            }
            if (stopped) break;

            // Seek istegi?
            double pending = seekTo;
            if (pending >= 0) {
                seekTo = -1;
                pcmIn.close(); mp3In.close();
                return pending;   // yeni konumdan yeniden basla
            }

            line.write(buf, 0, n);
            framesPlayed += (long) n / frameSize;

            // Ilerleme bildirimi
            if (totalFrames > 0 && onProgress != null) {
                double progress = Math.min(1.0, (double) framesPlayed / totalFrames);
                javax.swing.SwingUtilities.invokeLater(() -> onProgress.accept(progress));
            }
        }

        pcmIn.close();
        mp3In.close();
        if (line != null) { line.drain(); }
        closeLine();
        return -1;
    }

    // ── Yardimci metodlar ─────────────────────────────────────

    private void closeLine() {
        if (line != null) {
            try { if (line.isOpen()) { line.flush(); line.close(); } } catch (Exception ignored) {}
            line = null;
        }
    }

    private void applyVolume() {
        if (line == null || !line.isOpen()) return;
        try {
            if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = volume <= 0f
                    ? gain.getMinimum()
                    : 20f * (float) Math.log10(volume);
                dB = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB));
                gain.setValue(dB);
            }
        } catch (Exception ignored) {}
    }
}
